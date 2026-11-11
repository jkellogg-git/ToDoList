# ToDoList

A full-stack task management application. Users authenticate with their Google account and can create, manage, and track personal to-do items with priority levels and status tracking.

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 4.0.2, Java 21 |
| Frontend | Angular 20, TypeScript, SCSS |
| Database | PostgreSQL |
| Auth | Google OAuth2 (Spring Security) |
| ORM | Spring Data JPA / Hibernate |

## Features

- Google OAuth2 single sign-on — no passwords to manage
- Create, update, and delete tasks
- Set task priority: `low`, `medium`, or `high`
- Track task status: `in_progress` or `completed`
- Filter tasks by status or priority
- One-click task completion

## Project Structure

```
ToDoList/
├── src/                        # Spring Boot backend
│   └── main/java/.../
│       ├── config/             # Security & CORS config
│       ├── controller/         # REST controllers
│       ├── domain/             # JPA entities & enums
│       ├── dto/                # Request/response DTOs
│       ├── repository/         # Spring Data repositories
│       └── service/            # Business logic
├── frontend/                   # Angular SPA
│   └── src/app/
│       ├── components/
│       ├── pages/
│       ├── services/
│       └── models/
└── pom.xml
```

## Prerequisites

- Java 21+
- Node.js 18+ & npm
- PostgreSQL database
- Google OAuth2 credentials ([Google Cloud Console](https://console.cloud.google.com/))

## Environment Variables

The backend reads the following environment variables at startup:

| Variable | Description |
|----------|-------------|
| `PG_URL` | PostgreSQL connection string (e.g. `localhost:5432/todolist`) |
| `PG_USER` | Database username |
| `PG_PASSWORD` | Database password |
| `GOOGLE_CLIENT_ID` | OAuth2 client ID from Google Cloud Console |
| `GOOGLE_CLIENT_SECRET` | OAuth2 client secret from Google Cloud Console |

## Running Locally

### 1. Backend

```bash
# Set environment variables, then:
./mvnw spring-boot:run
```

The backend starts on **port 5000** (configured in `application.properties`).

> **Note:** The Angular dev proxy (`frontend/proxy.conf.json`) is currently configured to forward to port `8080`. Update the `target` values in that file to `http://localhost:5000` to match, or change `server.port` in `application.properties`.

### 2. Frontend

```bash
cd frontend
npm install
npm start        # ng serve — starts on http://localhost:4200
```

The Angular dev server proxies `/api`, `/oauth2`, `/login`, and `/logout` to the backend automatically via `proxy.conf.json`.

### 3. Build for Production

```bash
# Build Angular into src/main/resources/static/
cd frontend
npm run build

# Package everything into a single JAR
cd ..
./mvnw package

java -jar target/ToDoList-0.0.1-SNAPSHOT.jar
```

## API Reference

All endpoints require authentication (Google OAuth2 session).

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/tasks` | Get all tasks for the authenticated user |
| `POST` | `/api/tasks` | Create a new task |
| `PUT` | `/api/tasks/{id}` | Update a task |
| `DELETE` | `/api/tasks/{id}` | Delete a task |
| `PATCH` | `/api/tasks/{id}/complete` | Mark a task as completed |
| `GET` | `/api/tasks/completed` | Get all completed tasks |
| `GET` | `/api/tasks/in-progress` | Get all in-progress tasks |
| `GET` | `/api/tasks/priority/{priority}` | Get tasks filtered by priority (`low`, `medium`, `high`) |

### Task Object

```json
{
  "title": "Finish capstone",
  "description": "Complete the WGU D424 project",
  "status": "in_progress",
  "taskPriority": "high"
}
```

## Running Tests

```bash
# Backend unit & integration tests
./mvnw test

# Frontend tests
cd frontend
npm test
```

## Google OAuth2 Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/) → APIs & Services → Credentials.
2. Create an **OAuth 2.0 Client ID** (Web application).
3. Add `http://localhost:5000/login/oauth2/code/google` as an authorized redirect URI.
4. Copy the client ID and secret into your environment variables.
