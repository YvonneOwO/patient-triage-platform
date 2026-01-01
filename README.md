# Patient Triage & Appointment Management Platform

A backend system for patient triage and appointment management, designed to simulate a real-world healthcare workflow.  
This project focuses on clean backend architecture, role-based access control (RBAC), and containerized deployment.

---

## 🚀 Project Overview

The Patient Triage Platform allows patients to submit health-related requests and book appointments, while enabling doctors and administrators to manage schedules, review cases, and enforce access control.

The system is built using a layered backend architecture and follows industry best practices for RESTful API design and database interaction.

---

## 🏗️ Architecture

The project follows a classic **layered architecture**:

Controller → DTO → Service → Repository → Database

- **Controller**: Handles HTTP requests and responses
- **DTOs**: Define API contracts and prevent direct exposure of entities
- **Service**: Core business logic and permission checks
- **Repository**: Data access via JPA/Hibernate
- **Database**: PostgreSQL (containerized)

---

## 🔐 Key Features

- **Role-Based Access Control (RBAC)**
  - Roles: `PATIENT`, `DOCTOR`, `ADMIN`
  - Permissions enforced in the Service layer

- **Appointment Management**
  - Create, update, and cancel appointments
  - Time conflict validation for doctors and patients

- **Patient Triage Workflow**
  - Structured handling of patient requests
  - Separation of business logic from API layer

- **Global Exception Handling**
  - Centralized error handling for consistent API responses

- **Testing**
  - Unit tests for Controllers and Services using JUnit

---

## 🛠️ Tech Stack

- **Language**: Java  
- **Framework**: Spring Boot  
- **Database**: PostgreSQL  
- **ORM**: Spring Data JPA / Hibernate  
- **Build Tool**: Maven  
- **Containerization**: Docker & Docker Compose  
- **Testing**: JUnit  
- **API Documentation**: Swagger / OpenAPI  

---

## 🐳 Running the Project (Docker)

Make sure Docker and Docker Compose are installed.

```bash
docker compose up --build

The backend service and PostgreSQL database will start in containers.

⸻

📂 Project Structure

patient-triage-platform/
├── backend/
│   ├── src/main/java/com/patienttriage
│   │   ├── controller
│   │   ├── service
│   │   ├── repository
│   │   ├── dto
│   │   └── entity
│   └── src/test/java
├── init-db/
│   └── init.sql
├── docker-compose.yml
└── README.md

⸻

📌 Future Improvements
	•	Add Redis caching for frequently accessed data
	•	Introduce async processing for triage evaluation
	•	Enhance API security with JWT authentication
	•	Add monitoring and logging (e.g., Prometheus, ELK)
