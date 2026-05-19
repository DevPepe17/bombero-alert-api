package com.bomberos.emergencias.service;

import com.bomberos.emergencias.dto.request.AuthLoginRequest;
import com.bomberos.emergencias.dto.request.AuthRegisterRequest;
import com.bomberos.emergencias.dto.response.AuthResponse;
import com.bomberos.emergencias.entity.Usuario;
import com.bomberos.emergencias.entity.enums.EstadoUsuario;
import com.bomberos.emergencias.entity.enums.Rol;
import com.bomberos.emergencias.repository.UsuarioRepository;
import com.bomberos.emergencias.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse registrar(AuthRegisterRequest request) {
        // Validar si el correo o DNI ya existen
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        if (usuarioRepository.existsByDni(request.getDni())) {
            throw new IllegalArgumentException("El DNI ya está registrado");
        }

        Usuario nuevoUsuario = Usuario.builder()
                .dni(request.getDni())
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .telefono(request.getTelefono())
                .rol(Rol.CIUDADANO)
                .estado(EstadoUsuario.ACTIVO)
                .build();

        usuarioRepository.save(nuevoUsuario);

        String jwtToken = jwtUtil.generarToken(nuevoUsuario);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(nuevoUsuario.getEmail())
                .rol(nuevoUsuario.getRol().name())
                .nombreCompleto(nuevoUsuario.getNombre() + " " + nuevoUsuario.getApellido())
                .build();
    }

    public AuthResponse login(AuthLoginRequest request) {
        // Autenticar usuario (valida contra UserDetailsServiceImpl)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (usuario.getEstado() == EstadoUsuario.BANEADO) {
            throw new IllegalStateException("El usuario está baneado del sistema");
        }

        String jwtToken = jwtUtil.generarToken(usuario);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .nombreCompleto(usuario.getNombre() + " " + usuario.getApellido())
                .build();
    }
}
