# Spring Boot Web Application

A comprehensive Spring Boot application demonstrating web development with modern technologies.

## 🏗️ Architecture

### Web & API Architecture
- **spring-boot-starter-web**: Standard MVC web framework with Tomcat embedded server
- **spring-boot-starter-webflux**: Reactive web stack providing WebClient for non-blocking HTTP calls

### Database
- **spring-boot-starter-data-mongodb**: MongoDB integration with Spring Data repositories

### Frontend / UI
- **spring-boot-starter-thymeleaf**: Server-side template engine for dynamic HTML rendering

### Security & Validation
- **spring-boot-starter-security**: Authentication and authorization framework
- **spring-boot-starter-validation**: Bean validation with Hibernate Validator

### Developer Tools
- **spring-boot-devtools**: Auto-restart and LiveReload for enhanced development experience

### Testing
- **spring-boot-starter-test**: Comprehensive testing suite (JUnit 5, Mockito, AssertJ)
- **spring-security-test**: Security testing utilities
- **reactor-test**: Testing support for reactive components

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **MongoDB** (running locally on port 27017 or configure connection in `application.properties`)

## 🚀 Getting Started

### 1. Clone or navigate to the project directory
```bash
cd d:\SPRINGBOOT_TRY
```

### 2. Configure MongoDB
Update `src/main/resources/application.properties` with your MongoDB connection:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/springboot_db
```

### 3. Install dependencies
```bash
mvn clean install
```

### 4. Run the application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## 📁 Project Structure

```
springboot-app/
├── src/
│   ├── main/
│   │   ├── java/com/example/app/
│   │   │   ├── Application.java          # Main entry point
│   │   │   └── controller/
│   │   │       ├── HomeController.java   # Web page controller
│   │   │       └── ApiController.java    # REST API controller
│   │   └── resources/
│   │       ├── application.properties    # Configuration
│   │       ├── templates/                # Thymeleaf templates
│   │       │   └── index.html
│   │       └── static/                   # Static resources
│   │           ├── css/style.css
│   │           └── js/main.js
│   └── test/                             # Test files
└── pom.xml                               # Maven dependencies
```

## 🔗 Endpoints

### Web Pages (Thymeleaf)
- `GET /` - Home page
- `GET /about` - About page

### REST API (JSON)
- `GET /api/hello` - Simple greeting endpoint
- `GET /api/health` - Health check endpoint

## 🔒 Security

Default credentials (for development only):
- **Username**: admin
- **Password**: admin123

⚠️ **Important**: Change these credentials before deploying to production!

## 🛠️ Development

The application uses `spring-boot-devtools` which provides:
- **Automatic restart** when code changes
- **LiveReload** for browser refresh

Make sure DevTools is enabled in your IDE for the best development experience.

## 🧪 Testing

Run tests with:
```bash
mvn test
```

## 📦 Building for Production

Create a production-ready JAR:
```bash
mvn clean package
```

Run the JAR:
```bash
java -jar target/springboot-app-0.0.1-SNAPSHOT.jar
```

## 🚀 Deployment to Render

This application is ready for deployment on Render with Docker. See [DEPLOYMENT.md](DEPLOYMENT.md) for detailed instructions on:
- Setting up MongoDB Atlas
- Deploying with Docker on Render
- Configuring environment variables
- Troubleshooting common issues

## 📚 Technology Stack Summary

| Category | Technology | Purpose |
|----------|-----------|---------|
| Web Framework | Spring MVC | Servlet-based web applications |
| Reactive | Spring WebFlux | Non-blocking HTTP client (WebClient) |
| Database | MongoDB | NoSQL document database |
| Template Engine | Thymeleaf | Server-side HTML rendering |
| Security | Spring Security | Authentication & authorization |
| Validation | Hibernate Validator | Input validation |
| JSON Processing | Jackson | Object-JSON serialization |
| Testing | JUnit 5, Mockito | Unit and integration testing |

## 📝 Notes

- The project includes both **Web** and **WebFlux** starters. This is intentional:
  - Web (Servlet) handles the main blocking request/response cycle
  - WebFlux provides WebClient for making reactive HTTP calls to external services
  
- **Jackson** is explicitly listed but is already included in `spring-boot-starter-web`. This allows for version control if needed.

## 🤝 Contributing

Feel free to extend this application with additional features!

## 📄 License

This project is for educational purposes.
