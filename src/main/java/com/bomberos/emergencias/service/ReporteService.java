package com.bomberos.emergencias.service;

import com.bomberos.emergencias.dto.request.ReporteRequestDto;
import com.bomberos.emergencias.dto.response.ReporteResponseDto;
import com.bomberos.emergencias.entity.Reporte;
import com.bomberos.emergencias.entity.Usuario;
import com.bomberos.emergencias.entity.enums.EstadoReporte;
import com.bomberos.emergencias.repository.ReporteRepository;
import com.bomberos.emergencias.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.bomberos.emergencias.entity.AsignacionUnidad;
import com.bomberos.emergencias.entity.Incidente;
import com.bomberos.emergencias.entity.Unidad;
import com.bomberos.emergencias.entity.enums.EstadoAsignacion;
import com.bomberos.emergencias.entity.enums.EstadoIncidente;
import com.bomberos.emergencias.entity.enums.EstadoUnidad;
import com.bomberos.emergencias.repository.AsignacionUnidadRepository;
import com.bomberos.emergencias.repository.IncidenteRepository;
import com.bomberos.emergencias.repository.UnidadRepository;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadRepository unidadRepository;
    private final IncidenteRepository incidenteRepository;
    private final AsignacionUnidadRepository asignacionUnidadRepository;

    @Transactional
    public ReporteResponseDto crearReporte(ReporteRequestDto dto, String emailCiudadano) {
        Usuario usuario = usuarioRepository.findByEmail(emailCiudadano)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Reporte nuevoReporte = Reporte.builder()
                .usuario(usuario)
                .tipoIncidente(dto.getTipoIncidente())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .descripcion(dto.getDescripcion())
                .fotoUrl(dto.getFotoUrl())
                .estado(EstadoReporte.EN_COLA) // Todo reporte nuevo entra como EN_COLA
                // Inteligencia Artificial se añadirán en otra fase
                .build();

        Reporte guardado = reporteRepository.save(nuevoReporte);
        return mapearADto(guardado);
    }

    @Transactional(readOnly = true)
    public List<ReporteResponseDto> listarReportesPorCiudadano(String emailCiudadano) {
        Usuario usuario = usuarioRepository.findByEmail(emailCiudadano)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<Reporte> reportes = reporteRepository.findByUsuarioIdOrderByTimestampDesc(usuario.getId());

        return reportes.stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReporteResponseDto> listarReportesPendientesParaOperador() {
        // Devuelve TODOS los reportes (el filtrado lo hace el frontend)
        List<Reporte> reportes = reporteRepository.findAll(
                org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "timestamp"));

        return reportes.stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReporteResponseDto actualizarReporte(Long id,
            com.bomberos.emergencias.dto.request.ReporteUpdateRequestDto dto) {
        Reporte reporte = reporteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con ID: " + id));

        if (dto.getDescripcion() != null) {
            reporte.setDescripcion(dto.getDescripcion());
        }
        if (dto.getPrioridad() != null) {
            reporte.setPrioridad(dto.getPrioridad());
        }
        if (dto.getMotivoResolucion() != null) {
            reporte.setMotivoResolucion(dto.getMotivoResolucion());
        }
        if (dto.getEstado() != null && !dto.getEstado().isBlank()) {
            try {
                reporte.setEstado(EstadoReporte.valueOf(dto.getEstado().trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Estado inválido: '" + dto.getEstado()
                        + "'. Estados válidos: EN_COLA, ACTIVO, PENDIENTE, RESUELTO, CANCELADO");
            }
        }
        if (dto.getUnidadAsignada() != null && !dto.getUnidadAsignada().isBlank()) {
            Unidad unidad = unidadRepository.findByCodigo(dto.getUnidadAsignada())
                    .orElseThrow(() -> new RuntimeException("Unidad no encontrada: " + dto.getUnidadAsignada()));

            if (unidad.getEstado() != EstadoUnidad.DISPONIBLE) {
                throw new RuntimeException("La unidad " + unidad.getCodigo() + " no está disponible.");
            }

            Incidente incidente = reporte.getIncidente();

            if (incidente == null) {
                incidente = Incidente.builder()
                        .estado(EstadoIncidente.EN_ATENCION)
                        .observaciones("Incidente generado desde el reporte #" + reporte.getId())
                        .build();

                incidente = incidenteRepository.save(incidente);
                reporte.setIncidente(incidente);
            } else {
                incidente.setEstado(EstadoIncidente.EN_ATENCION);
                incidenteRepository.save(incidente);
            }

            boolean yaAsignada = asignacionUnidadRepository.existsByUnidadIdAndEstadoAsignacion(
                    unidad.getId(),
                    EstadoAsignacion.ACTIVA);

            if (yaAsignada) {
                throw new RuntimeException("La unidad " + unidad.getCodigo() + " ya está asignada a otro incidente.");
            }

            AsignacionUnidad asignacion = AsignacionUnidad.builder()
                    .incidente(incidente)
                    .unidad(unidad)
                    .estadoAsignacion(EstadoAsignacion.ACTIVA)
                    .notas("Asignación realizada desde el ticket #" + reporte.getId())
                    .build();

            asignacionUnidadRepository.save(asignacion);

            unidad.setEstado(EstadoUnidad.OCUPADA);
            unidadRepository.save(unidad);
        }
        if (reporte.getEstado() == EstadoReporte.RESUELTO || reporte.getEstado() == EstadoReporte.CANCELADO) {
            Incidente incidente = reporte.getIncidente();

            if (incidente != null) {
                List<AsignacionUnidad> asignacionesActivas = asignacionUnidadRepository
                        .findByIncidenteIdAndEstadoAsignacion(
                                incidente.getId(),
                                EstadoAsignacion.ACTIVA);

                for (AsignacionUnidad asignacion : asignacionesActivas) {
                    asignacion.setEstadoAsignacion(EstadoAsignacion.LIBERADA);
                    asignacion.setFechaLiberacion(LocalDateTime.now());

                    Unidad unidad = asignacion.getUnidad();
                    unidad.setEstado(EstadoUnidad.DISPONIBLE);

                    unidadRepository.save(unidad);
                    asignacionUnidadRepository.save(asignacion);
                }

                incidente.setEstado(EstadoIncidente.CERRADO);
                incidente.setFechaCierre(LocalDateTime.now());
                incidenteRepository.save(incidente);
            }
        }
        Reporte guardado = reporteRepository.save(reporte);
        return mapearADto(guardado);
    }

    private ReporteResponseDto mapearADto(Reporte reporte) {
        return ReporteResponseDto.builder()
                .id(reporte.getId())
                .tipoIncidente(reporte.getTipoIncidente())
                .estado(reporte.getEstado().name())
                .latitud(reporte.getLatitud())
                .longitud(reporte.getLongitud())
                .descripcion(reporte.getDescripcion())
                .prioridad(reporte.getPrioridad())
                .motivoResolucion(reporte.getMotivoResolucion())
                .fotoUrl(reporte.getFotoUrl())
                .timestamp(reporte.getTimestamp())
                .nombreCiudadano(reporte.getUsuario() != null
                        ? reporte.getUsuario().getNombre() + " " + reporte.getUsuario().getApellido()
                        : "No registrado")
                .telefonoCiudadano(reporte.getUsuario() != null ? reporte.getUsuario().getTelefono() : "No registrado")
                .correoCiudadano(reporte.getUsuario() != null ? reporte.getUsuario().getEmail() : "No registrado")
                .idIncidente(reporte.getIncidente() != null ? reporte.getIncidente().getId() : null)
                .build();
    }

    @Transactional
    public void eliminarTodosLosReportes() {
        reporteRepository.deleteAll();
    }
}
