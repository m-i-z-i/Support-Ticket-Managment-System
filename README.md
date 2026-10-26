# Support Ticket Management Platform

A full-stack application for creating support tickets, viewing reported issues, and tracking their status. The React frontend communicates with a Spring Boot REST API, and PostgreSQL stores ticket data.

## Features

- Create tickets with a title and description.
- View all tickets and retrieve individual tickets through the API.
- Update ticket status: `OPEN`, `IN_PROGRESS`, `RESOLVED`, or `CLOSED`.
- Validate required fields and limit titles to 150 characters.
- Display loading, saving, and request error states in the frontend.

## Technology stack

| Component | Technology |
| --- | --- |
| Backend | Java 21, Spring Boot 4.1.2 |
| API | Spring Web MVC, Jakarta Validation |
| Persistence | Spring Data JPA, Hibernate, PostgreSQL |
| Frontend | React 19, CSS, Fetch API, Create React App |
| Tests | JUnit, Spring Boot Test, MockMvc, H2 |
| Build tools | Maven Wrapper, npm |

## Project structure

```text
support_ticket_management_platform/
├── src/main/java/com/example/support_ticket_management_platform/
│   ├── controller/       # HTTP endpoints
│   ├── dto/              # Request and response models
│   ├── entity/           # JPA ticket entity
│   ├── enums/            # Ticket statuses
│   ├── repository/       # Database access
│   └── service/          # Ticket operations
├── src/main/resources/application.yaml
├── src/test/             # Backend tests and H2 configuration
├── frontend/
│   ├── public/
│   ├── src/              # React components and styles
│   └── package.json
├── pom.xml
└── mvnw
```

## Prerequisites

Install Java JDK 21, Node.js with npm, PostgreSQL, and Git. Maven is provided by the wrapper; a separate Maven installation is unnecessary. The first build requires internet access to download dependencies.

Check your tools:

```sh
java -version
node --version
npm --version
psql --version
```

Set `JAVA_HOME` to your JDK 21 installation. On macOS:

```sh
export JAVA_HOME="$(/usr/libexec/java_home -v 21)"
```

## Local setup

### 1. Clone the repository

```sh
git clone https://github.com/m-i-z-i/Support-Ticket-Managment-System.git
cd Support-Ticket-Managment-System
```

### 2. Create the PostgreSQL database

Start PostgreSQL and connect using an account that can create roles and databases:

```sh
psql -U postgres -d postgres
```

If your installation uses a different administrator account, substitute that username. Run the following SQL, replacing the example password:

```sql
CREATE USER support_user WITH PASSWORD 'replace_with_your_password';
CREATE DATABASE support_ticket_db OWNER support_user;
```

Exit with `\q`. If the role and database already exist, use their existing credentials.

### 3. Start the backend

From the repository root, export the database credentials and start Spring Boot:

```sh
export DB_USERNAME=support_user
export DB_PASSWORD='replace_with_your_password'
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
$env:DB_USERNAME = "support_user"
$env:DB_PASSWORD = "replace_with_your_password"
.\mvnw.cmd spring-boot:run
```

The backend runs at **http://localhost:8080**. Check the ticket endpoint:

```sh
curl http://localhost:8080/api/tickets
```

A new database returns `[]`. The backend root `/` has no page; use `/api/tickets` for the API.

### 4. Start the frontend

Open another terminal:

```sh
cd frontend
npm ci
npm start
```

Open **http://localhost:3000**. Keep both the backend and frontend running. Create a ticket and use its status dropdown to update it. Refreshing the page loads saved tickets from PostgreSQL.

## Configuration

| Setting | Current value or behavior |
| --- | --- |
| Database URL | `jdbc:postgresql://localhost:5432/support_ticket_db` |
| `DB_USERNAME` | Defaults to `support_user` |
| `DB_PASSWORD` | Required for backend startup |
| Backend port | `8080` |
| Frontend development origin | `http://localhost:3000` |
| Frontend API URL | `http://localhost:8080/api/tickets`, defined in `frontend/src/App.js` |

To override the database address without editing YAML, set Spring Boot's datasource environment variable:

```sh
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/your_database
```

The application reads exported environment variables. A `.env` file is not automatically loaded by the Maven command. Keep credentials out of source control.

If you change the frontend port, update `@CrossOrigin` in `TicketController.java`. If you change the backend address, update `API_URL` in `frontend/src/App.js`.

Hibernate currently uses `ddl-auto: update` to update the database schema during startup.

## REST API

Base URL: `http://localhost:8080/api/tickets`

| Method | Path | Purpose | Success |
| --- | --- | --- | --- |
| `GET` | `/api/tickets` | List tickets | `200 OK` |
| `GET` | `/api/tickets/{id}` | Retrieve a ticket | `200 OK` |
| `POST` | `/api/tickets` | Create a ticket | `201 Created` |
| `PATCH` | `/api/tickets/{id}/status` | Update ticket status | `200 OK` |

### Create a ticket

```sh
curl -X POST http://localhost:8080/api/tickets \
  -H 'Content-Type: application/json' \
  -d '{"title":"Cannot log in","description":"Password reset is failing"}'
```

The response contains the generated `id`, `title`, `description`, `status`, and `createdAt`. New tickets start with status `OPEN`.

### Update status

Replace `1` with an existing ticket ID:

```sh
curl -X PATCH http://localhost:8080/api/tickets/1/status \
  -H 'Content-Type: application/json' \
  -d '{"status":"IN_PROGRESS"}'
```

Supported values are `OPEN`, `IN_PROGRESS`, `RESOLVED`, and `CLOSED`. Any of these statuses can be selected; transition rules are not currently enforced.

Invalid input returns `400 Bad Request`. An unknown ticket ID returns `404 Not Found`.

## Tests and builds

Run backend tests and package the application from the repository root:

```sh
./mvnw clean verify
```

Backend tests use an in-memory H2 database configured in `src/test/resources/application.yaml`; PostgreSQL is not required for those tests. API tests cover ticket creation, retrieval, validation, status updates, and missing tickets.

Build the frontend:

```sh
cd frontend
npm run build
```

The frontend production files are generated in `frontend/build/`. This build does not package React into the backend JAR; hosting and API configuration must be arranged separately for deployment.

## Troubleshooting

| Problem | What to check |
| --- | --- |
| `JAVA_HOME` is invalid | Point it to an installed JDK 21 directory, then open a new terminal. |
| Database connection refused | Ensure PostgreSQL is running on port `5432` and the database exists. |
| Password authentication fails | Verify the database role and exported `DB_PASSWORD`. |
| `DB_PASSWORD` placeholder cannot be resolved | Export `DB_PASSWORD` in the terminal that starts the backend. |
| React shows a network error | Verify the backend is running and `/api/tickets` responds. |
| Browser reports a CORS error | Use `http://localhost:3000` or update the controller's allowed origin. |
| API returns `400` | Provide a nonblank title and description, a title of at most 150 characters, and a supported status. |

## Version control

Backend and frontend are tracked in the same repository. From the repository root:

```sh
git add .
git commit -m "Describe your changes"
git push
```

Dependencies, generated build output, local environment files, and the frontend Git history backup are excluded by `.gitignore`.
