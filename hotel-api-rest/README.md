# Hotel Reservation System (RESTful API)

**Modernization Demonstration Repository**

This repository demonstrates a **RESTful API modernization strategy** for the legacy Hotel Reservation System. Originally copied from **[hotel-monolith](https://github.com/brianeh/hotel-monolith)** (link to original repository), this project showcases how to add modern REST API capabilities to existing Java EE applications **without requiring major infrastructure upgrades**.

**Modernization Goal**: Decouple the frontend from backend by adding RESTful API endpoints while preserving the existing JSP/Servlet web interface and maintaining compatibility with Java 8, GlassFish 4, and MySQL.

[![License MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

> Note: This repository is part of the Hotel Modernization umbrella project: [hotel-modernization](https://github.com/brianeh/hotel-modernization).

---

## 🔄 Migration Strategy

### Approach
This modernization uses **JAX-RS (Java API for RESTful Web Services)** to add REST API capabilities to the existing Java EE 7 application. The strategy leverages the current technology stack without requiring upgrades to Java version, application server, or database.

### Key Benefits
- **Zero new dependencies**: Uses existing Java EE 7 APIs
- **Non-disruptive**: Existing JSP/Servlet UI continues to work unchanged
- **Reuses existing EJBs**: No business logic duplication
- **Compatible stack**: Works with Java 8 and GlassFish 4
- **Prepared entities**: Entities already have `@XmlRootElement` for JSON/XML serialization

### Implementation Status
✅ **Complete Implementation Guide Available**

For detailed technical implementation, API endpoints, code examples, and testing instructions, see:
- **[RESTful API Implementation Guide](docs/api/MODULES_AND_REST_API.md)** - Complete technical documentation

🚧 **RESTful API Implementation In Progress**

---

## 🚀 Quick Start

This project supports two development workflows. Choose the one that best fits your needs:

### Option 1: Docker Compose (Recommended for Monorepo Workflows)

**Best for**: Full-stack development, testing service integration, working with other services in the monorepo

The root `docker-compose.yml` orchestrates all services on a shared network. This is the recommended approach when working with the entire hotel-modernization monorepo.

#### Prerequisites
- Docker Desktop installed and running
- Git (to clone the repository)

#### Setup Steps:

1. **Clone the monorepo** (if you haven't already):
   ```bash
   git clone https://github.com/brianeh/hotel-modernization.git
   cd hotel-modernization
   ```

2. **Start the REST API service**:
   ```bash
   docker compose --profile api up -d
   ```

3. **Wait for services to start** (~2-3 minutes for initial setup):
   ```bash
   docker compose logs -f hotel-api-rest
   ```
   Press `Ctrl+C` to exit log view once services are ready.

4. **Access the application**:
   - **Web Application**: http://localhost:8080/HotelReservation-war/
   - **REST API**: http://localhost:8080/HotelReservation-war/api/rooms
   - **API Test Page**: http://localhost:8080/HotelReservation-war/rest-test.html
   - **Admin Console**: http://localhost:4850

#### Service Management:

```bash
# View logs
docker compose logs -f hotel-api-rest

# Stop the service
docker compose stop hotel-api-rest

# Stop and remove containers
docker compose --profile api down

# Start with other services (e.g., React UI + API)
docker compose --profile api --profile ui up -d

# Start all services
docker compose --profile all up -d
```

**Note**: The container automatically builds, initializes the database, and deploys the application. No manual deployment steps needed!

---

### Option 2: DevContainer (For Focused Development)

**Best for**: Focused development on this service only, independent development cycles, when you don't need other monorepo services

This approach uses the service-specific DevContainer configuration for isolated development.

#### Setup in 4 Steps:

1. **Open in DevContainer** (VS Code/Cursor with DevContainers extension)
   - Navigate to the `hotel-api-rest` folder
   - Select "Reopen in Container" when prompted

2. **Wait for automatic setup** (~7 minutes for initial setup)
   - Java 8, Ant, GlassFish, MySQL, and database are configured automatically

3. **Deploy the application**:
   ```bash
   ./deploy.sh
   ```

4. **Access the application**: http://localhost:8080/HotelReservation-war/

**That's it! Everything is configured automatically.**

#### Key Commands (DevContainer):
```bash
./status.sh   # Check all services status
./deploy.sh   # Build and deploy application
```

---

### Documentation:
- **[QUICKSTART.md](docs/QUICKSTART.md)** - Quick reference and daily workflow
- **[SETUP.md](docs/SETUP.md)** - Comprehensive setup guide and troubleshooting
- **[ARCHITECTURE.md](docs/ARCHITECTURE.md)** - Complete system architecture documentation
- **[ARCHITECTURE_DIAGRAM.md](docs/ARCHITECTURE_DIAGRAM.md)** - Visual architecture diagrams (Mermaid)
- **[RESTful API Implementation Guide](docs/api/MODULES_AND_REST_API.md)** - Complete REST API modernization documentation
- **[.devcontainer/README.md](.devcontainer/README.md)** - DevContainer technical details

---

### Features 💡
* Very simple to use (UX Design)
* Responsive design
* Online hotel reservation

### Report
* [Architecture and Software Development - PDF Version](Report.pdf)

### Screenshot
Home           |
:--------------:|
![home - screenshoot](screenshots/home.PNG) |


Available rooms    |
:-----------------:|
![available rooms - screenshoot](screenshots/availableRooms.PNG) |

Reservation       |
:----------------:|
![reservation - screenshoot](screenshots/finalReservation.PNG) |

### Used Languages
* Java
* HTML
* CSS
* JavaScript

### Used Technologies & Frameworks
* JavaEE (JSP, Servlet, EJB)
* JAX-RS (RESTful Web Services)
* Bootstrap

### Used Database
* MySQL

### Used server
* GlassFish 4.1.1

### Installation 🔌

**🌟 Recommended: DevContainer (Easiest)**
1. Clone this repository
2. Open in VS Code/Cursor with DevContainers extension
3. Wait for automatic setup (~7 minutes)
4. Run: `./deploy.sh`
5. Access: http://localhost:8080/HotelReservation-war/

**See [QUICKSTART.md](docs/QUICKSTART.md) for DevContainer details.**

**Manual Installation (Traditional):**
1. Press the **Fork** button (top right the page) to save copy of this project on your account.
2. Download the repository files (project) from the download section or clone this project:
   ```
   git clone https://github.com/brianeh/hotel-modernization.git
   cd hotel-modernization/hotel-api-rest
   ```
3. Download GlassFish 4.1+ or any other server (support EJB) like JBoss, and add it to the IDE.
4. Import & execute the SQL queries from the Database folder to the MySQL database.
5. Import the project in NetBeans or any other IDE.
6. Deploy & Run the application :D

