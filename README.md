# Online Bookstore Management System

A modern, microservices-based online bookstore management system built with Kotlin, Spring Boot, and event-driven architecture.

## 🚀 Quick Start

```bash
# Clone the repository
git clone <repository-url>
cd online-bookstore-mgmt-system

# Start all services with Docker Compose
docker-compose up -d

# Build and run services
./gradlew clean build
./gradlew :book-inventory-service:bootRun
./gradlew :order-management-service:bootRun  
./gradlew :user-management-service:bootRun
```

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Services](#services)
- [Recent Updates & Fixes](#recent-updates--fixes)
- [API Documentation](#api-documentation)
- [Setup & Installation](#setup--installation)
- [Development Guide](#development-guide)
- [Testing](#testing)
- [Deployment](#deployment)
- [Documentation](#documentation)
- [Troubleshooting](#troubleshooting)

## 🎯 Overview

The Online Bookstore Management System is a comprehensive microservices-based application that provides:

- **User Management**: Registration, authentication, and role-based access control
- **Book Inventory**: Complete book catalog with search and batch operations
- **Order Management**: Cart management and order processing with real-time pricing
- **Event-Driven Communication**: Kafka-based inter-service messaging
- **Security**: JWT-based authentication with service-to-service communication
- **API Documentation**: Complete OpenAPI/Swagger documentation

### Key Features

✅ **Real-time Price Fetching**: Order service fetches current prices from inventory  
✅ **Batch Operations**: Efficient bulk price retrieval for multiple books  
✅ **Service-to-Service Authentication**: Secure inter-service communication  
✅ **Circuit Breaker Pattern**: Fault tolerance for external service calls  
✅ **Event-Driven Architecture**: Kafka-based event streaming  
✅ **Database Migrations**: Automated schema management with Flyway  
✅ **Containerized Deployment**: Docker Compose for easy setup  

## 🏗️ Architecture

### Microservices Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   User Mgmt     │    │  Book Inventory │    │ Order Management│
│   Service       │    │   Service       │    │    Service      │
│   (Port 8083)   │    │   (Port 8081)   │    │   (Port 8082)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │   PostgreSQL    │
                    │   (Port 5432)   │
                    └─────────────────┘
                                 │
                    ┌─────────────────┐
                    │   Apache Kafka  │
                    │   (Port 9092)   │
                    └─────────────────┘
```

### Technology Stack

- **Backend**: Kotlin 1.9.22, Spring Boot 3.2.3
- **Database**: PostgreSQL 16 with Flyway migrations
- **Message Broker**: Apache Kafka 7.4.0
- **Security**: Spring Security with JWT tokens
- **HTTP Client**: WebClient with circuit breaker pattern
- **Testing**: JUnit 5, MockK
- **API Documentation**: SpringDoc OpenAPI 3
- **Build Tool**: Gradle
- **Containerization**: Docker & Docker Compose

## 🔧 Services

### 1. Book Inventory Service (Port: 8081)
- **Purpose**: Manages book catalog, inventory, and pricing
- **Database**: `bookstore_inventory`
- **Key Features**:
  - Book CRUD operations
  - Advanced search functionality
  - **Batch price retrieval** (`/api/books/prices`)
  - Inventory management
  - Price management

### 2. Order Management Service (Port: 8082)
- **Purpose**: Handles shopping carts and order processing
- **Database**: `bookstore_orders`
- **Key Features**:
  - Cart management
  - Order creation from cart
  - **Real-time price fetching** from inventory service
  - Service-to-service authentication
  - Circuit breaker pattern for fault tolerance

### 3. User Management Service (Port: 8083)
- **Purpose**: User authentication and authorization
- **Database**: `bookstore_users`
- **Key Features**:
  - User registration and authentication
  - JWT token generation and validation
  - Role-based access control (USERS, ADMIN, SUPERADMIN)
  - User profile management

## 📚 API Documentation

### OpenAPI Specifications
Complete API documentation is available in the `docs/openapi-specs/` folder:

- [Book Inventory API](docs/openapi-specs/book-inventory-api.yml)
- [Order Management API](docs/openapi-specs/order-management-api.yml)  
- [User Management API](docs/openapi-specs/user-management-api.yml)

### Key Endpoints

#### Book Inventory Service
```
GET  /api/books                    # Get all books
GET  /api/books/{id}               # Get book by ID
POST /api/books                    # Create new book
PUT  /api/books/{id}               # Update book
DELETE /api/books/{id}             # Delete book
GET  /api/books/search             # Search books
POST /api/books/prices             # Get batch prices (NEW)
```

#### Order Management Service
```
GET  /api/cart                     # Get user cart
POST /api/cart/items               # Add item to cart
DELETE /api/cart/items/{bookId}    # Remove item from cart
POST /api/orders                   # Create order from cart
GET  /api/orders                   # Get user orders
```

#### User Management Service
```
POST /api/auth/login               # User login
POST /api/auth/register            # User registration
GET  /api/users/profile            # Get user profile
PUT  /api/users/profile            # Update user profile
```

## 🛠️ Setup & Installation

### Prerequisites
- JDK 17
- Docker & Docker Compose
- Gradle (optional, wrapper included)

### Quick Setup

1. **Clone and Navigate**:
   ```bash
   git clone <repository-url>
   cd online-bookstore-mgmt-system
   ```

2. **Start Infrastructure**:
   ```bash
   docker-compose up -d
   ```

3. **Build Services**:
   ```bash
   ./gradlew clean build
   ```

4. **Run Services**:
   ```bash
   # Terminal 1 - Book Inventory Service
   ./gradlew :book-inventory-service:bootRun
   
   # Terminal 2 - Order Management Service  
   ./gradlew :order-management-service:bootRun
   
   # Terminal 3 - User Management Service
   ./gradlew :user-management-service:bootRun
   ```

### Environment Configuration

Key environment variables:
```yaml
# Database
DB_USERNAME: postgres
DB_PASSWORD: postgres

# JWT Configuration
JWT_SECRET: your-secret-key
JWT_EXPIRATION: 86400

# Service URLs
BOOK_INVENTORY_SERVICE_URL: http://book-inventory-service:8081
```

## 🧪 Testing

### Run All Tests
```bash
./gradlew test
```

### Run Specific Service Tests
```bash
./gradlew :book-inventory-service:test
./gradlew :order-management-service:test
./gradlew :user-management-service:test
```

### Integration Tests
```bash
./gradlew integrationTest
```

## 🚀 Deployment

### Docker Deployment
```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Production Considerations
- Use external PostgreSQL database
- Configure proper JWT secrets
- Set up monitoring and logging
- Configure proper network security
- Use load balancers for high availability

## 📖 Documentation

### Comprehensive Documentation
The `docs/` folder contains detailed documentation:

#### Architecture & Strategy
- [Cloud Architecture Evolution](docs/cloud-architecture-evolution.md)
- [Cloud Strategy Evolution](docs/cloud-strategy-evolution.md)
- [AWS Cloud Native Architecture](docs/aws-cloud-native-architecture.md)
- [Cloud Agnostic Analysis](docs/cloud-agnostic-analysis.md)

#### Deployment & Operations
- [Cloud Agnostic Deployment Guide](docs/cloud-agnostic-deployment-guide.md)
- [Cloud Transformation Strategy](docs/cloud-transformation-strategy.md)
- [Current System Review](docs/current-system-review.md)
- [Current System Architecture Diagram](docs/current-system-architecture-diagram.md)

#### API Documentation
- [OpenAPI Specifications](docs/openapi-specs/)
  - [Book Inventory API](docs/openapi-specs/book-inventory-api.yml)
  - [Order Management API](docs/openapi-specs/order-management-api.yml)
  - [User Management API](docs/openapi-specs/user-management-api.yml)
  - [API Documentation Guide](docs/openapi-specs/README.md)

#### Management
- [Manager Instructions](docs/MANAGER_INSTRUCTIONS.md)

### Service Health Checks
- Book Inventory: http://localhost:8081/actuator/health
- Order Management: http://localhost:8082/actuator/health
- User Management: http://localhost:8083/actuator/health

### Monitoring Tools
- **Kafka UI**: http://localhost:8080
- **pgAdmin**: http://localhost:5050 (admin@bookstore.com / admin)

## 🔧 Development Guide

### Project Structure
```
online-bookstore-mgmt-system/
├── book-inventory-service/     # Book catalog and inventory
├── order-management-service/   # Orders and cart management
├── user-management-service/    # User authentication
├── bookstore-common/          # Shared components
├── docs/                      # Documentation
├── docker/                    # Docker configuration
└── docker-compose.yml         # Service orchestration
```

### Adding New Features
1. Create feature branch
2. Implement changes with tests
3. Update API documentation
4. Update this README if needed
5. Create pull request

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comprehensive tests
- Document public APIs

## 🐛 Troubleshooting

### Common Issues

#### 1. Database Connection Issues
```bash
# Check if PostgreSQL is running
docker-compose ps

# Restart database
docker-compose restart postgres
```

#### 2. Service Communication Issues
```bash
# Check service health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

#### 3. Kafka Issues
```bash
# Check Kafka status
docker-compose logs kafka

# Access Kafka UI
open http://localhost:8080
```

#### 4. Build Issues
```bash
# Clean and rebuild
./gradlew clean build

# Stop Gradle daemon if needed
./gradlew --stop
```

### Logs
```bash
# View all service logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f book-inventory-service
docker-compose logs -f order-management-service
docker-compose logs -f user-management-service
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Update documentation
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For issues and questions:
1. Check the [Troubleshooting](#troubleshooting) section
2. Review the [Documentation](#documentation) folder
3. Create an issue in the repository

---

**Built with ❤️ using Kotlin, Spring Boot, and modern microservices architecture** 