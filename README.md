# Support Ticket Management Platform

Spring Boot application using Java 21 and Maven Wrapper.

## Run locally

On macOS, select an installed Java 21 JDK before running Maven:

```sh
export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
./mvnw spring-boot:run
```

The server listens on port 8080. The default database is H2 in memory;
its contents are lost when the application stops. No HTTP endpoints have
been implemented yet, so a request to `/` returns 404.

The VS Code workspace selects the installed Microsoft JDK 21. If using
another machine, update the JDK paths in `.vscode/settings.json`.
Open a new terminal after changing these settings.

## Use PostgreSQL

Create a database, then configure its connection through environment variables:

```sh
export SPRING_PROFILES_ACTIVE=postgres
export DB_URL=jdbc:postgresql://localhost:5432/support_tickets
export DB_USERNAME=your_database_user
export DB_PASSWORD=your_database_password
./mvnw spring-boot:run
```

## Verify

```sh
./mvnw clean verify
```

Tests use the default H2 database without requiring a PostgreSQL server.
Run checks with `SPRING_PROFILES_ACTIVE` unset.
