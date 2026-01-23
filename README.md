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

## 📌 API Endpoints Overview

The FinBooks API exposes REST endpoints to manage books, authors, categories, users, and reviews. All endpoints are secured with JWT authentication unless stated otherwise.

### 🔹 Authentication & Password Management

#### **Authentication**
| Method | Endpoint                  | Description |
|--------|---------------------------|-------------|
| POST   | /api/v1/auth/login        | Authenticate a user and return JWT & refresh token. |
| POST   | /api/v1/auth/refresh      | Refresh an existing JWT using a refresh token. |

**Notes:**
- The login endpoint returns a `LoginResponse` containing an access token and refresh token.
- Include the JWT token in the `Authorization` header for all protected endpoints:  
  `Authorization: Bearer <access_token>`

#### **Password Management**
| Method | Endpoint                        | Description |
|--------|---------------------------------|-------------|
| POST   | /password/forgot-password        | Initiate a password reset request (force reset). |
| GET    | /password/validate-otp           | Validate the OTP sent during password reset. |
| PUT    | /password/new-password           | Set a new password after validating OTP. |

**Notes:**
- Password endpoints are used to handle forgot/reset password workflows.
- Ensure OTP is validated before calling `/new-password`.


### 🔹 Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | /users | Register user  |
| GET    | /users/{id} | Get user by ID |
| PUT    | /users/{id} | Update user (self only) |
| DELETE | /users/{id} | Delete user (self or admin) |

### 🔹 Authors
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | /authors        | Get all authors |
| GET    | /authors/{id}   | Get author by ID |
| POST   | /authors        | Create a new author |
| PUT    | /authors/{id}   | Update an author |
| DELETE | /authors/{id}   | Delete an author |

### 🔹 Books
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | /books         | Get all books |
| GET    | /books/{id}    | Get book by ID |
| POST   | /books         | Create a new book |
| PUT    | /books/{id}    | Update a book |
| DELETE | /books/{id}    | Delete a book |
| POST | /books/{id}/reviews    | Create a book review |
| GET | /books/{id}/reviews    | Get a book review by ID |

### 🔹 Categories
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | /categories       | Get all categories |
| GET    | /categories/{id}  | Get category by ID |
| POST   | /categories       | Create a category |
| PUT    | /categories/{id}  | Update a category |
| DELETE | /categories/{id}  | Delete a category |

### 🔹 Reviews
| Method | Endpoint | Description |
|--------|----------|-------------|
| PUT    | /reviews/{id}          | Update a review  |
| DELETE | /reviews/{id}          | Delete a review  |


### 🔐 Security Configuration

#### User Roles
- **ADMIN**: Full access to all endpoints  
- **USER**: Can read data, create reviews, and manage their own profile  

#### JWT Authentication
1. Login to obtain a JWT token.  
2. Include the token in the `Authorization` header for protected endpoints:  






