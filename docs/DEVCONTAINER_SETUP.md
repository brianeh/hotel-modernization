# DevContainer Setup Guide

## Overview

This guide explains how to set up development environments for the hotel modernization monorepo. The repository contains multiple services that can be developed and tested together using Docker Compose orchestration or individual DevContainers.

**Document Purpose:** This guide focuses on development environment setup, Docker Compose orchestration, troubleshooting, and alternative DevContainer workflows. For day-to-day development workflows, see [IMPLEMENTATION_DETAILS.md](./IMPLEMENTATION_DETAILS.md#development-workflow). For quick start commands, see the main [README.md](../README.md#quick-start-guides).

The monorepo structure includes:
- `hotel-monolith/` - Legacy Java EE application
- `hotel-api-rest/` - Modern REST API backend
- `hotel-ui-react/` - React frontend SPA
- `hotel-db-postgres/` - Shared PostgreSQL database

---

## Development Environment Options

### Primary: Docker Compose Workflow (Recommended)

**Best for**: Full-stack development, testing service integration, monorepo workflows

The root `docker-compose.yml` orchestrates all services on a shared network (`hotel-shared-network`). Services use profiles so you can start them independently or together.

**Pros:**
- ✅ Single command to start all services
- ✅ Automatic service dependencies and health checks
- ✅ Shared network simplifies service communication
- ✅ Consistent with production-like environment
- ✅ Easy to test service integration

**Cons:**
- ⚠️ Requires Docker Compose knowledge
- ⚠️ All services share the same Docker network

### Alternative: Individual DevContainers

**Best for**: Focused development on a single service, different teams, independent development cycles

Each service has its own DevContainer configuration:
```
Workspace Root/
├── .devcontainer/                    # Root DevContainer (Python tooling)
├── hotel-monolith/.devcontainer/     # Legacy monolith DevContainer
├── hotel-api-rest/.devcontainer/     # REST API DevContainer
└── hotel-ui-react/.devcontainer/     # React frontend DevContainer
```

**Pros:**
- ✅ Clean separation of concerns
- ✅ Frontend developers work independently
- ✅ No backend dependency for UI development
- ✅ Matches production architecture

**Cons:**
- ⚠️ Need to run multiple DevContainers simultaneously
- ⚠️ Port forwarding coordination required
- ⚠️ Manual service orchestration

---

## Docker Compose Workflow (Primary)

### Prerequisites

1. Install Docker Desktop and ensure it is running
2. Install VS Code with the Dev Containers extension or use `devcontainer` CLI
3. (Optional) Open the repository in the root DevContainer for Python tooling

### Root DevContainer

The root `.devcontainer/` provides Python 3.12 tooling (`pipx`, `uv`, `ruff`), Docker CLI, and PostgreSQL client. It's useful for running Docker Compose commands and database administration.

To use it:
1. Open the repository folder in VS Code/Cursor
2. Run **Dev Containers: Reopen in Container**
3. The container mounts the Docker socket so you can control other containers

If your host Docker daemon uses a non-default group id, export it before reopening:
```bash
export DOCKER_GID=$(stat -c '%g' /var/run/docker.sock)   # macOS: stat -f '%g'
```

### Starting Services with Docker Compose

**Important**: Before starting services, ensure any existing containers are stopped to avoid name conflicts:

```bash
# Stop and remove all containers (if they exist)
docker compose down

# Or stop/remove specific containers
docker stop hotel-postgres-standalone hotel-api-rest hotel-ui-react hotel-monolith
docker rm hotel-postgres-standalone hotel-api-rest hotel-ui-react hotel-monolith
```

The root `docker-compose.yml` defines four services with profiles:

```bash
# Database only (default port 5432)
docker compose --profile db up hotel-db-postgres

# REST API toolkit container (waits for manual deploy steps)
docker compose --profile api up hotel-api-rest

# React UI pointing at the REST API proxy target
docker compose --profile ui up hotel-ui-react

# Legacy monolith toolkit container
docker compose --profile monolith up hotel-monolith

# Bring the entire stack up
docker compose --profile all up
```

### Service Details

#### `hotel-db-postgres`
- **Image**: `postgres:15-alpine`
- **Port**: `5432`
- **Database**: `hotel_reservation`
- **User**: `postgres` / **Password**: `postgres`
- **Schema**: Auto-initialized from `hotel-db-postgres/schema/`
- **Health Check**: Gates API and monolith containers

#### `hotel-api-rest`
- **Ports**: `8080` (HTTP), `4850` (Admin Console)
- **Container**: Idles and waits for manual deployment
- **Usage**: 
  ```bash
  docker compose exec hotel-api-rest bash
  ./status.sh    # Check services
  ./deploy.sh    # Deploy backend
  ```
- **Network**: Service name `hotel-api-rest` on shared network (`hotel-shared-network`)
- **Health Check**: Verifies `/api/rooms` endpoint

#### `hotel-ui-react`
- **Port**: `5173` (Vite dev server)
- **Auto-start**: Runs `npm run dev` automatically
- **Proxy**: Vite proxy in `vite.config.ts` targets `http://hotel-api-rest:8080/HotelReservation-war` (Docker Compose service name)
- **Dependencies**: Waits for `hotel-api-rest` to be healthy

#### `hotel-monolith`
- **Ports**: `8080` (HTTP), `4849` (Admin Console)
- **Container**: Idles for manual build/deploy operations
- **Usage**: 
  ```bash
  docker compose exec hotel-monolith bash
  ./status.sh
  ./deploy.sh
  ```

### Development Workflow

#### Full Stack Development

```bash
# 1. Start all services
docker compose --profile all up

# 2. In separate terminals, exec into containers for manual operations:

# Deploy the API
docker compose exec hotel-api-rest bash
./deploy.sh

# Or deploy the monolith
docker compose exec hotel-monolith bash
./deploy.sh

# 3. Access services:
# - Frontend: http://localhost:5173
# - API: http://localhost:8080/HotelReservation-war
# - API Admin: http://localhost:4850
# - Monolith Admin: http://localhost:4849
# - Database: localhost:5432
```

#### Frontend-Only Development

```bash
# Start database and API
docker compose --profile db --profile api up

# Deploy API (in another terminal)
docker compose exec hotel-api-rest bash
./deploy.sh

# Start frontend
docker compose --profile ui up hotel-ui-react
```

#### Database Administration

```bash
# Connect to Postgres from API container
docker compose exec hotel-api-rest psql -h hotel-db-postgres -U postgres hotel_reservation

# Or from root devcontainer
psql -h hotel-db-postgres -U postgres hotel_reservation
```

### Data Volume

Postgres data is stored in the named volume `hotel-postgres-data`. To reset:

```bash
docker compose down
docker volume rm hotel-postgres-data
docker compose --profile db up hotel-db-postgres
```

### Stopping Services

#### Stop All Services

To stop and remove all running containers:

```bash
# Stop and remove all containers, networks, and volumes
docker compose down

# Stop and remove containers but preserve volumes
docker compose down --volumes=false

# Stop and remove containers, networks, volumes, and images
docker compose down --rmi all
```

#### Stop Specific Services

To stop individual services:

```bash
# Stop a specific service (keeps container, just stops it)
docker compose stop hotel-db-postgres

# Stop multiple specific services
docker compose stop hotel-api-rest hotel-ui-react

# Stop and remove a specific service
docker compose rm -f hotel-db-postgres
```

#### Stop vs Down

- **`docker compose stop`**: Stops running containers but keeps them. You can restart with `docker compose start` (but must specify profiles).
- **`docker compose down`**: Stops and removes containers, networks, and optionally volumes. Containers must be recreated on next start.

**Example workflow:**

```bash
# Stop services but keep containers (faster restart)
docker compose stop

# Later, restart without rebuilding (must specify profiles)
docker compose --profile all start

# Or stop and remove everything (clean slate)
docker compose down

# Later, recreate and start
docker compose --profile all up
```

#### Preserving Data

When stopping services, volumes are preserved by default. To remove volumes as well:

```bash
# Remove volumes when stopping (WARNING: deletes database data)
docker compose down --volumes
```

**Note**: The `--volumes` flag removes all volumes defined in `docker-compose.yml`, including `hotel-postgres-data`. Use with caution.

---

## Individual DevContainers Workflow (Alternative)

### Frontend DevContainer

**File**: `hotel-ui-react/.devcontainer/devcontainer.json`

```json
{
  "name": "Hotel SPA Frontend",
  "image": "node:20",
  "forwardPorts": [5173],
  "portsAttributes": {
    "5173": {
      "label": "Vite Dev Server",
      "onAutoForward": "notify"
    }
  },
  "postCreateCommand": "npm install",
  "customizations": {
    "vscode": {
      "extensions": [
        "dbaeumer.vscode-eslint",
        "esbenp.prettier-vscode",
        "bradlc.vscode-tailwindcss"
      ],
      "settings": {
        "editor.defaultFormatter": "esbenp.prettier-vscode",
        "editor.formatOnSave": true
      }
    }
  },
  "remoteEnv": {
    "VITE_API_URL": "http://host.docker.internal:8080/HotelReservation-war"
  }
}
```

### Backend DevContainers

Both `hotel-monolith/.devcontainer/` and `hotel-api-rest/.devcontainer/` are configured with:
- Java 8 (OpenJDK)
- Apache Ant 1.10.7
- GlassFish 4.1.1
- MySQL 8.0 (for legacy compatibility)

**Note**: These containers use MySQL internally, but the monorepo's shared database is Postgres.

### Workflow with Separate DevContainers

#### Step 1: Start Backend DevContainer

```bash
cd hotel-api-rest
code --new-window .  # Or open in Cursor
# Select "Reopen in Container"
# Wait for DevContainer to start

# Once container is ready:
./status.sh    # Check services
./deploy.sh    # Deploy backend
```

**Backend will be available at**: `http://localhost:8080/HotelReservation-war/`

#### Step 2: Start Frontend DevContainer (New Window)

```bash
cd hotel-ui-react
code --new-window .  # Open in new VS Code window
# Select "Reopen in Container"
# Wait for npm install to complete

# Once container is ready:
npm run dev    # Start Vite dev server
```

**Frontend will be available at**: `http://localhost:5173/`

#### Step 3: Connect Frontend to Backend

The frontend uses `host.docker.internal` to access the backend running on the host:

```typescript
// Set in devcontainer.json remoteEnv
VITE_API_URL=http://host.docker.internal:8080/HotelReservation-war
```

The `host.docker.internal` allows the frontend container to access the host machine's port 8080 (where the backend is running).

---

## Port Configuration

### Development Ports

| Service | Port | URL | Purpose |
|---------|------|-----|---------|
| **Frontend (Vite)** | 5173 | http://localhost:5173 | React dev server |
| **Backend (GlassFish)** | 8080 | http://localhost:8080 | Backend API |
| **API Admin Console** | 4850 | http://localhost:4850 | API server admin |
| **Monolith Admin Console** | 4849 | http://localhost:4849 | Monolith server admin |
| **PostgreSQL** | 5432 | localhost:5432 | Shared database |

**Note**: When using Docker Compose, only one service can bind to port 8080 at a time. The API and monolith use different admin ports (4850 vs 4849) to avoid conflicts.

### Port Forwarding

**Docker Compose**: Ports are automatically mapped in `docker-compose.yml`

**Individual DevContainers**: 
- Frontend DevContainer: Exposes port 5173 for Vite
- Backend DevContainers: Expose ports 8080 (HTTP) and 4848 (Admin)

---

## Troubleshooting

### Issue: Frontend can't reach backend API

**For Docker Compose:**
- Verify `hotel-api-rest` is healthy: `docker compose ps`
- Check API container logs: `docker compose logs hotel-api-rest`
- Ensure API is deployed: `docker compose exec hotel-api-rest ./deploy.sh`
- Verify network connectivity: `docker compose exec hotel-ui-react ping hotel-api-rest`

**For Separate DevContainers:**
- Check the API URL in devcontainer config: `VITE_API_URL=http://host.docker.internal:8080/HotelReservation-war`
- Verify backend is running on host port 8080
- Test backend directly: `curl http://localhost:8080/HotelReservation-war/api/rooms`

### Issue: Container name already in use

**Error**: `Error response from daemon: Conflict. The container name "/hotel-postgres-standalone" is already in use`

**Solution**: Stop and remove the existing container

```bash
# Option 1: Stop and remove all containers
docker compose down

# Option 2: Stop and remove specific container
docker stop hotel-postgres-standalone
docker rm hotel-postgres-standalone

# Option 3: Force remove if container is stuck
docker rm -f hotel-postgres-standalone

# Then start services again
docker compose --profile db up hotel-db-postgres
```

### Issue: Port already in use

**Solution**: Change port or stop conflicting services

```bash
# Check what's using port 8080
lsof -i :8080    # macOS/Linux
netstat -ano | findstr :8080    # Windows

# Stop conflicting containers
docker compose down
# Or kill the process
```

### Issue: Database connection errors

**For Postgres (Docker Compose):**
- Check database is ready: `docker compose ps hotel-db-postgres`
- Verify health check: `docker compose logs hotel-db-postgres`
- Test connection: `docker compose exec hotel-api-rest psql -h hotel-db-postgres -U postgres hotel_reservation`
- Check environment variables: `docker compose exec hotel-api-rest env | grep DB_`

**For MySQL (Individual DevContainers):**
- Check MySQL is running: `./status.sh`
- Verify credentials: `mysql -u root -proot`
- Check database exists: `mysql -u root -proot -e "SHOW DATABASES;"`

### Issue: npm install fails

**Solution**: Clear npm cache and retry

```bash
npm cache clean --force
rm -rf node_modules package-lock.json
npm install
```

### Issue: CORS errors in browser

**Solution**: The Vite proxy configuration handles CORS automatically. If you see CORS errors:

- Verify `vite.config.ts` proxy configuration
- Check that API URL is correct in environment variables
- Ensure backend CORS configuration allows the frontend origin

### Issue: Service health checks failing

**Solution**: Check service logs and verify endpoints

```bash
# Check API health
docker compose logs hotel-api-rest
docker compose exec hotel-api-rest curl http://localhost:8080/HotelReservation-war/api/rooms

# Check database health
docker compose logs hotel-db-postgres
docker compose exec hotel-db-postgres pg_isready -U postgres
```

### Issue: Docker Compose network issues

**Warning**: `a network with name hotel-shared-network exists but was not created for project`

**Solution**: This warning is usually harmless if the network was created by the root devcontainer. If you encounter connectivity issues, recreate the network:

```bash
# Stop all services
docker compose down

# Remove the network (if it exists)
docker network rm hotel-shared-network

# Start services again (network will be recreated)
docker compose --profile db up hotel-db-postgres
```

**Note**: The root devcontainer's `connect-to-network.sh` script may create this network. If you're working from the root devcontainer, this is expected behavior.

---

## Recommended Development Flow

### Docker Compose (Recommended)

```bash
# Terminal 1: Start all services
docker compose --profile all up

# Terminal 2: Deploy API
docker compose exec hotel-api-rest bash
./deploy.sh

# Terminal 3: View logs
docker compose logs -f hotel-ui-react

# Browser
# http://localhost:5173 → Frontend
# Frontend makes API calls to :8080 via proxy
```

### Separate DevContainers

```bash
# Terminal 1: Backend
cd hotel-api-rest
code .  # Opens in DevContainer
./status.sh
./deploy.sh
# Backend running on :8080

# Terminal 2: Frontend  
cd hotel-ui-react
code .  # Opens in NEW DevContainer
npm run dev
# Frontend running on :5173

# Browser
# http://localhost:5173 → Frontend
# Frontend makes API calls to :8080
```

---

## Next Steps

- Tailor the API and monolith deployments to use Postgres by updating their GlassFish JDBC pools or persistence descriptors
- Extend the Compose profiles with health checks or automatic startup commands once the migration scripts are finalized
- See the root `README.md` for more details on the Docker Compose setup
