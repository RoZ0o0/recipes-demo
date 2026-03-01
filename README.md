# Recipes Demo API

REST API for managing recipes and ingredients.  
The application allows creating, retrieving, updating, deleting, and searching recipes with pagination, filtering, and sorting support.

The API is based on an OpenAPI specification and runs fully in Docker.

---

## Functional Description

The system allows managing recipes and their ingredients.

User can perform the following operations:

- create a new recipe with ingredients
- retrieve recipe details by ID
- update an existing recipe and its ingredients
- delete a recipe
- search recipes by name or keyword
- sort recipes by selected field (e.g. name)
- browse recipes using pagination

Each recipe contains:

- name
- description
- difficulty level
- preparation time (in minutes)
- list of ingredients

Each ingredient contains:

- name
- quantity
- unit

The system validates input data and returns an error when invalid data is provided.

The API requires authentication to access endpoints.


## Technologies Used

- Java 21
- Spring Boot 3.5.11 (Web, Data JPA, Security)
- PostgreSQL 16 (via Docker Compose)
- Flyway (database migrations)
- MapStruct (DTO mapping)
- Lombok (code generation)
- OpenAPI tools (Springdoc + OpenAPI Generator)
- Rest Assured & Testcontainers (testing)
- Maven (build tool)
- Docker + Docker Compose (containerization)

---

## Prerequisites

- Java 21+
- Maven 3.9+
- Docker
- Docker Compose

---

## How to run

### 1. Build the Application
Use Maven to build the application and skip tests:

```
mvn clean package -DskipTests
```

### 2. Start PostgreSQL with Docker Compose

Start the PostgreSQL database using Docker Compose:

```
docker compose up -d
```

### 3. Build the Backend Docker Image

Build a Docker image for the backend application:

```
docker build -t recipes-demo-app .
```

### 4. Run the Backend Application

Run the backend app container, connect it to the network, and expose port 8080:

```
docker run -d --name recipes-demo-app --network recipes-demo-net -p 8080:8080 recipes-demo-app
```

---

## Tests

To execute all tests:

```
mvn clean test
```
This will compile the code, run the tests, and show a summary of results

---

## How to stop

To stop and remove the running backend container:

```
docker stop recipes-demo-app

docker rm recipes-demo-app
```

To stop and remove postgres container and network:

```
docker compose down
```

---

## API Base URL

```
http://localhost:8080
```

---

## Access Swagger UI
Once the backend is running, access the API documentation via Swagger at:

```
http://localhost:8080/swagger-ui/index.html#/
```

---

## Default Credentials

The default credentials for the application are:

- `Username: admin`

- `Password: password`

---
