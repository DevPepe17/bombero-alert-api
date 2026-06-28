package com.bomberos.emergencias.controller;

import com.bomberos.emergencias.dto.request.AuthLoginRequest;
import com.bomberos.emergencias.dto.request.AuthRegisterRequest;
import com.bomberos.emergencias.dto.response.AuthResponse;
import com.bomberos.emergencias.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody AuthRegisterRequest request) {
        return new ResponseEntity<>(authService.registrar(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> solicitarRecuperacion(@RequestParam String email) {
        authService.solicitarRecuperacionPassword(email);
        return ResponseEntity.ok("Se envió un enlace de recuperación al correo indicado.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> restablecerPassword(
            @RequestParam String token,
            @RequestParam String nuevaPassword) {
        authService.restablecerPassword(token, nuevaPassword);
        return ResponseEntity.ok("Contraseña actualizada correctamente.");
    }
}
