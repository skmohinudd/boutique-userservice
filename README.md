# boutique-userservice

Manages customer profiles and user lifecycle operations.

## Overview

- **Type:** Spring Boot service
- **Stack:** Java 21, Spring Boot, Maven, JPA, PostgreSQL, Flyway, Actuator, Docker
- **Port:** `8082`

## Flow

```text
Client / service → Controller → Business logic → Database / events / downstream services
```

## Main APIs

```text
Get /userId
Post /userId/deactivate
Put /userId
```

## Database

```text
users
```

## Configuration

```text
DB_CONNECTION_TIMEOUT_MS
DB_MAX_LIFETIME_MS
DB_PASSWORD
DB_POOL_MAX_SIZE
DB_POOL_MIN_IDLE
DB_URL
DB_USERNAME
DB_VALIDATION_TIMEOUT_MS
```

## Run

```bash
./mvnw spring-boot:run
./mvnw clean verify
```

## Docker

```bash
docker build -t boutique-userservice:local .
```

## Health

```bash
curl http://localhost:8082/actuator/health
```

## CI/CD

This repository is built and deployed independently through its own GitHub Actions workflow.
