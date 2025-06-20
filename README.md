# Online Bookstore Management System - Comprehensive Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Services](#services)
4. [API Documentation](#api-documentation)
5. [Database Schema](#database-schema)
6. [Event-Driven Architecture](#event-driven-architecture)
7. [Security](#security)
8. [Setup and Installation](#setup-and-installation)
9. [Development Guide](#development-guide)
10. [Testing](#testing)
11. [Deployment](#deployment)
12. [Monitoring and Health Checks](#monitoring-and-health-checks)
13. [Troubleshooting](#troubleshooting)

## Project Overview

The Online Bookstore Management System is a modern, microservices-based application built with Kotlin and Spring Boot. It provides a complete solution for managing an online bookstore, including user management, book inventory, shopping cart functionality, and order processing.

### Key Features
- **User Management**: Registration, authentication, and role-based access control
- **Book Inventory**: Complete book catalog management with search capabilities
- **Shopping Cart**: Add, remove, and manage items in user carts
- **Order Management**: Process orders from cart to completion
- **Event-Driven Architecture**: Kafka-based inter-service communication
- **Security**: JWT-based authentication and authorization
- **API Documentation**: Comprehensive Swagger/OpenAPI documentation
- **Database Management**: PostgreSQL with automated migrations
- **Monitoring**: Health checks and metrics endpoints

### Technology Stack
- **Backend**: Kotlin 1.9.22, Spring Boot 3.2.3
- **Database**: PostgreSQL 16
- **Message Broker**: Apache Kafka 7.4.0
- **Security**: Spring Security with JWT
- **Testing**: JUnit 5, MockK
- **API Documentation**: SpringDoc OpenAPI 3
- **Build Tool**: Gradle
- **Containerization**: Docker & Docker Compose

## Architecture

### Microservices Architecture
The system follows a microservices architecture pattern with three main services:

1. **Book Inventory Service** (Port: 8081)
   - Manages book inventory
   - Handles book metadata and stock information
   - PostgreSQL database: bookstore_inventory

2. **Order Management Service** (Port: 8082)
   - Processes customer orders
   - Manages order lifecycle
   - Integrates with Kafka for event-driven architecture
   - PostgreSQL database: bookstore_orders

3. **User Management Service** (Port: 8083)
   - Handles user authentication and authorization
   - Manages user profiles and roles
   - JWT-based security
   - PostgreSQL database: bookstore_users

## Prerequisites

- JDK 17
- Kotlin 1.9.22
- Docker and Docker Compose
- PostgreSQL 14+ (if running locally)
- Apache Kafka (if running locally)

## Environment Variables

The following environment variables can be configured:

- `DB_USERNAME` - PostgreSQL username (default: postgres)
- `DB_PASSWORD` - PostgreSQL password (default: postgres)
- `JWT_SECRET` - Secret key for JWT token generation

## Docker Setup

The project includes a Docker Compose configuration for running all required infrastructure:

1. Start the infrastructure services:
   ```bash
   # Make the database initialization script executable
   chmod +x docker/postgres/init-multiple-dbs.sh
   
   # Start all services
   docker-compose up -d
   ```

This will start:
- PostgreSQL with three databases (bookstore_inventory, bookstore_orders, bookstore_users)
- Kafka and Zookeeper for event streaming
- Kafka UI for monitoring Kafka (available at http://localhost:8080)

Note about database initialization:
- The database initialization script (`init-multiple-dbs.sh`) runs automatically when PostgreSQL starts for the first time
- It only runs if the PostgreSQL data directory is empty (first initialization)
- If you need to reinitialize the databases, you must remove the existing volume:
  ```bash
  # Stop services and remove volumes
  docker-compose down -v
  
  # Start services again (this will trigger database initialization)
  docker-compose up -d
  ```

2. Stop the services:
   ```bash
   docker-compose down
   ```

3. Stop the services and remove volumes:
   ```bash
   docker-compose down -v
   ```

## Building the Project

```bash
./gradlew clean build
```

## Running the Services

### Option 1: Running All Services Together

1. Start all infrastructure services using Docker Compose:
   ```bash
   docker-compose up -d
   ```

2. Build all services:
   ```bash
   ./gradlew clean build
   ```

3. Run all services in parallel (open separate terminal windows for each command):
   ```bash
   # Terminal 1 - Book Inventory Service (Port 8081)
   ./gradlew :book-inventory-service:bootRun

   # Terminal 2 - Order Management Service (Port 8082)
   ./gradlew :order-management-service:bootRun

   # Terminal 3 - User Management Service (Port 8083)
   ./gradlew :user-management-service:bootRun
   ```

### Option 2: Running Services Individually

You can run each service independently based on your needs. Make sure the required infrastructure services are running first.

#### Book Inventory Service (Port 8081)
```bash
# Build and run only Book Inventory Service
./gradlew :book-inventory-service:clean :book-inventory-service:build
./gradlew :book-inventory-service:bootRun
```

#### Order Management Service (Port 8082)
```bash
# Build and run only Order Management Service
./gradlew :order-management-service:clean :order-management-service:build
./gradlew :order-management-service:bootRun
```

#### User Management Service (Port 8083)
```bash
# Build and run only User Management Service
./gradlew :user-management-service:clean :user-management-service:build
./gradlew :user-management-service:bootRun
```

### Verifying Services

After starting the services, you can verify they are running:

1. Book Inventory Service: http://localhost:8081/actuator/health
2. Order Management Service: http://localhost:8082/actuator/health
3. User Management Service: http://localhost:8083/actuator/health

### Stopping Services

1. To stop individual services:
   - Press `Ctrl+C` in the respective terminal window

2. To stop all infrastructure services:
   ```bash
   docker-compose down
   ```

3. To stop and remove all containers and volumes:
   ```bash
   docker-compose down -v
   ```

## Testing

Run tests for all services:
```bash
./gradlew test
```

## Technologies Used

- Kotlin
- Spring Boot 3.2.3
- Spring Data JPA
- Spring Security
- PostgreSQL
- Apache Kafka
- JWT Authentication
- Gradle (Kotlin DSL)
- JUnit 5 & MockK for testing
- Docker & Docker Compose

## Database Management

### Connecting to PostgreSQL using pgAdmin

1. **Access pgAdmin**:
   - Open your browser and go to `http://localhost:5050`
   - Login credentials:
     - Email: `admin@bookstore.com`
     - Password: `admin`

2. **Add PostgreSQL Server**:
   1. Right-click on "Servers" in the left panel
   2. Choose "Register" → "Server"
   3. Fill in the following details:

   **General Tab**:
   - Name: `bookstore_postgres` (or any name you prefer)

   **Connection Tab**:
   - Host name/address: `postgres`
   - Port: `5432`
   - Maintenance database: `postgres`
   - Username: `postgres`
   - Password: `postgres`

3. **Verify Connection**:
   - After clicking "Save", you should see these databases:
     - bookstore_inventory
     - bookstore_orders
     - bookstore_users

4. **Troubleshooting**:
   - If connection fails, ensure all containers are running:
     ```bash
     docker-compose ps
     ```
   - Check container logs:
     ```bash
     docker-compose logs postgres
     docker-compose logs pgadmin
     ```
   - Restart services if needed:
     ```bash
     docker-compose restart postgres pgadmin
     ``` 

## Services

### 1. User Management Service (Port 8083)

**Purpose**: Handles user authentication, authorization, and user profile management.

**Key Features**:
- User registration and login
- JWT token generation and validation
- Role-based access control (USER, ADMIN, SUPERADMIN)
- User profile management
- Admin user management

**Database**: `bookstore_users`

**Key Endpoints**:
- `POST /api/auth/login` - User authentication
- `GET /api/users` - List all users
- `POST /api/users` - Create new user
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `POST /api/admins` - Create admin user

### 2. Book Inventory Service (Port 8081)

**Purpose**: Manages book catalog, inventory, and search functionality.

**Key Features**:
- Book CRUD operations
- Inventory management (stock levels)
- Advanced book search with filters
- Low stock alerts
- Soft delete functionality

**Database**: `bookstore_inventory`

**Key Endpoints**:
- `GET /api/books` - List all books
- `POST /api/books` - Create new book
- `GET /api/books/{id}` - Get book by ID
- `PUT /api/books/{id}` - Update book
- `DELETE /api/books/{id}` - Soft delete book
- `POST /api/books/search` - Advanced book search
- `GET /api/inventory/status/{bookId}` - Get inventory status
- `POST /api/inventory/update` - Update inventory
- `GET /api/inventory/low-stock` - List low stock books

### 3. Order Management Service (Port 8082)

**Purpose**: Handles shopping cart and order processing functionality.

**Key Features**:
- Shopping cart management
- Order creation and processing
- Order status tracking
- Order history
- Payment confirmation

**Database**: `bookstore_orders`

**Key Endpoints**:
- `POST /cart/add` - Add item to cart
- `POST /cart/remove` - Remove item from cart
- `GET /cart/{userId}` - View user's cart
- `DELETE /cart/{userId}` - Clear cart
- `POST /orders` - Create new order
- `GET /orders/{orderId}/status` - Get order status
- `GET /orders/user/{userId}` - Get user's order history
- `POST /orders/{orderId}/confirm-payment` - Confirm payment
- `POST /orders/from-cart/{userId}` - Create order from cart

## API Documentation

### Authentication
All secured endpoints require a Bearer JWT token in the Authorization header:
```
Authorization: Bearer <jwt-token>
```

### Role-Based Access Control
- **USER**: Can access cart and order endpoints
- **ADMIN**: Can manage books and inventory + USER permissions
- **SUPERADMIN**: Full system access

### Swagger Documentation
Each service provides interactive API documentation:
- User Management: http://localhost:8083/swagger-ui.html
- Book Inventory: http://localhost:8081/swagger-ui.html
- Order Management: http://localhost:8082/swagger-ui.html

### Sample API Requests

#### User Authentication
```bash
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "superadmin",
    "password": "password"
  }'
```

#### Create Book (Requires ADMIN/SUPERADMIN)
```bash
curl -X POST http://localhost:8081/api/books \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Sample Book",
    "author": "Author Name",
    "isbn": "978-1234567890",
    "price": 29.99,
    "stockQuantity": 100,
    "description": "Book description"
  }'
```

#### Add to Cart (Requires USER+)
```bash
curl -X POST http://localhost:8082/cart/add \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "bookId": 1,
    "quantity": 2
  }'
```

## Database Schema

### User Management Database (`bookstore_users`)
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USERS'
);
```

### Book Inventory Database (`bookstore_inventory`)
```sql
-- Books table (managed by JPA/Hibernate)
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(13) UNIQUE NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    description TEXT,
    deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Order Management Database (`bookstore_orders`)
```sql
-- Carts table
CREATE TABLE carts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Cart Items table
CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    cart_id BIGINT REFERENCES carts(id),
    book_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Orders table
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Order Items table
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id),
    book_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10,2) NOT NULL
);
```

## Event-Driven Architecture

### Kafka Topics and Events
The system uses Apache Kafka for asynchronous communication between services.

#### Event Message Structure
```kotlin
data class EventMessage(
    val eventType: String,
    val payload: Map<String, Any?>
)
```

#### Common Event Types
- `USER_CREATED` - Published when a new user is created
- `ORDER_CREATED` - Published when an order is placed
- `INVENTORY_UPDATED` - Published when book inventory changes
- `PAYMENT_CONFIRMED` - Published when payment is confirmed

#### Kafka Configuration
- **Bootstrap Servers**: `kafka:9092` (Docker) / `localhost:29092` (Local)
- **Topics**: Auto-created with single partition and replication factor 1
- **Consumer Groups**: Each service has its own consumer group

## Security

### JWT Authentication
- **Algorithm**: HS256
- **Expiration**: 24 hours (86400000 ms)
- **Secret**: Configurable via `JWT_SECRET` environment variable

### Password Security
- **Encoding**: BCrypt with strength 10
- **Storage**: Hashed passwords only, never plain text

### Role Hierarchy
1. **SUPERADMIN**: Full system access
2. **ADMIN**: Can manage books, inventory, and users
3. **USER**: Can access cart and order functionality

### Security Configuration
Each service has its own security configuration:
- CSRF disabled for REST APIs
- JWT filter for token validation
- Method-level security with `@PreAuthorize`

## Setup and Installation

### Prerequisites
- JDK 17 or higher
- Docker and Docker Compose
- Git

### Quick Start
1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd online-bookstore-mgmt-system
   ```

2. **Start Infrastructure Services**
   ```bash
   # Make initialization script executable (Linux/Mac)
   chmod +x docker/postgres/init-multiple-dbs.sh
   
   # Start all infrastructure
   docker-compose up -d
   ```

3. **Build the Project**
   ```bash
   ./gradlew clean build
   ```

4. **Run Services**
   ```bash
   # Terminal 1 - User Management Service
   ./gradlew :user-management-service:bootRun
   
   # Terminal 2 - Book Inventory Service  
   ./gradlew :book-inventory-service:bootRun
   
   # Terminal 3 - Order Management Service
   ./gradlew :order-management-service:bootRun
   ```

5. **Verify Installation**
   - User Management: http://localhost:8083/actuator/health
   - Book Inventory: http://localhost:8081/actuator/health
   - Order Management: http://localhost:8082/actuator/health

### Environment Variables
```bash
# Database Configuration
DB_USERNAME=postgres
DB_PASSWORD=postgres

# JWT Configuration
JWT_SECRET=your-secret-key-here

# Kafka Configuration
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092
```

## Development Guide

### Project Structure
```
online-bookstore-mgmt-system/
├── bookstore-common/           # Shared libraries
├── user-management-service/    # User & Auth service
├── book-inventory-service/     # Book & Inventory service
├── order-management-service/   # Cart & Order service
├── docker/                     # Docker configurations
├── docker-compose.yml          # Infrastructure setup
└── build.gradle               # Root build configuration
```

### Adding New Features
1. **Create DTOs** in the appropriate `dto` package
2. **Create Entities** in the `model` package
3. **Create Repository** interfaces extending JpaRepository
4. **Implement Service** layer with business logic
5. **Create Controller** with REST endpoints
6. **Add Tests** for all layers
7. **Update API Documentation** with Swagger annotations

### Code Style
- **Language**: Kotlin with functional programming principles
- **Framework**: Spring Boot with annotations
- **Testing**: JUnit 5 with MockK for mocking
- **Documentation**: KDoc for Kotlin code documentation

### Database Migrations
- **Tool**: Hibernate DDL auto-update (development)
- **Production**: Consider using Flyway for production migrations
- **Initialization**: Custom SQL scripts in `docker/postgres/`

## Testing

### Test Structure
Each service has comprehensive test coverage:
- **Unit Tests**: Service layer testing with mocked dependencies
- **Integration Tests**: Controller layer testing with MockMvc
- **Repository Tests**: Data layer testing with H2 in-memory database

### Running Tests
```bash
# Run all tests
./gradlew test

# Run tests for specific service
./gradlew :user-management-service:test
./gradlew :book-inventory-service:test
./gradlew :order-management-service:test

# Generate test coverage report
./gradlew jacocoTestReport
```

### Test Coverage
Test reports are generated in:
- HTML: `build/reports/jacoco/test/html/index.html`
- XML: `build/reports/jacoco/test/jacocoTestReport.xml`

## Deployment

### Docker Deployment
The project includes Dockerfile for containerized deployment:

```bash
# Build all services as Docker images
docker-compose build

# Run complete system with Docker
docker-compose up -d
```

### Production Considerations
1. **Environment Variables**: Use proper secrets management
2. **Database**: Use managed PostgreSQL service
3. **Kafka**: Use managed Kafka service (e.g., Confluent Cloud)
4. **Load Balancing**: Use reverse proxy (Nginx, HAProxy)
5. **Monitoring**: Integrate with monitoring solutions
6. **Logging**: Centralized logging with ELK stack

### Kubernetes Deployment
For Kubernetes deployment, create:
- Deployment manifests for each service
- Service manifests for internal communication
- Ingress for external access
- ConfigMaps for configuration
- Secrets for sensitive data

## Monitoring and Health Checks

### Health Endpoints
Each service exposes health check endpoints:
- `/actuator/health` - Service health status
- `/actuator/info` - Service information
- `/actuator/metrics` - Service metrics

### Database Monitoring
Access pgAdmin at http://localhost:5050:
- **Email**: admin@bookstore.com
- **Password**: admin

### Kafka Monitoring
Access Kafka UI at http://localhost:8080 for:
- Topic management
- Message monitoring
- Consumer group status

### Application Metrics
Spring Boot Actuator provides metrics for:
- HTTP requests
- Database connections
- JVM metrics
- Custom business metrics

## Troubleshooting

### Common Issues

#### 1. Database Connection Issues
```bash
# Check if PostgreSQL is running
docker-compose ps postgres

# Check database logs
docker-compose logs postgres

# Restart database
docker-compose restart postgres
```

#### 2. Kafka Connection Issues
```bash
# Check Kafka status
docker-compose ps kafka zookeeper

# Check Kafka logs
docker-compose logs kafka

# Restart Kafka services
docker-compose restart zookeeper kafka
```

#### 3. Service Startup Issues
```bash
# Check service logs
./gradlew :user-management-service:bootRun --debug

# Verify database connectivity
curl http://localhost:8083/actuator/health
```

#### 4. Authentication Issues
- Verify JWT token is valid and not expired
- Check user roles and permissions
- Ensure proper Authorization header format

### Performance Tuning
1. **Database**: Add appropriate indexes
2. **JVM**: Tune heap size and garbage collection
3. **Connection Pools**: Configure optimal pool sizes
4. **Caching**: Implement Redis for frequently accessed data

### Logging Configuration
Adjust logging levels in `application.yml`:
```yaml
logging:
  level:
    com.bookstore: DEBUG
    org.springframework.security: INFO
    org.hibernate.SQL: DEBUG
```

## Contributing

### Development Workflow
1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

### Code Review Guidelines
- Ensure all tests pass
- Follow Kotlin coding conventions
- Add appropriate documentation
- Update API documentation if needed

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Support

For support and questions:
- **Email**: your@email.com
- **Documentation**: Check this README and Swagger docs
- **Issues**: Create GitHub issues for bugs and feature requests

---

*Last Updated: [Current Date]*
*Version: 1.0.0* 