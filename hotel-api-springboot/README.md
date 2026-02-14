# Hotel API Spring Boot (Phase 4)

This folder contains the Phase 4 Spring Boot implementation of the hotel API. The API is split into two services:

- rooms-service: room CRUD endpoints
- reservations-service: reservation CRUD endpoints and availability search

Both services use PostgreSQL (from hotel-db-postgres) and are designed to run together via Docker Compose.

## Services

### rooms-service
- Base path: /api/rooms
- Endpoints:
  - GET /api/rooms
  - GET /api/rooms/{id}
  - POST /api/rooms
  - PUT /api/rooms/{id}
  - DELETE /api/rooms/{id}

### reservations-service
- Base path: /api/reservations
- Endpoints:
  - GET /api/reservations
  - GET /api/reservations/{id}
  - GET /api/reservations/search?checkIn=YYYY-MM-DD&checkOut=YYYY-MM-DD
  - POST /api/reservations
  - PUT /api/reservations/{id}
  - DELETE /api/reservations/{id}

## Local Development (Docker Compose)

Start the Spring Boot services with Postgres:

```bash
docker compose --profile db --profile springboot up -d
```

Start the Spring Boot UI profile (React UI + Spring Boot services + Postgres):

```bash
docker compose --profile db --profile springboot --profile ui-springboot up -d
```

Stop the Spring Boot UI profile stack:

```bash
docker compose --profile db --profile springboot --profile ui-springboot down
```

## Running Services Individually

From the repo root:

```bash
cd hotel-api-springboot/rooms-service
mvn spring-boot:run
```

```bash
cd hotel-api-springboot/reservations-service
mvn spring-boot:run
```

Make sure Postgres is running:

```bash
docker compose --profile db up -d
```

## Service URLs

When running via Docker Compose:
- Rooms: http://localhost:8081/api/rooms
- Reservations: http://localhost:8082/api/reservations

From within the Docker network (container-to-container):
- Rooms: http://hotel-api-rooms:8080/api/rooms
- Reservations: http://hotel-api-reservations:8080/api/reservations

## UI Configuration

The React UI supports Spring Boot via the ui-springboot profile. The Vite proxy routes /api/rooms and /api/reservations to the Spring Boot services when running in containers.

Optional overrides:
- VITE_ROOMS_API_URL
- VITE_RESERVATIONS_API_URL

## Notes

- Date format for search: YYYY-MM-DD.
- Room JSON uses havePrivateBathroom while the database column is has_private_bathroom.
