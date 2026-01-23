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

2. **Update the configuration**

Open `application-dev.properties` and fill in your local setup details:

- **Database**: URL, username, and password  
- **JWT**: secret key  
- **Email service**: API keys (e.g., Brevo)

### 4. Build the Application

```bash
mvn clean install
```
### 5. Run the Application

```bash
mvn spring-boot:run
```

### 6. Run Tests

```bash
mvn test
```

src/<br>
├── main/<br>
│   ├── java/com/whytelabeltech/finbooks/<br>
│   │   ├── app/<br>
│   │   │   ├── author/          # Author management<br>
│   │   │   ├── book/            # Book management<br>
│   │   │   ├── category/        # Category management<br>
│   │   │   ├── password/        # password management<br>
│   │   │   ├── review/          # Review management<br>
│   │   │   ├── user/            # User management<br>
│   │   │   └── shared/          # Shared components<br>
│   │   ├── middleware/<br>
│   │   │   ├── security/        # JWT, authentication<br>
│   │   │   ├── exception/       # Exception handling<br>
│   │   └── FinbooksApplication.java<br>
│   └── resources/<br>
│       ├── application.properties<br>
│       ├── application-dev.properties<br>
│       └── application-dev-example.properties<br>
└── test/                        # Test files<br>

## 🎨 Design Decisions

1. **Feathered Monolithic Architecture**  
   The application follows a **feathered monolithic architecture**, where each domain (e.g., `book`, `author`, `review`, `user`) is organized into its own module or package within a single Spring Boot application.  
   - **Reason**: This approach allows for a clear separation of concerns, easier maintenance, and faster development compared to a full microservices setup, while still keeping the project deployable as a single unit. Each “feather” can later be split into microservices if needed, giving flexibility for scaling.

2. **Spring Boot & JPA**  
   Chosen for rapid development, production-ready REST APIs, and database interaction via ORM.


### 🔐 Security Configuration

#### User Roles
- **ADMIN**: Full access to all endpoints  
- **USER**: Can read data, create reviews, and manage their own profile  

#### JWT Authentication
1. Login to obtain a JWT token.  
2. Include the token in the `Authorization` header for protected endpoints:  






