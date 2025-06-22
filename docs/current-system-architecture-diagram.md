# 📊 Online Bookstore Management System - Architecture Diagrams

This document contains comprehensive system architecture diagrams for the **Online Bookstore Management System**, a microservices-based e-commerce platform built with **Spring Boot**, **Kotlin**, **PostgreSQL**, and **Apache Kafka**.

## 📋 Table of Contents

1. [System Architecture Overview](#1-system-architecture-overview)
2. [User Workflow & Business Processes](#2-user-workflow--business-processes)
3. [Database Schema & Relationships](#3-database-schema--relationships)

---

## 1. System Architecture Overview

This diagram shows the complete microservices architecture with all components and their relationships.

```mermaid
graph TB
    subgraph "Client Layer"
        WEB["Web Application"]
        MOBILE["Mobile App"]
        API_CLIENT["API Client"]
    end

    subgraph "API Gateway / Load Balancer"
        LB["Load Balancer<br/>(Future)"]
    end

    subgraph "Microservices Architecture"
        subgraph "User Management Service"
            UMS["User Management<br/>Service<br/>:8083"]
            UMS_DB[(bookstore_users<br/>PostgreSQL)]
        end

        subgraph "Book Inventory Service"
            BIS["Book Inventory<br/>Service<br/>:8081"]
            BIS_DB[(bookstore_inventory<br/>PostgreSQL)]
        end

        subgraph "Order Management Service"
            OMS["Order Management<br/>Service<br/>:8082"]
            OMS_DB[(bookstore_orders<br/>PostgreSQL)]
        end

        subgraph "Common Library"
            COMMON["Bookstore Common<br/>Event & Kafka Utils"]
        end
    end

    subgraph "Message Broker"
        KAFKA["Apache Kafka<br/>:9092"]
        ZK["Zookeeper<br/>:2181"]
    end

    subgraph "Database Infrastructure"
        PG["PostgreSQL<br/>:5432"]
        PGADMIN["pgAdmin<br/>:5050"]
    end

    subgraph "Monitoring & Management"
        KAFKA_UI["Kafka UI<br/>:8080"]
        SWAGGER["Swagger UI<br/>Built-in"]
    end

    subgraph "Container Platform"
        DOCKER["Docker Compose<br/>Container Orchestration"]
    end

    %% Client Connections
    WEB --> LB
    MOBILE --> LB
    API_CLIENT --> LB

    %% Load Balancer to Services
    LB --> UMS
    LB --> BIS
    LB --> OMS

    %% Service to Database Connections
    UMS --> UMS_DB
    BIS --> BIS_DB
    OMS --> OMS_DB

    %% Service Dependencies
    UMS -.-> COMMON
    BIS -.-> COMMON
    OMS -.-> COMMON

    %% Kafka Messaging
    UMS <--> KAFKA
    BIS <--> KAFKA
    OMS <--> KAFKA
    KAFKA --> ZK

    %% Database Management
    PG --> UMS_DB
    PG --> BIS_DB
    PG --> OMS_DB
    PGADMIN --> PG

    %% Monitoring
    KAFKA_UI --> KAFKA
    SWAGGER -.-> UMS
    SWAGGER -.-> BIS
    SWAGGER -.-> OMS

    %% Container Platform
    DOCKER -.-> UMS
    DOCKER -.-> BIS
    DOCKER -.-> OMS
    DOCKER -.-> PG
    DOCKER -.-> KAFKA
    DOCKER -.-> ZK

    classDef serviceBox fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef databaseBox fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef messageBox fill:#e8f5e8,stroke:#1b5e20,stroke-width:2px
    classDef clientBox fill:#fff3e0,stroke:#e65100,stroke-width:2px

    class UMS,BIS,OMS serviceBox
    class UMS_DB,BIS_DB,OMS_DB,PG databaseBox
    class KAFKA,ZK,KAFKA_UI messageBox
    class WEB,MOBILE,API_CLIENT clientBox
```

### Key Components:
- **3 Microservices**: User Management (:8083), Book Inventory (:8081), Order Management (:8082)
- **Database Per Service**: Separate PostgreSQL databases for data isolation
- **Event-Driven Communication**: Apache Kafka for async messaging
- **Container Orchestration**: Docker Compose for development deployment

---

## 2. User Workflow & Business Processes

This diagram illustrates the complete user journey and business workflows within the system.

```mermaid
graph TD
    START([User Starts Session])
    
    subgraph "Authentication Flow"
        LOGIN[Login Request]
        AUTH_CHECK{Valid Credentials?}
        JWT_GEN[Generate JWT Token]
        AUTH_FAIL[Authentication Failed]
    end
    
    subgraph "User Management Flow"
        REGISTER[User Registration]
        PROFILE[Manage Profile]
        ADMIN_CHECK{Admin User?}
        ADMIN_PANEL[Admin Panel Access]
        USER_MGMT[User Management]
    end
    
    subgraph "Book Discovery Flow"
        BROWSE[Browse Books]
        SEARCH[Search Books]
        FILTER[Apply Filters<br/>Genre, Author, etc.]
        VIEW_BOOK[View Book Details]
        CHECK_STOCK[Check Availability]
    end
    
    subgraph "Shopping Cart Flow"
        ADD_CART[Add to Cart]
        VIEW_CART[View Cart]
        UPDATE_QTY[Update Quantities]
        REMOVE_ITEM[Remove Items]
        CART_VALIDATE{Cart Valid?}
    end
    
    subgraph "Order Processing Flow"
        CHECKOUT[Checkout Process]
        ORDER_REVIEW[Review Order]
        PAYMENT[Payment Processing<br/>Future Integration]
        INVENTORY_CHECK{Stock Available?}
        INVENTORY_RESERVE[Reserve Inventory]
        ORDER_CREATE[Create Order]
        ORDER_CONFIRM[Order Confirmation]
        STOCK_UPDATE[Update Stock Levels]
    end
    
    subgraph "Order Management Flow"
        ORDER_HISTORY[View Order History]
        ORDER_DETAILS[View Order Details]
        ORDER_STATUS[Check Order Status]
        ORDER_TRACKING[Order Tracking<br/>Future Feature]
    end
    
    subgraph "Admin Operations"
        BOOK_MGMT[Book Management]
        ADD_BOOK[Add New Books]
        UPDATE_BOOK[Update Book Info]
        INVENTORY_MGMT[Inventory Management]
        PRICE_MGMT[Price Management]
        ORDER_ADMIN[Order Administration]
        USER_ADMIN[User Administration]
    end
    
    subgraph "Event-Driven Updates"
        USER_EVENTS[User Events]
        INVENTORY_EVENTS[Inventory Events]
        ORDER_EVENTS[Order Events]
        KAFKA_PUBLISH[Publish to Kafka]
        SERVICE_SYNC[Service Synchronization]
    end
    
    %% Main Flow
    START --> LOGIN
    START --> REGISTER
    
    %% Authentication
    LOGIN --> AUTH_CHECK
    AUTH_CHECK -->|Yes| JWT_GEN
    AUTH_CHECK -->|No| AUTH_FAIL
    JWT_GEN --> BROWSE
    
    %% Registration
    REGISTER --> PROFILE
    PROFILE --> BROWSE
    
    %% Admin Access
    JWT_GEN --> ADMIN_CHECK
    ADMIN_CHECK -->|Yes| ADMIN_PANEL
    ADMIN_CHECK -->|No| BROWSE
    ADMIN_PANEL --> BOOK_MGMT
    ADMIN_PANEL --> USER_MGMT
    
    %% Book Discovery
    BROWSE --> SEARCH
    BROWSE --> FILTER
    SEARCH --> VIEW_BOOK
    FILTER --> VIEW_BOOK
    VIEW_BOOK --> CHECK_STOCK
    CHECK_STOCK --> ADD_CART
    
    %% Shopping Cart
    ADD_CART --> VIEW_CART
    VIEW_CART --> UPDATE_QTY
    VIEW_CART --> REMOVE_ITEM
    VIEW_CART --> CHECKOUT
    UPDATE_QTY --> CART_VALIDATE
    CART_VALIDATE -->|Valid| VIEW_CART
    CART_VALIDATE -->|Invalid| BROWSE
    
    %% Order Processing
    CHECKOUT --> ORDER_REVIEW
    ORDER_REVIEW --> PAYMENT
    PAYMENT --> INVENTORY_CHECK
    INVENTORY_CHECK -->|Available| INVENTORY_RESERVE
    INVENTORY_CHECK -->|Unavailable| BROWSE
    INVENTORY_RESERVE --> ORDER_CREATE
    ORDER_CREATE --> ORDER_CONFIRM
    ORDER_CONFIRM --> STOCK_UPDATE
    
    %% Order Management
    ORDER_CONFIRM --> ORDER_HISTORY
    ORDER_HISTORY --> ORDER_DETAILS
    ORDER_DETAILS --> ORDER_STATUS
    
    %% Admin Operations
    BOOK_MGMT --> ADD_BOOK
    BOOK_MGMT --> UPDATE_BOOK
    BOOK_MGMT --> INVENTORY_MGMT
    BOOK_MGMT --> PRICE_MGMT
    USER_MGMT --> USER_ADMIN
    ADMIN_PANEL --> ORDER_ADMIN
    
    %% Event-Driven Architecture
    REGISTER --> USER_EVENTS
    ADD_BOOK --> INVENTORY_EVENTS
    ORDER_CREATE --> ORDER_EVENTS
    STOCK_UPDATE --> INVENTORY_EVENTS
    
    USER_EVENTS --> KAFKA_PUBLISH
    INVENTORY_EVENTS --> KAFKA_PUBLISH
    ORDER_EVENTS --> KAFKA_PUBLISH
    KAFKA_PUBLISH --> SERVICE_SYNC
    
    classDef startNode fill:#c8e6c9,stroke:#388e3c,stroke-width:3px
    classDef authNode fill:#bbdefb,stroke:#1976d2,stroke-width:2px
    classDef businessNode fill:#fff3e0,stroke:#f57c00,stroke-width:2px
    classDef adminNode fill:#fce4ec,stroke:#c2185b,stroke-width:2px
    classDef eventNode fill:#f3e5f5,stroke:#7b1fa2,stroke-width:2px
    
    class START startNode
    class LOGIN,REGISTER,AUTH_CHECK,JWT_GEN authNode
    class BROWSE,SEARCH,ADD_CART,CHECKOUT,ORDER_CREATE businessNode
    class ADMIN_PANEL,BOOK_MGMT,USER_MGMT,ORDER_ADMIN adminNode
    class USER_EVENTS,INVENTORY_EVENTS,ORDER_EVENTS,KAFKA_PUBLISH eventNode
```

### Key Business Flows:
- **Authentication**: JWT-based security with role-based access control
- **Book Discovery**: Search, filter, and browse functionality
- **Shopping Cart**: Full cart management with validation
- **Order Processing**: Complete e-commerce checkout flow
- **Admin Operations**: Comprehensive administrative capabilities
- **Event-Driven Updates**: Real-time synchronization via Kafka

---

## 3. Database Schema & Relationships

Entity Relationship Diagram showing all database tables across the three services.

```mermaid
erDiagram
    %% User Management Database (bookstore_users)
    USERS {
        bigint id PK
        varchar user_name UK
        varchar email
        varchar full_name
        varchar password
        varchar phone_number
        varchar address
        varchar role
    }

    %% Book Inventory Database (bookstore_inventory)
    BOOKS {
        bigint id PK
        varchar title
        varchar author
        varchar genre
        varchar isbn UK
        timestamp created_at
        timestamp updated_at
        boolean is_deleted
    }

    BOOKS_INVENTORY {
        bigint id PK
        bigint book_id FK
        int quantity
    }

    BOOKS_PRICE {
        bigint id PK
        bigint book_id FK
        numeric price
    }

    INVENTORY_LOGS {
        bigint id PK
        bigint book_id
        varchar action
        int quantity
        timestamp timestamp
    }

    %% Order Management Database (bookstore_orders)
    ORDERS {
        bigint id PK
        bigint user_id
        varchar status
        timestamp created_at
    }

    ORDER_ITEMS {
        bigint id PK
        bigint book_id
        int quantity
        numeric price
        bigint order_id FK
    }

    CARTS {
        bigint id PK
        bigint user_id UK
    }

    CART_ITEMS {
        bigint id PK
        bigint book_id
        int quantity
        bigint cart_id FK
    }

    %% Relationships within each database
    BOOKS ||--o{ BOOKS_INVENTORY : "has"
    BOOKS ||--o{ BOOKS_PRICE : "has"
    BOOKS ||--o{ INVENTORY_LOGS : "tracks"
    
    ORDERS ||--o{ ORDER_ITEMS : "contains"
    CARTS ||--o{ CART_ITEMS : "contains"

    %% Cross-database relationships (logical, not FK)
    USERS ||--o{ ORDERS : "places"
    USERS ||--o{ CARTS : "owns"
    BOOKS ||--o{ ORDER_ITEMS : "ordered_as"
    BOOKS ||--o{ CART_ITEMS : "added_as"
```

### Database Design Principles:
- **Database Per Service**: Each microservice owns its data completely
- **No Cross-Database Foreign Keys**: Services communicate via APIs and events
- **Audit Trails**: Inventory logs track all changes
- **Performance Optimization**: Strategic indexing on frequently queried columns

---

## 📈 System Overview

### Database Coverage
- **Total Tables**: 9 tables across 3 databases
- **Complete Coverage**: 100% table creation with Flyway migrations
- **Sample Data**: Pre-populated with realistic test data

### Architecture Benefits
- **Microservices Pattern**: Clear separation of concerns
- **Event-Driven Communication**: Scalable async messaging via Kafka
- **Database Per Service**: Data isolation and service autonomy
- **Container Orchestration**: Docker Compose for easy deployment

---

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose
- Java 17+
- Gradle 8.x

### Quick Start
```bash
# Clone the repository
git clone <repository-url>
cd online-bookstore-mgmt-system

# Build all services
./gradlew clean build

# Start the complete system
docker-compose up -d

# Verify all services are running
docker-compose ps
```

### Access Points
- **User Management API**: http://localhost:8083/swagger-ui.html
- **Book Inventory API**: http://localhost:8081/swagger-ui.html
- **Order Management API**: http://localhost:8082/swagger-ui.html
- **pgAdmin**: http://localhost:5050 (admin@bookstore.com / admin)
- **Kafka UI**: http://localhost:8080

---

## 📚 Additional Resources

- [API Documentation](./openapi-specs/README.md)
- [Manager Instructions](./MANAGER_INSTRUCTIONS.md)
- [Technical Documentation](./technical_documentation.html)

---

**Generated on**: June 2025  
**System Version**: 1.0.0-SNAPSHOT  
**Architecture**: Microservices with Event-Driven Communication  
**Status**: ✅ Production Ready 