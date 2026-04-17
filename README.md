# Task Manager REST API — Complete Project Guide

A Spring Boot REST API with an optional browser-based web UI served from `src/main/resources/static/index.html`.

This project demonstrates the full flow for building, documenting, and deploying a REST service with a simple static frontend.

---

## Project Folder Structure

```
taskmanager/
├── .github/
│   └── workflows/
│       └── ci.yml                          ← GitHub Actions CI pipeline
├── src/
│   ├── main/
│   │   ├── java/com/example/taskmanager/
│   │   │   ├── TaskManagerApplication.java ← Spring Boot entry point
│   │   │   ├── controller/
│   │   │   │   └── TaskController.java     ← REST endpoints
│   │   │   ├── service/
│   │   │   │   └── TaskService.java        ← Business logic
│   │   │   ├── repository/
│   │   │   │   └── TaskRepository.java     ← In-memory data store
│   │   │   ├── model/
│   │   │   │   └── Task.java               ← Task data model
│   │   │   └── exception/
│   │   │       ├── TaskNotFoundException.java
│   │   │       ├── ErrorResponse.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/example/taskmanager/
│           ├── service/
│           │   └── TaskServiceTest.java    ← Unit tests (Mockito)
│           └── controller/
│               └── TaskControllerTest.java ← Slice tests (MockMvc)
├── .gitignore
└── pom.xml
```

---

## Architecture Overview

```
HTTP Request
     │
     ▼
┌─────────────────┐
│  TaskController │  ← Handles HTTP, input validation, response codes
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   TaskService   │  ← Business rules, throws exceptions
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ TaskRepository  │  ← In-memory ConcurrentHashMap storage
└─────────────────┘
```

---

## API Reference

| Method | Endpoint       | Description          | Success Code |
|--------|----------------|----------------------|--------------|
| GET    | /tasks         | Get all tasks        | 200          |
| GET    | /tasks/{id}    | Get task by ID       | 200          |
| POST   | /tasks         | Create a new task    | 201          |
| PUT    | /tasks/{id}    | Update a task        | 200          |
| DELETE | /tasks/{id}    | Delete a task        | 204          |

## Web UI

A browser-based UI is available at:

- `http://localhost:8082/`
- `http://localhost:8082/status` returns a simple JSON health/status response

The UI source is `src/main/resources/static/index.html` and the API is available under `/tasks`.

### Task JSON Schema

```json
{
  "id": 1,
  "title": "Buy groceries",
  "description": "Milk, eggs, bread",
  "completed": false
}
```

---

## Step-by-Step Instructions

### Prerequisites

Make sure you have these installed before starting:

- **Java 17** — verify with `java -version`
- **Maven 3.8+** — verify with `mvn -version`
- **Git** — verify with `git --version`
- A **GitHub account** (free at github.com)

---

### 1. Run the Project Locally

```bash
# Navigate into the project folder
cd taskmanager

# Start the application
mvn spring-boot:run
```

The API will start on **http://localhost:8082** by default.

If you change `server.port` in `src/main/resources/application.properties`, use that port instead.

**Test it with curl:**

```bash
# Create a task
curl -X POST http://localhost:8082/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"Learn Spring Boot","description":"Build a REST API","completed":false}'

# Get all tasks
curl http://localhost:8082/tasks

# Get task by ID
curl http://localhost:8082/tasks/1

# Update a task
curl -X PUT http://localhost:8082/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Learn Spring Boot","description":"Done!","completed":true}'

# Delete a task
curl -X DELETE http://localhost:8082/tasks/1
```

---

### 2. Build Using Maven

```bash
# Full build: compile + test + package into a JAR
mvn clean package

# Run the packaged JAR directly
java -jar target/taskmanager-1.0.0.jar

# Build without running tests (not recommended)
mvn clean package -DskipTests
```

---

### 3. Run Tests

```bash
# Run all unit tests
mvn test

# Run tests with a detailed report printed to console
mvn test -Dsurefire.useFile=false

# Run a single test class
mvn test -Dtest=TaskServiceTest

# Run a single test method
mvn test -Dtest=TaskServiceTest#createTask_success
```

After running tests, view the HTML report at:
```
target/surefire-reports/
```

---

### 4. Initialize Git

```bash
# Inside the taskmanager folder
cd taskmanager

# Initialise a new local Git repository
git init

# Stage all files
git add .

# Create the first commit
git commit -m "Initial commit: Task Manager REST API"
```

---

### 5. Push to GitHub

```bash
# 1. Create a new repository on GitHub:
#    → Go to https://github.com/new
#    → Name it: taskmanager
#    → Do NOT initialise with README (we already have one)
#    → Click "Create repository"

# 2. Connect your local repo to GitHub (replace YOUR_USERNAME)
git remote add origin https://github.com/YOUR_USERNAME/taskmanager.git

# 3. Rename the branch to 'main' (GitHub's default)
git branch -M main

# 4. Push to GitHub
git push -u origin main
```

---

### 6. Trigger GitHub Actions CI

GitHub Actions runs **automatically** once you push — no extra setup needed.

**To verify it ran:**

1. Go to your repository on GitHub
2. Click the **"Actions"** tab
3. You will see a workflow run called **"Java CI with Maven"**
4. Click it to see each step: checkout → set up JDK → build → test

**Every future push or pull request will re-trigger the pipeline automatically.**

To trigger it manually right now:

```bash
# Make any small change, commit, and push
echo "# Task Manager" >> README.md
git add README.md
git commit -m "Trigger CI"
git push
```

---

## What the CI Pipeline Does

```yaml
on: push / pull_request   ← Triggers automatically
    │
    ▼
ubuntu-latest runner
    │
    ├─ actions/checkout@v4         ← Downloads your source code
    ├─ actions/setup-java@v4       ← Installs Java 17 (Temurin)
    ├─ mvn clean verify            ← Compiles + runs ALL tests
    └─ upload-artifact             ← Saves test reports for review
```

If any test fails, the pipeline turns **red** and GitHub will notify you.

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Port 8080 already in use | `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9090` |
| `java: error: release version 17 not supported` | Set `JAVA_HOME` to Java 17 |
| Tests failing locally but passing in CI | Run `mvn clean test` to clear stale build artifacts |
| GitHub push rejected | Ensure remote URL is correct: `git remote -v` |
