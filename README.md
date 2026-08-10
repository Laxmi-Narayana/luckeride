# LuckeRide

LuckeRide is a ride-sharing platform built as a production-oriented full-stack application.

## Project Structure

```text
luckeride/
├── backend/       # Spring Boot + Java 21
├── frontend/      # React
├── docs/          # Architecture, API & design documentation
├── docker/        # Docker Compose & infrastructure
├── .github/       # GitHub Actions / CI
├── README.md
├── .gitignore
└── LICENSE
```

## Technology Stack

### Backend

- Java 21
- Spring Boot 4.1
- Spring MVC
- Spring Data JPA
- Spring Security
- PostgreSQL
- Bean Validation
- Actuator
- Maven

### Frontend

- React
- JavaScript / TypeScript
- Tailwind CSS

### Infrastructure

- Docker
- Docker Compose
- Redis
- Kafka
- GitHub Actions

> Infrastructure components will be introduced incrementally as the application evolves.

## Architecture

LuckeRide will initially be developed as a **modular monolith**.

The architecture will evolve toward distributed services where there is a clear technical or business reason to do so.

## Core Features

Planned capabilities include:

- User registration and authentication
- Rider and driver profiles
- Driver availability
- Ride booking
- Ride lifecycle management
- Location tracking
- Fare calculation
- Payments
- Notifications
- Ratings and reviews
- Ride history
- Admin capabilities

## Engineering Goals

The project focuses on:

- Clean architecture and maintainable code
- Secure API design
- Database design and performance
- REST API development
- Automated testing
- Caching
- Event-driven architecture
- Distributed systems concepts
- Observability
- Containerization
- CI/CD
- Production-oriented engineering practices

## Status

**Under active development**

The project is being built incrementally, with architecture and implementation decisions documented as development progresses.

## License

This project is licensed under the MIT License.

