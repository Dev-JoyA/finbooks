# FinBooks - Book Management System API

FinBooks is a comprehensive RESTful API for managing books, authors, categories, and user reviews. Built with Spring Boot and following microservices architecture principles.

---

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 
- MySQL 
- Git

### 1. Clone the Repository
```bash
git clone https://github.com/Dev-JoyA/finbooks
cd finbooks
```
## 2. Database Setup

Create a MySQL database for the application. For example:

```sql
CREATE DATABASE finbooks_db;
```

## 3. Configuration

1. **Copy the template configuration file**:

```bash
cp src/main/resources/application-dev-example.properties src/main/resources/application-dev.properties
```

2. **Update the configuration

Open `application-dev.properties` and fill in your local setup details:

- **Database**: URL, username, and password  
- **JWT**: secret key  
- **Email service**: API keys (e.g., Brevo)

### 4. Build the Application

```bash
# Clean and build the project
mvn clean install
```
### 5. Run the Application

```bash
# Run the Spring Boot application using Maven
mvn spring-boot:run
```

### 6. Run Tests

```bash
# Run all unit and integration tests using Maven
mvn test
```

src/
├── main/
│   ├── java/com/whytelabeltech/finbooks/
│   │   ├── app/
│   │   │   ├── author/          # Author management
│   │   │   ├── book/            # Book management
│   │   │   ├── category/        # Category management
│   │   │   ├── password/        # password management
│   │   │   ├── review/          # Review management
│   │   │   ├── user/            # User management
│   │   │   └── shared/          # Shared components
│   │   ├── middleware/
│   │   │   ├── security/        # JWT, authentication
│   │   │   ├── exception/       # Exception handling
│   │   └── FinbooksApplication.java
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       └── application-dev-example.properties
└── test/                        # Test files

### 🔐 Security Configuration

#### User Roles
- **ADMIN**: Full access to all endpoints  
- **USER**: Can read data, create reviews, and manage their own profile  

#### JWT Authentication
1. Login to obtain a JWT token.  
2. Include the token in the `Authorization` header for protected endpoints:  

#### API Documentation
All endpoints are documented using **Swagger**.  
- After running the application, access the Swagger UI at: [Swagger API Documentation](http://localhost:8080/swagger-ui/index.html)
- Use the Swagger interface to explore endpoints, see required parameters, and test API calls.




