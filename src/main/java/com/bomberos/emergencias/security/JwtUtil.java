package com.bomberos.emergencias.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utilidad para la creación, validación y parseo de JSON Web Tokens (JWT).
 *
 * Usa JJWT 0.12.x con algoritmo HMAC-SHA256 (HS256).
 * La llave secreta y el tiempo de expiración se inyectan desde application.properties.
 */
@Component
@Slf4j
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private Long expiration;

    // ─── Generación de Token ───────────────────────────────────────────────────

    /**
     * Genera un JWT para el usuario autenticado.
     * Incluye claims adicionales: rol y email.
     */
    public String generarToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Agregar el rol como claim personalizado
        claims.put("roles", userDetails.getAuthorities().stream()
            .map(Object::toString)
            .toList());
        return buildToken(claims, userDetails.getUsername());
    }

    private String buildToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignKey())
            .compact();
    }

    // ─── Validación ────────────────────────────────────────────────────────────

    /**
     * Valida que el token sea válido para el usuario dado.
     * Comprueba: username coincide + token no expirado + firma válida.
     */
    public boolean esTokenValido(String token, UserDetails userDetails) {
        try {
            final String username = extraerUsername(token);
            return username.equals(userDetails.getUsername()) && !estaExpirado(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token JWT inválido: {}", e.getMessage());
            return false;
        }
    }

    // ─── Extracción de Claims ──────────────────────────────────────────────────

    /** Extrae el email (subject) del token */
    public String extraerUsername(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    /** Extrae la fecha de expiración */
    public Date extraerExpiracion(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }

    /** Extrae cualquier claim con una función de resolución */
    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerTodosLosClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSignKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private boolean estaExpirado(String token) {
        return extraerExpiracion(token).before(new Date());
    }

    // ─── Llave Criptográfica ───────────────────────────────────────────────────

    private SecretKey getSignKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
