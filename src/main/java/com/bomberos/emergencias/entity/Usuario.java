package com.bomberos.emergencias.entity;

import com.bomberos.emergencias.entity.enums.EstadoUsuario;
import com.bomberos.emergencias.entity.enums.Rol;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Entidad que representa a un usuario del sistema.
 * Implementa UserDetails para integrarse con Spring Security.
 *
 * Casos de uso: CU01 (Registro), CU02 (Login), CU10 (Gestión de usuarios)
 */
@Entity
@Table(
    name = "usuarios",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuarios_dni",   columnNames = "dni"),
        @UniqueConstraint(name = "uk_usuarios_email", columnNames = "email")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** DNI peruano: exactamente 8 dígitos numéricos */
    @NotBlank
    @Size(min = 8, max = 8, message = "El DNI debe tener exactamente 8 dígitos")
    @Pattern(regexp = "\\d{8}", message = "El DNI solo debe contener dígitos numéricos")
    @Column(nullable = false, length = 8)
    private String dni;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String nombre;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String apellido;

    @NotBlank
    @Email(message = "Debe ser un correo electrónico válido")
    @Column(nullable = false, length = 200)
    private String email;

    /** Número de teléfono peruano (9 dígitos) */
    @Size(max = 15)
    @Column(length = 15)
    private String telefono;

    /** Contraseña almacenada con BCrypt */
    @NotBlank
    @Column(nullable = false)
    private String password;

    /**
     * Puntos de reputación del ciudadano.
     * Aumenta con reportes validados, disminuye con falsas alarmas.
     * Permite al sistema de IA ponderar la confianza de los reportes.
     */
    @Builder.Default
    @Column(nullable = false)
    private Integer reputacion = 100;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 30)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private Rol rol = Rol.CIUDADANO;

    /** Fecha de registro en el sistema */
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    /** Última fecha de inicio de sesión */
    private LocalDateTime ultimoAcceso;

    /** Relación inversa con los reportes enviados por este usuario */
    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reporte> reportes;

    // ─── Lifecycle callbacks ───────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
    }

    // ─── UserDetails (Spring Security) ────────────────────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.rol.name()));
    }

    /** Spring Security usa email como username principal */
    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() {
        return this.estado != EstadoUsuario.BANEADO;
    }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return this.estado == EstadoUsuario.ACTIVO;
    }
}
