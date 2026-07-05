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
                }
        }

        private void crearEstacionesYUnidades() {
                Estacion callao1 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos Unión Chalaca N° 1",
                                "Av 2 de Mayo 375",
                                -12.057971545807222,
                                -77.14214622836695,
                                "Callao",
                                "532-0044");
                crearUnidadSiNoExiste("M1-01", "Autobomba", callao1);
                crearUnidadSiNoExiste("RES-1", "Unidad de Rescate", callao1);
                crearUnidadSiNoExiste("AMB-1", "Ambulancia", callao1);
                crearUnidadSiNoExiste("ESC-1", "Unidad Escala", callao1);

                Estacion roma2 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos Roma N° 2",
                                "Jr. Junín 568",
                                -12.047873722939315,
                                -77.0261288891957,
                                "Cercado de Lima",
                                "01 428-1122");
                crearUnidadSiNoExiste("M2-01", "Autobomba", roma2);
                crearUnidadSiNoExiste("AMB2-01", "Ambulancia", roma2);

                Estacion chorrillos6 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos Garibaldi N° 6",
                                "Av. Huaylas No. 298",
                                -12.166954704363667,
                                -77.02614029104379,
                                "Chorrillos",
                                "467-0729 - 252-4761");
                crearUnidadSiNoExiste("M6-01", "Autobomba", chorrillos6);
                crearUnidadSiNoExiste("RESLIG-06", "Rescate Ligera", chorrillos6);
                crearUnidadSiNoExiste("AMB-6", "Ambulancia", chorrillos6);

                Estacion lima10 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos Salvadora Lima N° 10",
                                "Jr. de la Unión 150",
                                -12.051581853409148,
                                -77.03509938937842,
                                "Cercado de Lima",
                                "422-7719");
                crearUnidadSiNoExiste("M10-01", "Autobomba", lima10);
                crearUnidadSiNoExiste("RESLIG-10", "Rescate Ligera", lima10);
                crearUnidadSiNoExiste("RES-10", "Unidad de Rescate", lima10);
                crearUnidadSiNoExiste("AMB-10", "Ambulancia", lima10);

                Estacion olaya13 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos Olaya N° 13",
                                "Malecón Grau 227",
                                -12.164276884386313,
                                -77.02435962513572,
                                "Chorrillos",
                                "467-0638 - 252-4760");
                crearUnidadSiNoExiste("M13-01", "Autobomba", olaya13);
                crearUnidadSiNoExiste("RES-13", "Unidad Rescate", olaya13);
                crearUnidadSiNoExiste("ESC-13", "Unidad Escala", olaya13);

                Estacion internacional14 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos Internacional N° 14",
                                "Jirón Rebeca Oquendo",
                                -12.05922019193437,
                                -77.04312455849319,
                                "Breña",
                                "252-6750");
                crearUnidadSiNoExiste("M14-01", "Autobomba", internacional14);
                crearUnidadSiNoExiste("RES-14", "Unidad Rescate", internacional14);
                crearUnidadSiNoExiste("ESC-14", "Unidad Escala", internacional14);
                crearUnidadSiNoExiste("AUX-14", "Unidad Auxiliar", internacional14);

                Estacion barranco16 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos Barranco N° 16",
                                "Jiron Unión 261",
                                -12.146497706462705,
                                -77.02005027655271,
                                "Barranco",
                                "247-3031");
                crearUnidadSiNoExiste("M16-01", "Autobomba", barranco16);
                crearUnidadSiNoExiste("RES-16", "Unidad Rescate", barranco16);
                crearUnidadSiNoExiste("AMB-16", "Ambulancia", barranco16);
                crearUnidadSiNoExiste("MED-16", "Unidad Médica", barranco16);
                crearUnidadSiNoExiste("AUX-16", "Unidad Auxiliar", barranco16);

                Estacion miraflores28 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos Miraflores N° 28",
                                "Av. Andrés Avelino Cáceres 170",
                                -12.119824809976494,
                                -77.02263183580024,
                                "Miraflores",
                                "445-7447");
                // Compatibilidad con versiones antiguas que tenían el código "B-28" para la
                // unidad principal de Miraflores
                corregirCodigoUnidadSiExiste("B-28", "M28-01");

                crearUnidadSiNoExiste("M28-01", "Autobomba", miraflores28);
                crearUnidadSiNoExiste("RES-28", "Unidad de Rescate", miraflores28);
                crearUnidadSiNoExiste("PLT-28", "Unidad Plataforma", miraflores28);
                crearUnidadSiNoExiste("MED-28", "Unidad Médica", miraflores28);

                Estacion lapunta34 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos La Punta N° 34",
                                "Av. Coronel Bolognesi 137",
                                -12.07205390254538,
                                -77.16462611332804,
                                "Callao",
                                "552-0034");
                crearUnidadSiNoExiste("M34-01", "Autobomba", lapunta34);
                crearUnidadSiNoExiste("RES-34", "Unidad de Rescate", lapunta34);
                crearUnidadSiNoExiste("AMB-34", "Ambulancia", lapunta34);

                Estacion magdalena36 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos Magdalena N° 36",
                                "Av Antonio José de Sucre 899",
                                -12.078820292374603,
                                -77.06510248470144,
                                "Magdalena",
                                "261-7139");
                crearUnidadSiNoExiste("M36-01", "Autobomba", magdalena36);
                crearUnidadSiNoExiste("RES-36", "Unidad de Rescate", magdalena36);
                crearUnidadSiNoExiste("ESC-36", "Unidad Escala", magdalena36);
                crearUnidadSiNoExiste("AUX-36", "Unidad Auxiliar", magdalena36);
                crearUnidadSiNoExiste("AMB-36", "Ambulancia", magdalena36);

                Estacion bellavista60 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos Antonio Alarco Espinosa N° 60",
                                "Jr. los Cóndores 591",
                                -12.059004426264611,
                                -77.08928346767075,
                                "Bellavista",
                                "261-7140");
                crearUnidadSiNoExiste("M60-01", "Autobomba", bellavista60);
                crearUnidadSiNoExiste("RES-60", "Unidad de Rescate", bellavista60);
                crearUnidadSiNoExiste("AUX-60", "Unidad Auxiliar", bellavista60);
                crearUnidadSiNoExiste("AMB-60", "Ambulancia", bellavista60);

                Estacion smp65 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos San Martín de Porres N° 65",
                                "Juan Vicente Nicolini 344",
                                -12.018052482801533,
                                -77.05496853873632,
                                "San Martín de Porres",
                                "534-7725");
                crearUnidadSiNoExiste("M65-01", "Autobomba", smp65);
                crearUnidadSiNoExiste("RES-65", "Unidad de Rescate", smp65);
                crearUnidadSiNoExiste("AMB-65", "Ambulancia", smp65);
                crearUnidadSiNoExiste("ESC-65", "Unidad Escala", smp65);
                crearUnidadSiNoExiste("RESLIG-65", "Rescate Ligera", smp65);

                Estacion carmen115 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos Carmen de la Legua N° 115",
                                "Av. Morales Duárez",
                                -12.036740743343426,
                                -77.08364738418877,
                                "Callao",
                                "320-0115");
                crearUnidadSiNoExiste("M115-01", "Autobomba", carmen115);
                crearUnidadSiNoExiste("RES-115", "Unidad de Rescate", carmen115);
                crearUnidadSiNoExiste("AMB-115", "Ambulancia", carmen115);
                crearUnidadSiNoExiste("ESC-115", "Unidad Escala", carmen115);

                Estacion sjm120 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos San Juan de Miraflores N° 120",
                                "Av. Pedro Miotta Cdra 9 entre los jirones Paita y Sullana, Zona Industrial,",
                                -12.157647124014805,
                                -76.97989187755103,
                                "San Juan de Miraflores",
                                "276-5961");
                crearUnidadSiNoExiste("M120-01", "Autobomba", sjm120);
                crearUnidadSiNoExiste("AMB120-02", "Ambulancia", sjm120);
                crearUnidadSiNoExiste("RES-120", "Unidad de Rescate", sjm120);
                crearUnidadSiNoExiste("ESC-120", "Unidad Escala", sjm120);
                crearUnidadSiNoExiste("AUX-120", "Unidad Auxiliar", sjm120);

                Estacion comas124 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos Comas N° 124",
                                "Martin Aranguri 699",
                                -11.942622249306563,
                                -77.0616547455618,
                                "Comas",
                                " 265-0124");
                crearUnidadSiNoExiste("M124-01", "Autobomba", comas124);
                crearUnidadSiNoExiste("RES-124", "Unidad de Rescate", comas124);
                crearUnidadSiNoExiste("AMB-124", "Ambulancia", comas124);

                Estacion surco134 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos Surco N° 134",
                                "Monte de los Olivos Cdra. 9, Urb. Prolog. Benavides 2da Etapa",
                                -12.143337235221685,
                                -76.98873853152277,
                                "Santiago de Surco",
                                "274-6066");
                crearUnidadSiNoExiste("M134-01", "Autobomba", surco134);
                crearUnidadSiNoExiste("RES-134", "Unidad de Rescate", surco134);
                crearUnidadSiNoExiste("ESC-134", "Unidad Escala", surco134);
                crearUnidadSiNoExiste("AUX-134", "Unidad Auxiliar", surco134);

                Estacion puentePiedra150 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos Puente Piedra N° 150",
                                "Av Jose Galvez Circunvalacion 315",
                                -11.867077500187877,
                                -77.07830817994868,
                                "Puente Piedra",
                                "548-3961");
                crearUnidadSiNoExiste("M150-01", "Autobomba", puentePiedra150);
                crearUnidadSiNoExiste("RES-150", "Unidad de Rescate", puentePiedra150);
                crearUnidadSiNoExiste("CIS-150", "Cisterna", puentePiedra150);
                crearUnidadSiNoExiste("ESC-150", "Unidad Escala", puentePiedra150);

                Estacion olivos161 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos Los Olivos N° 161",
                                "Jirón las Guayabas 15302",
                                -12.007556753252517,
                                -77.07199690693213,
                                "Los Olivos",
                                "298-0161");
                crearUnidadSiNoExiste("M161-01", "Autobomba", olivos161);
                crearUnidadSiNoExiste("RES-161", "Unidad de Rescate", olivos161);
                crearUnidadSiNoExiste("AMB-161", "Ambulancia", olivos161);
                crearUnidadSiNoExiste("ESC-161", "Unidad Escala", olivos161);

                Estacion independencia168 = crearEstacionSiNoExiste(
                                "Compañía de Bomberos Independencia N° 168",
                                "José Marti 122",
                                -11.993634779540415,
                                -77.05546211988238,
                                "Independencia",
                                "244-0168");
                crearUnidadSiNoExiste("M168-01", "Autobomba", independencia168);
                crearUnidadSiNoExiste("RES-168", "Unidad de Rescate", independencia168);
                crearUnidadSiNoExiste("AUX-168", "Unidad Auxiliar", independencia168);
                crearUnidadSiNoExiste("ESC-168", "Unidad Escala", independencia168);

                log.info("Estaciones y unidades verificadas correctamente.");
        }

        private Estacion crearEstacionSiNoExiste(
                        String nombre,
                        String direccion,
                        Double latitud,
                        Double longitud,
                        String distrito,
                        String telefono) {
                return estacionRepository.findByNombreContainingIgnoreCase(nombre)
                                .stream()
                                .findFirst()
                                .orElseGet(() -> {
                                        Estacion estacion = Estacion.builder()
                                                        .nombre(nombre)
                                                        .direccion(direccion)
                                                        .latitud(latitud)
                                                        .longitud(longitud)
                                                        .distrito(distrito)
                                                        .telefono(telefono)
                                                        .build();

                                        return estacionRepository.save(estacion);
                                });
        }

        private void crearUnidadSiNoExiste(String codigo, String tipo, Estacion estacion) {
                if (!unidadRepository.existsByCodigo(codigo)) {
                        unidadRepository.save(Unidad.builder()
                                        .codigo(codigo)
                                        .tipo(tipo)
                                        .estacion(estacion)
                                        .estado(EstadoUnidad.DISPONIBLE)
                                        .build());
                }
        }

        private void corregirCodigoUnidadSiExiste(String codigoActual, String codigoNuevo) {
                unidadRepository.findByCodigo(codigoActual).ifPresent(unidad -> {
                        if (!unidadRepository.existsByCodigo(codigoNuevo)) {
                                unidad.setCodigo(codigoNuevo);
                                unidadRepository.save(unidad);
                        }
                });
        }
}