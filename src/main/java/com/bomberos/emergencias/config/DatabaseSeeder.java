package com.bomberos.emergencias.config;

import com.bomberos.emergencias.entity.Estacion;
import com.bomberos.emergencias.entity.Unidad;
import com.bomberos.emergencias.entity.Usuario;
import com.bomberos.emergencias.entity.enums.EstadoUnidad;
import com.bomberos.emergencias.entity.enums.EstadoUsuario;
import com.bomberos.emergencias.entity.enums.Rol;
import com.bomberos.emergencias.repository.EstacionRepository;
import com.bomberos.emergencias.repository.UnidadRepository;
import com.bomberos.emergencias.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Se encarga de inicializar datos básicos en la base de datos si esta se encuentra vacía.
 * Excelente práctica para tener un entorno de pruebas listo al instante.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final EstacionRepository estacionRepository;
    private final UnidadRepository unidadRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Iniciando verificación de datos semilla...");
        crearUsuariosIniciales();
        crearEstacionesYUnidades();
        log.info("Verificación de datos semilla completada.");
    }

    private void crearUsuariosIniciales() {
        if (!usuarioRepository.existsByEmail("admin@bomberos.pe")) {
            Usuario admin = Usuario.builder()
                    .dni("00000000")
                    .nombre("Administrador")
                    .apellido("Sistema")
                    .email("admin@bomberos.pe")
                    .password(passwordEncoder.encode("admin123"))
                    .telefono("999888777")
                    .rol(Rol.ADMINISTRADOR)
                    .estado(EstadoUsuario.ACTIVO)
                    .reputacion(100)
                    .build();
            usuarioRepository.save(admin);
            log.info("Usuario Administrador creado por defecto.");
        }

        if (!usuarioRepository.existsByEmail("operador1@bomberos.pe")) {
            Usuario operador = Usuario.builder()
                    .dni("11111111")
                    .nombre("Operador")
                    .apellido("Central")
                    .email("operador1@bomberos.pe")
                    .password(passwordEncoder.encode("operador123"))
                    .telefono("999111222")
                    .rol(Rol.OPERADOR)
                    .estado(EstadoUsuario.ACTIVO)
                    .reputacion(100)
                    .build();
            usuarioRepository.save(operador);
            log.info("Usuario Operador creado por defecto.");
        }
    }

    private void crearEstacionesYUnidades() {
        if (estacionRepository.count() == 0) {
            // Estación 1
            Estacion roma2 = Estacion.builder()
                    .nombre("Compañía de Bomberos Roma N° 2")
                    .direccion("Av. Abancay, Lima")
                    .latitud(-12.046374)
                    .longitud(-77.029851)
                    .distrito("Cercado de Lima")
                    .telefono("01 428-1122")
                    .build();
            estacionRepository.save(roma2);

            // Unidades para Estación 1
            unidadRepository.save(Unidad.builder().codigo("M-02").tipo("Autobomba").estacion(roma2).estado(EstadoUnidad.DISPONIBLE).build());
            unidadRepository.save(Unidad.builder().codigo("AMB-02").tipo("Ambulancia").estacion(roma2).estado(EstadoUnidad.DISPONIBLE).build());

            // Estación 2
            Estacion miraflores28 = Estacion.builder()
                    .nombre("Compañía de Bomberos Miraflores N° 28")
                    .direccion("Av. Ricardo Palma, Miraflores")
                    .latitud(-12.119426)
                    .longitud(-77.025000)
                    .distrito("Miraflores")
                    .telefono("01 444-2222")
                    .build();
            estacionRepository.save(miraflores28);

            // Unidades para Estación 2
            unidadRepository.save(Unidad.builder().codigo("RES-28").tipo("Unidad de Rescate").estacion(miraflores28).estado(EstadoUnidad.DISPONIBLE).build());
            unidadRepository.save(Unidad.builder().codigo("B-28").tipo("Autobomba").estacion(miraflores28).estado(EstadoUnidad.DISPONIBLE).build());

            log.info("Estaciones y unidades base creadas.");
        }
    }
}
