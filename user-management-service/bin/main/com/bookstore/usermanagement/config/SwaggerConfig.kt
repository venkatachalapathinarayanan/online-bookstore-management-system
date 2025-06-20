package com.bookstore.usermanagement.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(
        title = "📱 User Management Service API",
        version = "1.0.0",
        description = """
            # User Management Service API
            
            Complete user management system with authentication and authorization.
            
            ## Features
            - 🔐 **User Authentication:** Login with JWT token generation
            - 👥 **User Management:** CRUD operations for regular users
            - 👨‍💼 **Admin Management:** CRUD operations for admin users
            - 🛡️ **Security:** JWT-based authentication with role-based access control
            
            ## Authentication
            Most endpoints require authentication using a Bearer JWT token:
            1. Login via `/api/auth/login` to get your JWT token
            2. Include the token in the Authorization header: `Bearer <your-jwt-token>`
            
            ## Roles
            - **USER:** Can manage their own profile
            - **ADMIN:** Can manage all users and admin accounts
            
            ## Quick Start
            1. Create a user account via `POST /api/users`
            2. Login via `POST /api/auth/login` to get JWT token
            3. Use the token to access protected endpoints
        """,
        contact = Contact(
            name = "Bookstore Development Team", 
            email = "support@bookstore.com", 
            url = "https://bookstore.com"
        ),
        license = License(
            name = "Apache 2.0", 
            url = "http://www.apache.org/licenses/LICENSE-2.0.html"
        )
    ),
    servers = [
        Server(url = "http://localhost:8081", description = "Local Development Server"),
        Server(url = "https://api.bookstore.com/user-service", description = "Production Server")
    ]
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer",
    description = "Enter JWT Bearer token in the format: Bearer <token>"
)
@Configuration
class SwaggerConfig

