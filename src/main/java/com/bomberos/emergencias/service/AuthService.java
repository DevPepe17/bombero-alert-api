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
import com.bomberos.emergencias.entity.PasswordResetToken;
import com.bomberos.emergencias.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.UUID;

import com.bomberos.emergencias.entity.EmailVerificationToken;
import com.bomberos.emergencias.repository.EmailVerificationTokenRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.password-reset.expiration-minutes}")
    private Long resetExpirationMinutes;

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
                .emailVerificado(false)
                .build();

        usuarioRepository.save(nuevoUsuario);
        enviarCorreoVerificacion(nuevoUsuario);

        return AuthResponse.builder()
                .token(null)
                .email(nuevoUsuario.getEmail())
                .rol(nuevoUsuario.getRol().name())
                .nombreCompleto(nuevoUsuario.getNombre() + " " + nuevoUsuario.getApellido())
                .build();
    }

    public AuthResponse login(AuthLoginRequest request) {
        // Autenticar usuario (valida contra UserDetailsServiceImpl)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (usuario.getEstado() == EstadoUsuario.BANEADO) {
            throw new IllegalStateException("El usuario está baneado del sistema");
        }
        if (usuario.getRol() == Rol.CIUDADANO && Boolean.FALSE.equals(usuario.getEmailVerificado())) {
            throw new IllegalStateException("Debes verificar tu correo antes de iniciar sesión.");
        }

        String jwtToken = jwtUtil.generarToken(usuario);

        return AuthResponse.builder()
                .token(jwtToken)
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .nombreCompleto(usuario.getNombre() + " " + usuario.getApellido())
                .build();
    }

    @Transactional
    public void solicitarRecuperacionPassword(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No existe una cuenta con ese correo"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .usuario(usuario)
                .expiracion(LocalDateTime.now().plusMinutes(resetExpirationMinutes))
                .usado(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        String resetLink = frontendUrl + "/reset-password?token=" + token;

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(usuario.getEmail());
        mensaje.setSubject("Recuperación de contraseña - Emergencias Perú");
        mensaje.setText(
                "Hola " + usuario.getNombre() + ",\n\n" +
                        "Recibimos una solicitud para restablecer tu contraseña.\n\n" +
                        "Ingresa al siguiente enlace para crear una nueva contraseña:\n" +
                        resetLink + "\n\n" +
                        "Este enlace vence en " + resetExpirationMinutes + " minutos.\n\n" +
                        "Si no solicitaste este cambio, ignora este mensaje.\n\n" +
                        "Emergencias Perú");

        mailSender.send(mensaje);
    }

    @Transactional
    public void restablecerPassword(String token, String nuevaPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (resetToken.isUsado()) {
            throw new RuntimeException("Este enlace ya fue utilizado");
        }

        if (resetToken.getExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El enlace de recuperación ha expirado");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));

        resetToken.setUsado(true);

        usuarioRepository.save(usuario);
        passwordResetTokenRepository.save(resetToken);
    }

    @Transactional(readOnly = true)
    public boolean validarTokenRecuperacion(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .map(resetToken -> !resetToken.isUsado()
                        && resetToken.getExpiracion().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    @Transactional
    public void enviarCorreoVerificacion(Usuario usuario) {
        String token = UUID.randomUUID().toString();

        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .usuario(usuario)
                .expiracion(LocalDateTime.now().plusMinutes(5))
                .usado(false)
                .build();

        emailVerificationTokenRepository.save(verificationToken);

        String verificationLink = frontendUrl + "/verify-email?token=" + token;

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(usuario.getEmail());
        mensaje.setSubject("Verifica tu cuenta - Emergencias Perú");
        mensaje.setText(
                "Hola " + usuario.getNombre() + ",\n\n" +
                        "Gracias por registrarte en Emergencias Perú.\n\n" +
                        "Para activar tu cuenta, ingresa al siguiente enlace:\n" +
                        verificationLink + "\n\n" +
                        "Este enlace vence en 5 minutos.\n\n" +
                        "Si no creaste esta cuenta, ignora este mensaje.\n\n" +
                        "Emergencias Perú");

        mailSender.send(mensaje);
    }

    @Transactional
    public void verificarEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token de verificación inválido"));

        if (verificationToken.isUsado()) {
            if (Boolean.TRUE.equals(verificationToken.getUsuario().getEmailVerificado())) {
                return;
            }
            throw new RuntimeException("Este enlace ya fue utilizado");
        }

        if (verificationToken.getExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El enlace de verificación ha expirado");
        }

        Usuario usuario = verificationToken.getUsuario();
        usuario.setEmailVerificado(true);

        verificationToken.setUsado(true);

        usuarioRepository.save(usuario);
        emailVerificationTokenRepository.save(verificationToken);
    }
}
