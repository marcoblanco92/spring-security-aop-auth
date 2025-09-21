# Marbl Auth

Spring Boot authentication module with:

- JWT-based login/logout
- OAuth2 social login (Google/GitHub)
- Cross-cutting concerns via Spring AOP:
    - Login attempt monitoring and user lock
    - Auditing of sensitive operations
    - Logging/profiling
- Ready to be used as a reusable library (Maven/Gradle)