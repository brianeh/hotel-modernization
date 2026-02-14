FROM mcr.microsoft.com/devcontainers/java:1-17-bookworm

# Remove problematic Yarn repository that causes GPG errors
RUN rm -f /etc/apt/sources.list.d/yarn.list \
    && apt-get update \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*
