# Cloud-Native Expense Tracker

## Overview
This is a microservices-based personal finance application designed to demonstrate enterprise-grade software architecture.

### Tech Stack
- **Frontend:** React with TypeScript & Vite
- **Backend:** Spring Boot (Microservices)
- **Database:** PostgreSQL
- **Infrastructure:** Docker & Docker Compose
- **Observability:** Prometheus & Grafana

## Getting Started

### Prerequisites
- Docker Desktop
- Java 17+
- Node.js (LTS)

### Local Development
To launch the core infrastructure (Database, etc.):
```bash
docker-compose up -d
```

## Directory Structure
- `/frontend`: React SPA application.
- `/backend`: Spring Boot microservices (Auth, Expense, Analytics, Gateway).
- `/infrastructure`: Configuration files for Prometheus, Grafana, and Docker.
- `/docs`: Project plan, architecture diagrams, and user guides.
