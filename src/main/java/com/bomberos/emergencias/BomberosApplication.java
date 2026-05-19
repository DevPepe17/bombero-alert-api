package com.bomberos.emergencias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del Sistema de Emergencias - Bomberos Perú.
 *
 * Stack: Java 21 + Spring Boot 3 + Spring Security (JWT) + PostgreSQL (Neon)
 */
@SpringBootApplication
public class BomberosApplication {

    public static void main(String[] args) {
        SpringApplication.run(BomberosApplication.class, args);
    }
}
