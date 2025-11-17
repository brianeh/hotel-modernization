#!/usr/bin/env bash
set -uo pipefail

# Network name to connect to
NETWORK_NAME="hotel-shared-network"

# Check if network exists
if ! docker network inspect "$NETWORK_NAME" >/dev/null 2>&1; then
    echo "Network $NETWORK_NAME does not exist yet. It will be created when you start docker compose services."
    exit 0
fi

# Get the current container ID
# Try multiple methods for reliability across different environments
CONTAINER_ID=""

# Method 1: Get from /proc/self/cgroup (works in most container environments)
if [ -f /proc/self/cgroup ]; then
    CGROUP_ID=$(cat /proc/self/cgroup 2>/dev/null | grep "docker" | sed 's/.*\///' | tail -1 || true)
    if [ -n "$CGROUP_ID" ]; then
        # Get full container ID by inspecting the container
        CONTAINER_ID=$(docker ps --filter "id=${CGROUP_ID}" --format "{{.ID}}" 2>/dev/null | head -1 || true)
    fi
fi

# Method 2: Find by hostname (fallback)
if [ -z "$CONTAINER_ID" ]; then
    CONTAINER_NAME=$(hostname 2>/dev/null || echo "")
    if [ -n "$CONTAINER_NAME" ]; then
        CONTAINER_ID=$(docker ps --filter "name=${CONTAINER_NAME}" --format "{{.ID}}" 2>/dev/null | head -1 || true)
    fi
fi

# Method 3: Find devcontainer by label (last resort)
if [ -z "$CONTAINER_ID" ]; then
    CONTAINER_ID=$(docker ps --filter "label=devcontainer.config_file" --format "{{.ID}}" 2>/dev/null | head -1 || true)
fi

if [ -z "$CONTAINER_ID" ]; then
    echo "Warning: Could not determine container ID. Network connection skipped."
    echo "You can manually connect with: docker network connect $NETWORK_NAME <container-id>"
    exit 0
fi

# Check if already connected
if docker network inspect "$NETWORK_NAME" 2>/dev/null | grep -q "\"$CONTAINER_ID\""; then
    echo "Already connected to $NETWORK_NAME"
    exit 0
fi

# Connect to network
echo "Connecting devcontainer to $NETWORK_NAME..."
if docker network connect "$NETWORK_NAME" "$CONTAINER_ID" 2>/dev/null; then
    echo "Successfully connected to $NETWORK_NAME"
else
    echo "Note: Could not automatically connect. You can manually connect with:"
    echo "  docker network connect $NETWORK_NAME $CONTAINER_ID"
fi

