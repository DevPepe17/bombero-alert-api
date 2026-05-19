package com.bomberos.emergencias.security;

import com.bomberos.emergencias.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de UserDetailsService que carga el usuario desde la base de datos.
 *
 * Spring Security llama a este servicio en el flujo de login y en el filtro JWT
 * para verificar que el usuario sigue siendo válido en cada petición.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Busca al usuario por email (username en el sistema).
     * La entidad {@link com.bomberos.emergencias.entity.Usuario} implementa
     * {@link UserDetails} directamente, por lo que se retorna sin transformación.
     *
     * @param email Email del usuario (usado como username)
     * @throws UsernameNotFoundException si no existe un usuario con ese email
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(
                "Usuario no encontrado con email: " + email
            ));
    }
}
