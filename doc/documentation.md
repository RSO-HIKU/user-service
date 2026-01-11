# User Service - Technical Documentation

## Overview

The **User Service** is a microservice responsible for managing user profiles and social relationships (follow/unfollow functionality) within the HIKU hiking application.

## Table of Contents

1. [Architecture](#architecture)
2. [Technology Stack](#technology-stack)
3. [Business Logic](#business-logic)
4. [Database Schema](#database-schema)
5. [API Endpoints](#api-endpoints)
6. [Authentication & Authorization](#authentication--authorization)
7. [Messaging Integration](#messaging-integration)
8. [Health Checks](#health-checks)
9. [Configuration](#configuration)
10. [Deployment](#deployment)
11. [Local Development](#local-development)

---

## Architecture

### Key Components

- **Controllers**: Handle HTTP requests and responses
- **Repository**: Encapsulates database operations using JPA
- **Models**: JPA-mapped domain models
- **Messaging**: RabbitMQ integration for publishing follow events
- **Health**: Health check endpoints

---

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Runtime** | Java (Eclipse Temurin) | 17+ |
| **Build Tool** | Maven | 3.9 |
| **Framework** | KumuluzEE | 4.1.0 |
| **JPA Provider** | Hibernate | 5.6.15.Final |
| **Database** | PostgreSQL | 14+ |
| **Migration** | Flyway | 9.16.1 |
| **Authentication** | MicroProfile JWT | 2.1 |
| **Messaging** | RabbitMQ (AMQP) | 5.16.0 |
| **Containerization** | Docker | - |
| **Orchestration** | Kubernetes (via Helm) | - |



## Business Logic

### User Profile Management

The User Service manages the complete lifecycle of user profiles:

- **User Registration**: When a new user registers through Keycloak, their profile is created with a unique UUID as the primary identifier
- **Profile Updates**: Users can modify their profile information including username, full name, bio, and profile image
- **Profile Retrieval**: Supports fetching individual user profiles or listing multiple users

### Social Relationships

The core social feature is the **follow/unfollow** mechanism:

1. **Following a User**:
   - A user (follower) can follow another user (following)
   - Creates a relationship record in the `follows` table
   - Publishes a `follow.created` event to RabbitMQ for downstream services
   - Prevents duplicate follows via unique constraint

2. **Unfollowing a User**:
   - Removes the relationship record from the `follows` table
   - Publishes a `follow.deleted` event to RabbitMQ
   - Cascade deletion ensures cleanup when users are deleted

3. **Social Queries**:
   - Retrieve list of followers for a user
   - Retrieve list of users a user is following
   - Get follower/following counts
   - Check if a specific follow relationship exists

### Event-Driven Architecture

The service publishes follow/unfollow events to RabbitMQ, enabling:
- **Social Feed Service** to update user feeds when follow relationships change



---

## Database Schema

### Schema: `user_service`

#### Table: `users`

Stores user profile information.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | VARCHAR(255) | PRIMARY KEY | UUID from Keycloak |
| `username` | VARCHAR(255) | NOT NULL, UNIQUE | Unique username |
| `email` | VARCHAR(255) | NOT NULL, UNIQUE | User email |
| `password_hash` | VARCHAR(255) | - | (Legacy field, not used with Keycloak) |
| `full_name` | VARCHAR(255) | - | User's full name |
| `bio` | TEXT | - | User biography |
| `profile_image_url` | VARCHAR(500) | - | Profile picture URL |
| `created_at` | TIMESTAMP WITH TIME ZONE | NOT NULL | Account creation timestamp |

#### Table: `follows`

Manages follower/following relationships.

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGSERIAL | PRIMARY KEY | Auto-increment ID |
| `follower_id` | VARCHAR(255) | NOT NULL, FK → users(id) | User who follows |
| `following_id` | VARCHAR(255) | NOT NULL, FK → users(id) | User being followed |
| `created_at` | TIMESTAMP WITH TIME ZONE | NOT NULL | Relationship creation timestamp |

**Unique Constraint**: `(follower_id, following_id)` - prevents duplicate follows

**Foreign Keys**:
- `follower_id` references `users(id)` ON DELETE CASCADE
- `following_id` references `users(id)` ON DELETE CASCADE

### Database Migration

Database schema is managed using **Flyway** migrations located in `src/main/resources/db/migration/`.

Rules when working with migrations:
- Each migration has to follow the naming convention: VX__\<short name\>, where X is the next number that hasn't been used yet.
- Database migrations must be idempotent. You must not delete already existing and applied migrations.

Migrations run automatically via Kubernetes Job (see `helm/templates/migrate-job.yaml`).

---

## API Endpoints

Api endpoints are described in /doc/api.json.
## Authentication & Authorization

### JWT-Based Authentication

The User Service uses **MicroProfile JWT** with Keycloak as the identity provider.


## Messaging Integration

### RabbitMQ Event Publishing

The User Service publishes follow/unfollow events to RabbitMQ for consumption by other services ( Social Feed Service).

#### Configuration

**Environment Variables**:
- `RABBITMQ_HOST`: RabbitMQ server hostname (default: `localhost`)
- `RABBITMQ_PORT`: RabbitMQ port (default: `5672`)
- `RABBITMQ_USERNAME`: Authentication username (default: `guest`)
- `RABBITMQ_PASSWORD`: Authentication password (default: `guest`)

#### Event Structure

Defined in `FollowEvent.java`.

#### Publishing Events

Publishing an event defined in `FollowEventPublisher.java`.

**Routing Keys**:
- `follow.created`: Published when a follow relationship is created
- `follow.deleted`: Published when a follow relationship is deleted


## Health Checks

The service implements three health check endpoints for Kubernetes orchestration:

### Comprehensive Health Check

**Endpoint**: `GET /api/user/health`

Performs multiple health checks:
1. **Database connectivity** - Executes test query
2. **Memory usage** - Monitors JVM heap usage

### Liveness Probe

**Endpoint**: `GET /api/user/health/live`

Indicates if the application is running. Kubernetes restarts the pod if this fails.

### Readiness Probe

**Endpoint**: `GET /api/user/health/ready`

Indicates if the application is ready to accept traffic. Kubernetes removes the pod from load balancing if this fails.

---


## Configuration

### Application Configuration

Configuration file: `src/main/resources/config.yaml`

### JPA Configuration

Configuration file: `src/main/resources/META-INF/persistence.xml`


### Environment Variables

These values can be set in the `\helm\template\values-dev.yaml` file.

| Variable | Description | Default |
|----------|-------------|---------|
| `KUMULUZEE_ENV_NAME` | Environment name | `dev` |
| `KUMULUZEE_SERVER_HTTP_PORT` | HTTP server port | `8091` |
| `KUMULUZEE_SERVER_HTTP_ADDRESS` | Bind address | `0.0.0.0` |
| `KUMULUZEE_DATASOURCES_DEFAULT_CONNECTIONURL` | JDBC connection URL | `jdbc:postgresql://localhost:5432/hikudb` |
| `KUMULUZEE_DATASOURCES_DEFAULT_POOL_MAX_SIZE` | Connection pool size | `3` |
| `KUMULUZEE_JWT_AUTH_ISSUER` | JWT issuer URL | (required) |
| `KUMULUZEE_JWT_AUTH_JWKS_URI` | JWKS endpoint for JWT verification | (required) |
| `RABBITMQ_HOST` | RabbitMQ hostname | `rabbitmq.platform.svc.cluster.local` |
| `RABBITMQ_PORT` | RabbitMQ port | `5672` |

For local dev these values can be set in the `\helm\template\secret.yaml` file.
For the test and prod environments they are obtained from our Azure Key Vault.

| Secret | Description | Default |
|--------|-------------|---------|
| `KUMULUZEE_DATASOURCES_DEFAULT_USERNAME` | Database username | `hikuuser` |
| `KUMULUZEE_DATASOURCES_DEFAULT_PASSWORD` | Database password | `hikupassword` |
| `FLYWAY_USER` | Flyway Database role username | `hikuuser` |
| `FLYWAY_PASSWORD` | Flyway Database role password | `hikuadmin` | 
| `PG_HOST` | Database hostname | `localhost` |
| `RABBITMQ_USER` | RabbitMQ username | `hikuuser` |
| `RABBITMQ_PASSWORD` | RabbitMQ password | `hikupassword` |

---

## Deployment



#### Database Migrator Image

**Dockerfile**: `Dockerfile.migrator`

Runs Flyway migrations as a Kubernetes Job.

---

### Kubernetes (Helm)

#### Chart Structure

```
helm/
├── Chart.yaml              # Chart metadata
├── values-dev.yaml         # Development values
└── templates/
    ├── _helpers.tpl        # Template helpers
    ├── deployment.yaml     # Main application deployment
    ├── service-clusterip.yaml  # Internal service
    ├── service-nodeport.yaml   # External service (dev)
    ├── migrate-job.yaml    # Database migration job
    ├── secret.yaml         # Database credentials
    └── secretsproviderclass.yaml  # Azure Key Vault integration
```

---

### CI/CD Pipelines

#### Test Environment Pipeline

**File**: `.github/workflows/test-build-deploy.yaml`

**Triggers**:
- Push to `test` branch

**Steps**:
1. Checkout code
2. Build Docker images (app + migrator)
3. Push to Azure Container Registry (ACR)
4. Deploy to AKS test environment using ArgoCD

---

#### Production Promotion Pipeline

**File**: `.github/workflows/prod-promote.yaml`

**Triggers**:
- Manual workflow dispatch with image tag selection

**Steps**:
1. Pull images from test ACR
2. Retag images for production
3. Push to production ACR
4. Deploy to AKS production environment

Secrets used in the GitHub Actions workflows are saved as secrets in our GitHub Organization. Secrets used for deployment on the Azure cluster are provided by our Azure Key Vault.

---

## Local Development

Building images and deployment for local development is handled by Skaffold. By running the command **skaffold dev** in the root folder of the repository in a terminal window will make Skaffold automatically build and deploy the service to your local Minikube cluster. Skaffold watches your local files and when you save a change, Skaffold automatically applies it.  

### Prerequisites

#### Required Tools & Services
- **Java 17+**
- **Maven 3.9+**
- **PostgreSQL 14+**
- **Docker Desktop**
- **Keycloak**
- **RabbitMQ**
- **Minikube**
- **Skaffold**

#### Other requirements
- Docker Desktop is running,
- Minikube cluster is running on Docker Desktop,
- The database is deployed on your local cluster,
- The Traefik ingress controller is deployed on your local cluster,
- Keycloak is deployed on your local cluster.

### Steps performed by Skaffold
- Builds docker image for microservice,
- Builds docker image for database migrations,
- Deploys both images,
- Portforwards NodePort to the default port setting.

