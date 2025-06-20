import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
    info = Info(
        title = "Book Inventory Service API",
        version = "1.0.0",
        description = "API documentation for the Book Inventory Service. Provides endpoints for managing books, inventory, and more.\n\n**Authentication:**\nAll secured endpoints require a Bearer JWT token with one of the following roles: USER, ADMIN, SUPERADMIN.\n\n**Contact:**\nFor support, contact Your Name at your@email.com.",
        contact = Contact(name = "Your Name", email = "your@email.com", url = "https://your-company.com"),
        license = License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html")
    ),
    servers = [
        Server(url = "http://localhost:8082", description = "Local server")
    ]
)
@Configuration
class SwaggerConfig

