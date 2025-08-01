# Order Microservice – Podzilla System

## Overview
The **Order Microservice** is a core component of the **Podzilla system**, responsible for managing everything related to customer orders. It is built with **Java Spring Boot** and handles order creation, updates, cancellation, tracking, and more.

## Features
- Create new orders
- Retrieve orders by ID or user
- Update order information
- Cancel or delete orders
- Track real-time shipment status
- Manage order statuses (e.g., pending, shipped, delivered)

## Badges
![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring--Boot-3.0-brightgreen)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blueviolet)
![Docker](https://img.shields.io/badge/Docker-24.0-blue)
![JUnit](https://img.shields.io/badge/Testing-JUnit%205-orange)
![Checkstyle](https://img.shields.io/badge/Code%20Style-Checkstyle-yellow)
![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-blue)


## Code Style

### Naming Conventions
| Element        | Convention       | Example             |
|----------------|------------------|---------------------|
| Classes        | PascalCase       | `OrderService`      |
| Methods/Vars   | camelCase        | `createOrder`       |
| Constants      | UPPER_SNAKE_CASE | `MAX_ORDER_ITEMS`   |
| Packages       | lowercase        | `com.podzilla.order`|

### Best Practices
-  Single Responsibility Principle
-  Proper JavaDoc for public classes/methods
-  Centralized error handling
-  Logging with SLF4J
-  No magic values – use constants
-  Thorough unit testing with JUnit 5

## Architecture

```text
com.podzilla.orders
│
├── controller/ # REST endpoints
├── service/ # Business logic
├── model/ # Data models (Order, Status, etc.)
├── repository/ # DB access via JDBC
├── dto/ # Clean input/output objects
└── config/ # Configuration classes
```

## Installation and Running

### Prerequisites

Ensure the following are installed:

- Java 17
- Maven 3.8.6+
- Docker 24.0+
- MySQL 8.0+
- Redis 7.2+
- Git

---

### Setup

#### 1. Clone the Repository

```bash
git clone https://github.com/Podzilla/order.git
cd authentication
```
#### 2. Configure Environment
    
   
- Create a .env file based on .env.example and update with your postgreSQL  
credentials
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:5432/order_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password
```
#### 3. Build Project

```bash
mvn clean install
```

#### 4. Run Application
```bash
mvn spring-boot:run
```
The service will be available at ```http://localhost:8080```

#### 5. Run with Docker

```bash 
docker build -t podzilla-order .
docker run -p 8080:8080 -env-file .env podzilla-order
```

### 6. Access Swagger UI
Navigate to ```http://localhost:8080/swagger-ui.html``` to explore and test API endpoints.


