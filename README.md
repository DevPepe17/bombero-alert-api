<div align="center">
  <img src="https://spring.io/images/spring-logo-9146a4d3298760c2e7e49595184e1975.svg" width="200" alt="Spring Boot Logo">
  <h1>🚨 Backend API: Sistema de Emergencias (CGBVP)</h1>
  <p>
    <b>API RESTful desarrollada con Spring Boot 3 y Java 21 para la gestión y despacho de emergencias en tiempo real.</b>
  </p>
  <br/>
</div>

## 📖 Descripción del Backend

Este repositorio contiene exclusivamente el **Backend** del Sistema de Despacho de Emergencias. Es una arquitectura basada en API REST que expone endpoints seguros mediante tokens JWT (JSON Web Tokens) para interactuar de forma separada con los clientes web o móviles de los Ciudadanos, Operadores y Administradores.

## 🛠️ Tecnologías Principales

- **Java 21** - Lenguaje principal.
- **Spring Boot 3.4.x** - Framework núcleo del servidor.
- **Spring Security** + **JJWT (0.12.6)** - Sistema de Autenticación Stateless y validación de roles.
- **Spring Data JPA (Hibernate)** - ORM para la persistencia de datos.
- **PostgreSQL (Neon)** - Base de datos relacional escalable.
- **Lombok** - Reducción de código boilerplate.
- **Spring Dotenv** - Carga segura de variables de entorno (Credenciales, DB).

---

## 🔒 Arquitectura de Seguridad

La API está protegida por un sistema de control de acceso basado en Roles (RBAC):
1. **`ROLE_CIUDADANO`**: Solo puede crear reportes y ver su historial personal.
2. **`ROLE_OPERADOR`**: Puede ver los reportes globales (Bandeja) y actualizar sus estados.
3. **`ROLE_ADMINISTRADOR`**: Acceso al endpoint exclusivo `/api/admin/stats` para extraer KPI's y data masiva.

> Todos los endpoints requieren que se pase el token JWT en la cabecera HTTP: 
> `Authorization: Bearer <TOKEN>`

---

## 🚀 Instalación y Ejecución (Entorno Local)

### 1. Clonar y preparar variables
Renombra el archivo `.env.example` a `.env` en la raíz de este proyecto (la misma carpeta que el `pom.xml`).
Añade tus credenciales de base de datos PostgreSQL y tu JWT Secret.

```env
DB_URL=jdbc:postgresql://tu-servidor-db:5432/bomberos_db
DB_USERNAME=usuario_postgres
DB_PASSWORD=contraseña_fuerte
JWT_SECRET=escribe_aqui_una_clave_muy_larga_de_256_bits_o_mas
```

### 2. Ejecutar con Maven
Abre una terminal en esta misma carpeta y ejecuta:
```bash
./mvnw spring-boot:run
```
(O en Windows puro sin Powershell: `mvnw spring-boot:run`)

El servidor Tomcat integrado iniciará automáticamente en el puerto `http://localhost:8080`.

---

## 📂 Estructura del Código

El proyecto sigue un patrón robusto MVC / Capas:

```text
src/main/java/com/bomberos/emergencias/
├── config/        # Seguridad JWT, CORS y Database Seeder (datos iniciales)
├── controller/    # Endpoints REST (ReporteController, AuthController, etc.)
├── dto/           # Data Transfer Objects (Peticiones y Respuestas)
├── entity/        # Entidades JPA mapeadas a tablas PostgreSQL
├── repository/    # Interfaces de Spring Data JPA con queries nativas/JPQL
├── security/      # Filtros JWT personalizados (JwtAuthenticationFilter)
└── service/       # Lógica de negocio core (Validaciones, cálculos, etc.)
```

## 🌱 Data Semilla (Generación Automática)
Al iniciar la aplicación por primera vez (gracias a `DatabaseSeeder.java`), la base de datos se poblará automáticamente con cuentas de prueba para facilitar el desarrollo sin necesidad de scripts SQL manuales:

- **Admin**: `admin@bomberos.pe` / `admin123`
- **Operador**: `operador1@bomberos.pe` / `operador123`
