# Hotel Reservation System - Modernization Roadmap

An execution-focused plan for evolving the legacy Java EE hotel reservation system through progressive phases—from API enablement to microservices. It defines scope, sequencing, effort bands, risks, team profiles, and environment assumptions per phase, along with expected deliverables across code, infrastructure, and documentation. Estimates are directional and vary based on team skills, traffic scale, compliance needs, and deployment constraints.

## Overview

This roadmap explains what changes in each phase, why those changes matter, and how to proceed: architecture diagrams, technology choices, migration steps, testing and rollout guidance, and links to working repositories. Use it to pick a starting phase, verify prerequisites and exit criteria, align timelines and cost levels, and plan cutover/rollback. Pair it with the Decision Framework and Architecture Comparison to finalize scope, sequence, and stakeholder expectations before execution.

**Document Purpose:** This roadmap provides phase-by-phase execution details, code structure, and API summaries. For detailed API specifications with HTTP examples, see [IMPLEMENTATION_DETAILS.md](./IMPLEMENTATION_DETAILS.md). For development setup and troubleshooting, see [DEVCONTAINER_SETUP.md](./DEVCONTAINER_SETUP.md).

---

## Phase 1: Legacy Java EE Application ✅

**Project:** `hotel-monolith`

**Status:** ✅ Completed

### Architecture

3-tier Java EE monolithic application with tight coupling between frontend and backend.

```
[Browser] → [JSP/Servlet] → [EJB Session Beans] → [JPA] → [MySQL]
```

### Technology Stack

- **Frontend:** JSP (JavaServer Pages), HTML, CSS, JavaScript
- **Backend:** EJB 3.1 (Stateless Session Beans), Servlets 3.1
- **Database:** MySQL 8.0
- **Server:** GlassFish 4.1.1
- **Build Tool:** Apache Ant
- **JDK:** Java 8

### Key Features

- MVC pattern with JSP views and Servlet controllers
- EJB session beans for business logic
- JPA entities for data persistence
- CRUD operations for rooms and reservations
- Date-based availability filtering
- Responsive Bootstrap-based UI

### Code Structure

**Module: `HotelReservation-ejb` (EJB Module)**
- `models/Room.java` - JPA entity for room data (id, description, numberOfPerson, havePrivateBathroom, price)
- `models/Reservation.java` - JPA entity for reservation data (id, idRoom, checkInDate, checkOutDate, fullName, email, phone, specialRequest)
- `sessionbeans/RoomFacade.java` - Stateless session bean for room CRUD operations
- `sessionbeans/ReservationFacade.java` - Stateless session bean for reservation CRUD operations
- `sessionbeans/AbstractFacade.java` - Base facade with common JPA operations

**Module: `HotelReservation-war` (Web Module)**
- `servlets/Home.java` - Home page servlet
- `servlets/AvailableRooms.java` - Available rooms listing servlet
- `servlets/FinalReservation.java` - Reservation submission servlet
- `views/*.jsp` - JSP view pages (home.jsp, availableRooms.jsp, reservation.jsp)

**Database Schema:**
- `room` table: id (INT, PK, AUTO_INCREMENT), description (TEXT), number_of_person (INT), have_private_bathroom (BOOLEAN), price (REAL)
- `reservation` table: id (INT, PK, AUTO_INCREMENT), id_room (INT, FK), check_in_date (DATE), check_out_date (DATE), full_name (VARCHAR(25)), email (VARCHAR(25)), phone (VARCHAR(20)), special_request (TEXT)

**Build Configuration:**
- `build.xml` - Apache Ant build script
- `build.properties` - Build configuration properties
- EAR deployment structure: `HotelReservation.ear` containing EJB and WAR modules

### Business Justification

Establishes the baseline legacy system that subsequent phases will modernize. Demonstrates traditional Java EE architecture patterns.

---

## Phase 2: RESTful API Layer ✅

**Project:** `hotel-api-rest`

**Status:** ✅ Completed

### Architecture

Added JAX-RS REST API layer to decouple frontend from backend while preserving existing JSP/Servlet interface.

```
[Browser] → [JSP/Servlet] ───┐
         → [REST API] ───→ [EJB Session Beans] → [JPA] → [MySQL]
```

### Technology Stack

- **Frontend:** JSP (preserved), REST API clients
- **Backend:** EJB 3.1, JAX-RS (Jersey), Jackson JSON
- **Database:** MySQL 8.0 (unchanged)
- **Server:** GlassFish 4.1.1
- **Build Tool:** Apache Ant
- **JDK:** Java 8

### Key Changes from Phase 1

- Added `RoomResource` and `ReservationResource` JAX-RS endpoints
- Created `ApplicationConfig` class to configure JAX-RS application path (`/api`) and disable MOXy JSON provider
- Configured Jackson for JSON serialization (via Jersey's automatic provider selection after disabling MOXy)
- Enabled CDI for dependency injection
- Full CRUD operations for both rooms and reservations
- API-first approach while maintaining backward compatibility

### Code Structure

**REST API Implementation:**
- `rest/ApplicationConfig.java` - JAX-RS application configuration
  - `@ApplicationPath("api")` - Sets base path to `/api`
  - Disables MOXy JSON provider to use Jackson
- `rest/RoomResource.java` - REST resource for room operations
  - `@Path("rooms")` - Maps to `/api/rooms`
  - Injects `RoomFacadeLocal` via `@EJB`
  - Supports JSON and XML via `@Produces` and `@Consumes`
- `rest/ReservationResource.java` - REST resource for reservation operations
  - `@Path("reservations")` - Maps to `/api/reservations`
  - Injects `ReservationFacadeLocal` via `@EJB`
  - Implements date conflict checking for availability search

**Configuration:**
- Jackson libraries added to `WEB-INF/lib/` for JSON serialization
- `web.xml` - Servlet configuration (unchanged from Phase 1)
- JAX-RS automatically discovered via `@ApplicationPath` annotation

### API Endpoints

**Note:** For detailed API endpoint documentation with complete HTTP request/response examples, see [IMPLEMENTATION_DETAILS.md](./IMPLEMENTATION_DETAILS.md#rest-api-specifications).

**Room Endpoints:**
- `GET /api/rooms` - List all rooms
  - Response: `200 OK` with JSON array of Room objects
- `GET /api/rooms/{id}` - Get room details
  - Response: `200 OK` with Room object or `404 Not Found`
- `POST /api/rooms` - Create new room
  - Request Body: JSON Room object (without id)
  - Response: `201 Created` with Location header and Room object
- `PUT /api/rooms/{id}` - Update room
  - Request Body: JSON Room object
  - Response: `200 OK` or `400 Bad Request`
- `DELETE /api/rooms/{id}` - Delete room
  - Response: `204 No Content` or `400 Bad Request`

**Reservation Endpoints:**
- `GET /api/reservations` - List all reservations
  - Response: `200 OK` with JSON array of Reservation objects
- `GET /api/reservations/{id}` - Get reservation details
  - Response: `200 OK` with Reservation object or `404 Not Found`
- `GET /api/reservations/search?checkIn=YYYY-MM-DD&checkOut=YYYY-MM-DD` - Search available rooms by date range
  - Query Parameters: `checkIn` (required), `checkOut` (required)
  - Response: `200 OK` with JSON array of available Room objects
  - Logic: Returns rooms that don't have conflicting reservations
  - Example: `/api/reservations/search?checkIn=2025-11-01&checkOut=2025-11-05`
- `POST /api/reservations` - Create new reservation
  - Request Body: JSON Reservation object (without id)
  - Response: `201 Created` with Location header and Reservation object
- `PUT /api/reservations/{id}` - Update reservation
  - Request Body: JSON Reservation object
  - Response: `200 OK` or `400 Bad Request`
- `DELETE /api/reservations/{id}` - Cancel reservation
  - Response: `204 No Content` or `400 Bad Request`

**Content Negotiation:**
- All endpoints support both `application/json` and `application/xml`
- Accept header determines response format
- Default is JSON when no Accept header specified

**Note:** The search endpoint (`GET /api/reservations/search`) is essential for frontend applications to find available rooms for booking. The React SPA in Phase 3 relies on this endpoint for its core room search functionality.

### Business Justification

**Benefits:**
- Minimal infrastructure changes
- Enables future frontend modernization
- API-first design for mobile apps
- Zero new dependencies (uses Java EE 7 APIs)

**Trade-offs:**
- Still running legacy GlassFish server
- Technical debt remains in JSP/Servlet layer
- No infrastructure modernization

**Estimated Effort:** 2-3 weeks  
**Risk Level:** Low

---

## Phase 3: React Frontend ✅

**Project:** `hotel-ui-react`

**Status:** ✅ Completed

### Architecture

Modern React Single Page Application consuming REST API from backend server via Vite dev server proxy.

```
[Browser] → [Vite Dev Server:5173] → [Proxy /api/*] → [hotel-api-rest:8080 + REST API] → [EJB] → [MySQL]
             (React SPA)              (Docker network)
```

**Development Setup:**
- React app runs on Vite dev server (port 5173) in `hotel-ui-react` container
- Vite proxy forwards `/api/*` requests to `hotel-api-rest` service via Docker network
- Eliminates CORS issues by keeping browser requests same-origin
- Docker Compose orchestrates services on shared network (`hotel-shared-network`)

### Technology Stack

- **Frontend:** React 18.2, TypeScript 5.0, CSS Modules, React Router 7.9.5
- **Build Tool:** Vite 5.0
- **Development:** Docker Compose with Node.js 20+ container
- **Backend:** GlassFish with REST API (from Phase 2, service: `hotel-api-rest`)
- **Database:** MySQL 8.0 (via backend) or PostgreSQL 15 (Phase 3.5 option)
- **Deployment:** Docker Compose for development, S3 + CloudFront planned for production (frontend), EC2/ECS (backend)

### Key Changes from Phase 2

- Replace JSP/Servlet UI with React SPA
- React Router 7.9.5 for client-side navigation
- CSS Modules for component-scoped styling
- TypeScript for type safety and better developer experience
- Vite build tool with hot module replacement (HMR)
- Vite proxy configuration eliminates CORS issues in development
- Responsive mobile-first design with CSS Grid and Flexbox
- Component-based architecture with reusable UI components
- REST API becomes primary integration point
- Docker Compose orchestration for consistent development environment

### Features

**User-Facing Pages:**
- **Home Page** - Welcome page with date selection for check-in and check-out dates
- **Explore Page** - Browse hotel amenities and facilities with image galleries
- **Contact Page** - Contact information and hotel details
- **Available Rooms Page** - Display and browse available rooms with details and pricing
- **Reservation Page** - Complete reservation booking flow with customer information form

**Administrative Features:**
- **API Test Page** - Comprehensive test interface for all REST API endpoints:
  - Get All Rooms, Get Room by ID
  - Search Available Rooms by date range
  - Create, view, update, and delete reservations
  - Create, update, and delete rooms (admin operations)

**Technical Features:**
- React Router for client-side navigation
- Responsive design with mobile-first approach (breakpoints at 768px, 900px, 1024px)
- TypeScript interfaces for type safety (Room and Reservation types)
- CSS Modules for scoped styling per component
- Vite proxy configuration for seamless API integration
- Docker Compose orchestration for development environment consistency

### Code Structure

**Component Architecture:**
```
src/
├── components/
│   ├── HomePage.tsx              # Landing page with date selection form
│   ├── ExplorePage.tsx           # Hotel amenities showcase
│   ├── ContactPage.tsx           # Contact information
│   ├── AvailableRoomsPage.tsx   # Room listing with search results
│   ├── ReservationPage.tsx       # Booking form with customer details
│   ├── ApiTestPage.tsx           # Administrative API testing interface
│   ├── layout/
│   │   └── SiteLayout.tsx        # Main layout with navigation
│   ├── ui/
│   │   ├── Button.tsx           # Reusable button component
│   │   ├── Container.tsx       # Page container wrapper
│   │   ├── FormField.tsx         # Form input component
│   │   ├── ImageTile.tsx         # Image display component
│   │   └── SectionHeader.tsx    # Section title component
│   └── common/
│       └── SearchForm.tsx        # Reusable date search form
├── services/
│   └── api-client.ts             # REST API client functions
├── types/
│   ├── room.ts                   # Room TypeScript interface
│   └── reservation.ts            # Reservation TypeScript interface
├── App.tsx                       # Root component with React Router
└── main.tsx                      # Application entry point
```

**API Client (`services/api-client.ts`):**
- `getAllRooms()` - Fetch all rooms
- `getRoomById(id)` - Fetch room by ID
- `searchAvailableRooms(checkIn, checkOut)` - Search available rooms
- `createReservation(reservation)` - Create new reservation
- `getAllReservations()` - Fetch all reservations
- `createRoom(room)` - Create new room (admin)
- `updateRoom(id, room)` - Update room (admin)
- `deleteRoom(id)` - Delete room (admin)
- `updateReservation(id, reservation)` - Update reservation (admin)
- `deleteReservation(id)` - Delete reservation (admin)
- Uses relative URLs (`/api/*`) for Vite proxy integration

**TypeScript Types:**
- `Room` interface: id, description, numberOfPerson, havePrivateBathroom, price
- `Reservation` interface: id?, idRoom, checkInDate, checkOutDate, fullName, email, phone, specialRequest?

**Vite Configuration (`vite.config.ts`):**
- Proxy configuration for `/api/*` routes
- Development: Proxies to `http://hotel-api-rest:8080/HotelReservation-war` (Docker Compose service name)
- Production: Uses `VITE_API_URL` environment variable
- Port: 5173 (strictPort: true)
- Host: 0.0.0.0 (allows container access)
- Services communicate via Docker network (`hotel-shared-network`)

**React Router Routes (`App.tsx`):**
- `/` - HomePage
- `/explore` - ExplorePage
- `/contact` - ContactPage
- `/api-test` - ApiTestPage
- `/available-rooms` - AvailableRoomsPage (with query params: checkIn, checkOut)
- `/reservation` - ReservationPage

**Responsive Design:**
- Mobile breakpoint: ≤768px (hamburger menu, stacked forms)
- Tablet breakpoint: ≥900px (two-column layouts)
- Desktop breakpoint: ≥1024px (max-width containers)
- CSS Grid and Flexbox for flexible layouts

### Business Justification

**Benefits:**
- Modern user experience
- Better developer productivity
- Separation of frontend/backend deployment
- Future mobile app support

**Trade-offs:**
- Maintaining legacy backend infrastructure (GlassFish, Java EE)
- Dual deployment complexity (frontend and backend separately)
- Vite proxy required in development (resolved in production with proper CORS configuration)
- No authentication/authorization layer implemented yet

**Estimated Effort:** 6-8 weeks  
**Risk Level:** Medium

---

## Phase 4: Spring Boot Backend ✅

**Project:** `hotel-api-springboot`

**Status:** ✅ Complete

### Architecture

Spring Boot REST API split into two services (rooms and reservations) backed by PostgreSQL.

```
[React SPA] → [Rooms Service] ─┐
            ├→ [PostgreSQL]
[React SPA] → [Reservations Service] ┘
```

### Technology Stack

- **Frontend:** React SPA (Phase 3)
- **Backend:** Spring Boot 3.x (Java 17)
- **Database:** PostgreSQL 15 (Phase 3.5 service)
- **Deployment:** Docker Compose (local)
- **Build Tool:** Maven

### Key Changes from Phase 3

- Modern Spring Boot replacing GlassFish for API workloads
- Two services: `rooms-service` and `reservations-service`
- PostgreSQL backing store with schema from Phase 3.5
- Docker Compose profiles for UI + backend selection

### Spring Components

- `RoomController` - REST endpoints
- `ReservationController` - Booking management and search
- `RoomRepository` - Spring Data JPA
- `ReservationService` - Business logic

### Business Justification

**Benefits:**
- Industry-standard framework
- Modern local dev workflow with Compose
- Decoupled services for rooms and reservations
- Uses PostgreSQL (aligns with Phase 3.5)

**Trade-offs:**
- Two-service coordination for local dev
- No auth or gateway in this phase

**Estimated Effort:** 8-10 weeks  
**Risk Level:** Medium

### Local Development

Start Postgres and Spring Boot services:

```bash
docker compose --profile db --profile springboot up -d
```

Run the React UI against Spring Boot:

```bash
docker compose --profile db --profile springboot --profile ui-springboot up -d
```

Service URLs:
- Rooms API: http://localhost:8081/api/rooms
- Reservations API: http://localhost:8082/api/reservations

---

## Phase 5: Serverless Backend 📋

**Repository:** [hotel-api-serverless](https://github.com/brianeh/hotel-api-serverless)

**Status:** 📋 Planned

### Architecture

Fully serverless architecture with AWS Lambda functions, API Gateway, and managed services.

```
[React SPA] → [API Gateway] → [Lambda Functions] → [DynamoDB]
                ↓                  ↓
            [Cognito]          [EventBridge]
```

### Technology Stack

- **Frontend:** React SPA (S3 + CloudFront)
- **API Layer:** AWS API Gateway
- **Business Logic:** AWS Lambda (Java/Node.js)
- **Database:** Amazon DynamoDB
- **Authentication:** AWS Cognito
- **Notifications:** SNS + SES
- **Deployment:** Serverless Framework / AWS SAM

### Key Changes from Phase 4

- Replace Spring Boot with Lambda functions
- Replace PostgreSQL with DynamoDB
- Event-driven architecture
- Managed authentication service
- Pay-per-use pricing model

### Lambda Functions

- `room-service` - Room inventory management
- `reservation-service` - Booking processing
- `availability-service` - Room availability checks
- `notification-service` - Email/SMS notifications

### Business Justification

**Benefits:**
- Zero server management
- Automatic scaling
- Cost-effective for variable workloads
- High availability built-in
- Event-driven loose coupling

**Trade-offs:**
- Vendor lock-in to AWS
- Cold start latency
- Stateless architecture complexity
- Limited execution time (15 min)

**Estimated Effort:** 8-10 weeks  
**Risk Level:** Medium

---

## Phase 6: Microservices Architecture 📋

**Repository:** [hotel-api-microservices](https://github.com/brianeh/hotel-api-microservices)

**Status:** 📋 Planned

### Architecture

Domain-driven microservices with event-driven communication and independent deployment.

```
[SPA] → [API Gateway] → [Service Mesh]
                           ↓
        ┌─────────────────┼─────────────────┐
        ↓                 ↓                 ↓
    [Room MS]      [Reservation MS]    [User MS]
        ↓                 ↓                 ↓
    [PostgreSQL]     [Aurora]           [Cognito]
```

### Technology Stack

- **Frontend:** React SPA
- **Services:** Quarkus (Java Native)
- **API Gateway:** Kong or AWS API Gateway
- **Messaging:** Apache Kafka or AWS EventBridge
- **Database:** PostgreSQL, DynamoDB
- **Orchestration:** Kubernetes (EKS)
- **Service Mesh:** AWS App Mesh or Istio
- **Monitoring:** Prometheus, Grafana

### Microservices

1. **Room Service** - Room inventory and availability
2. **Reservation Service** - Booking management
3. **User Service** - User authentication and profiles
4. **Payment Service** - Payment processing
5. **Notification Service** - Email/SMS notifications
6. **Analytics Service** - Business intelligence

### Key Changes from Phase 4

- Break monolith into bounded contexts
- Event-driven service communication
- Independent service deployment
- Container orchestration with Kubernetes
- Service mesh for observability
- Domain-driven design principles

### Business Justification

**Benefits:**
- Maximum scalability and flexibility
- Technology diversity per service
- Independent team development
- Fault isolation
- Cloud-native architecture

**Trade-offs:**
- Operational complexity
- Distributed system challenges
- Higher costs
- Requires DevOps maturity
- Overkill for small applications

**Estimated Effort:** 16+ weeks  
**Risk Level:** High

---

## Migration Strategy Comparison

| Approach | Effort | Cost | Risk | Benefits |
|----------|--------|------|------|----------|
| Lift & Shift | 3-4 weeks | Low | Low | Immediate cloud benefits |
| Frontend Modernization | 6-8 weeks | Medium | Medium | Modern UX, API-first |
| Full Refactor (Spring Boot) | 8-10 weeks | Medium | Medium | Modern stack, maintainable |
| Serverless | 8-10 weeks | Low-Medium | Medium | Pay-per-use, auto-scale |
| Microservices | 16+ weeks | High | High | Maximum scalability |

## Decision Framework

**Choose Phase 2 (RESTful API)** if:
- Need quick wins with minimal risk
- Want to enable future modernization
- Limited infrastructure changes allowed

**Choose Phase 3 (SPA)** if:
- User experience is priority
- Gradual migration preferred
- Frontend skills available

**Choose Phase 4 (Spring Boot)** if:
- Need industry-standard framework
- Large team with Spring expertise
- Long-term maintainability important
- Want a PostgreSQL-backed Spring Boot implementation available in this repo

**Choose Phase 5 (Serverless)** if:
- Variable/low traffic workload
- Want pay-per-use pricing
- Minimal operational overhead desired

**Choose Phase 6 (Microservices)** if:
- Large scale application
- Multiple teams working independently
- Need maximum flexibility and scalability

## References

- [Legacy Architecture](https://github.com/brianeh/hotel-monolith/blob/master/docs/ARCHITECTURE.md)
- [Migration Plans](https://github.com/brianeh/hotel-monolith/blob/master/docs/MIGRATION_PLAN.md)

