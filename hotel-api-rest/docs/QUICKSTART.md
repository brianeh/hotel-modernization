# Hotel Reservation System - Quick Start

## First Time Setup

This project supports two development workflows. Choose the one that best fits your needs:

### Option 1: Docker Compose (Recommended for Monorepo Workflows)

**Best for**: Full-stack development, testing service integration, working with other services in the monorepo

#### Prerequisites
- Docker Desktop installed and running
- Git (to clone the repository)

#### Setup Steps:

1. **Navigate to monorepo root** (if you haven't already):
   ```bash
   cd /path/to/hotel-modernization
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

4. **Access the application**: http://localhost:8080/HotelReservation-war/

**That's it! The container automatically builds, initializes the database, and deploys the application.** 🎉

---

### Option 2: DevContainer (For Focused Development)

**Best for**: Focused development on this service only, independent development cycles, when you don't need other monorepo services

#### Setup Steps:

1. **Open in DevContainer** (wait ~7 minutes for setup)
   - Navigate to the `hotel-api-rest` folder
   - Select "Reopen in Container" when prompted

2. **Verify setup**: `./status.sh`

3. **Deploy application**: `./deploy.sh`

4. **Open browser**: http://localhost:8080/HotelReservation-war/

That's it! 🎉

## Daily Development

### Docker Compose Workflow

```bash
# Make code changes...

# Restart the service to pick up changes
docker compose restart hotel-api-rest

# Or rebuild and restart
docker compose --profile api up -d --build

# View logs to verify changes
docker compose logs -f hotel-api-rest

# Test in browser
# http://localhost:8080/HotelReservation-war/
```

**Note**: With Docker Compose, the container automatically builds and deploys on startup. For code changes, restart the container or rebuild.

### DevContainer Workflow

```bash
# Make code changes...

# Build and deploy
./deploy.sh

# Test in browser
# http://localhost:8080/HotelReservation-war/
```

## Essential Commands

### Docker Compose Commands (from monorepo root)

```bash
# Start the service
docker compose --profile api up -d

# Stop the service
docker compose stop hotel-api-rest

# Stop and remove containers
docker compose --profile api down

# View logs
docker compose logs -f hotel-api-rest

# Restart the service
docker compose restart hotel-api-rest

# Rebuild and restart
docker compose --profile api up -d --build

# Execute commands in container
docker compose exec hotel-api-rest bash

# Start with other services (e.g., React UI)
docker compose --profile api --profile ui up -d
```

### DevContainer Commands (from service directory)

```bash
# Quick status check
./status.sh

# Build and deploy
./deploy.sh

# Export database dump (writes to database/dump/data/mysql_dump.sql)
./database/dump/export_data.sh

# Build only
ant dist

# Deploy only
asadmin deploy --force=true dist/HotelReservation.ear

# View logs
tail -f /opt/glassfish4/glassfish/domains/domain1/logs/server.log

# Restart GlassFish
asadmin restart-domain domain1
```

Need a different password? Prefix the command with `MYSQL_PASSWORD=...` before running the export script.

## Access URLs

### Docker Compose

- **Application**: http://localhost:8080/HotelReservation-war/
- **REST API**: http://localhost:8080/HotelReservation-war/api/rooms
- **API Test Page**: http://localhost:8080/HotelReservation-war/rest-test.html
- **Admin Console**: http://localhost:4850
- **MySQL**: localhost:3308 (user: root, password: root)

### DevContainer

- **Application**: http://localhost:8080/HotelReservation-war/
- **REST API**: http://localhost:8080/HotelReservation-war/api/rooms
- **API Test Page**: http://localhost:8080/HotelReservation-war/rest-test.html
- **Admin Console**: http://localhost:4848
- **MySQL**: localhost:3306 (user: root, password: root)

**Note**: Docker Compose uses different ports (4850 for admin console, 3308 for MySQL) to avoid conflicts with other services.

## Troubleshooting

### Docker Compose Issues

#### Services not running?
```bash
# Check container status
docker compose ps

# View logs
docker compose logs hotel-api-rest

# Restart the service
docker compose restart hotel-api-rest

# Rebuild and restart
docker compose --profile api up -d --build
```

#### Application not working?
```bash
# Check container logs
docker compose logs -f hotel-api-rest

# Check if container is healthy
docker compose ps

# Execute into container to debug
docker compose exec hotel-api-rest bash
```

#### Database issues?
```bash
# Connect to MySQL in container
docker compose exec hotel-api-rest mysql -u root -proot

# Or from host (port 3308)
mysql -h localhost -P 3308 -u root -proot

# Check database
USE hotel_reservation_system;
SHOW TABLES;
SELECT * FROM room;
```

#### Port conflicts?
```bash
# Check what's using the ports
docker compose ps

# Stop conflicting services
docker compose --profile api down

# Start again
docker compose --profile api up -d
```

### DevContainer Issues

#### Services not running?
```bash
# Start MySQL
sudo service mysql start

# Start GlassFish
asadmin start-domain domain1
```

#### Application not working?
```bash
# Check status
./status.sh

# View logs
tail -50 /opt/glassfish4/glassfish/domains/domain1/logs/server.log

# Redeploy
./deploy.sh
```

#### Database issues?
```bash
# Connect to MySQL
mysql -u root -proot

# Check database
USE hotel_reservation_system;
SHOW TABLES;
SELECT * FROM room;
```

## Project Structure

```
hotel-api-rest/
├── deploy.sh              # ← Build & deploy script
├── status.sh              # ← Status check
├── HotelReservation-ejb/  # ← Backend (EJB)
├── HotelReservation-war/  # ← Frontend (Web)
└── database/              # ← SQL schema
```

## Development Workflow

### Docker Compose Workflow
1. Edit code
2. Restart container: `docker compose restart hotel-api-rest` (or rebuild: `docker compose --profile api up -d --build`)
3. Refresh browser
4. Check logs if needed: `docker compose logs -f hotel-api-rest`

### DevContainer Workflow
1. Edit code
2. Run `./deploy.sh`
3. Refresh browser
4. Check logs if needed

For more details, see **SETUP.md**


