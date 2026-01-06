# Mental Health Hub Backend

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17+-blue)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)

A comprehensive mental health support platform for universities, providing self-assessments, appointment booking, professional consultations, peer forums, and analytics dashboards. Built with Spring Boot 3.2 and Thymeleaf.

## What This Project Does

The **Mental Health Hub** is a full-stack web application designed to support student mental health and wellness at universities. It enables:

- **Students** to self-assess mental health, book appointments with professionals, access educational modules, and engage in peer support forums
- **Mental Health Professionals** to manage client appointments, view health records, and document consultations
- **Staff Members** to monitor student wellness, manage referrals, and track mental health concerns
- **Administrators** to oversee system usage, generate analytics reports, and manage users

The platform combines accessible mental health resources with clinical functionality to create a supportive digital ecosystem.

## Why Use This Project

### Key Benefits

✅ **Complete Mental Health Platform** - All-in-one solution for student mental health support  
✅ **Role-Based Access** - Different dashboards and features for students, professionals, staff, and admins  
✅ **Real-Time Analytics** - Comprehensive admin dashboard with usage metrics and mental health trends  
✅ **Professional Architecture** - Clean separation of concerns with Spring MVC pattern  
✅ **Scalable Design** - MySQL database backend supports production deployments  
✅ **User-Friendly Interface** - Bootstrap 5.3 responsive design with intuitive navigation

### Ideal For

- University wellness departments implementing mental health technology
- Telehealth platform development
- Student support service modernization
- Mental health awareness and resource management

## Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: 17 or higher
- **Maven**: 3.6 or higher
- **MySQL**: 5.7+ (for production) or use H2 (development)
- **Git**: For version control

### Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd Mental-Health-Hub-Backend
```

2. **Build the project**
```bash
mvn clean install
```

3. **Configure the database** (development uses H2 in-memory)

For MySQL production setup, update `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mental_health_hub
    username: root
    password: your_password
```

4. **Run the application**
```bash
mvn spring-boot:run
```

Access the application at: **`http://localhost:8888/mentalhealthhub`**

### Quick Test

After starting the application, use these credentials to test different roles:

| Role | Email | Password |
|------|-------|----------|
| **Student** | student@test.edu | password |
| **Staff** | staff@test.edu | password |
| **Professional** | prof@test.edu | password |
| **Admin** | admin@test.edu | password |

**Note**: Any password is accepted for testing purposes.

## Core Features

### 🎓 Student Features
- Mental health self-assessments with score tracking
- Professional appointment booking and management
- Educational modules on mental health topics
- Mood and wellness tracking (daily self-care log)
- Peer support forum access
- Personal dashboard with wellness overview

### 👨‍⚕️ Professional Features
- Client/student management
- Appointment scheduling with status tracking
- Clinical report documentation
- Health records access
- Dashboard with caseload overview

### 👔 Staff Features
- Student referral tracking
- Mental health concern monitoring
- Appointment oversight
- Resource management
- Dashboard with system overview

### 🔐 Admin Features
- User management (create, view, deactivate)
- **Comprehensive Analytics Dashboard** including:
  - User registration trends (6-month history)
  - Mental health distribution metrics
  - Appointment statistics
  - Module engagement tracking
  - Forum activity analytics
- System health monitoring
- Admin activity logs

## API Endpoints

### Authentication
```
GET    /login              Login page
POST   /login              Process login
GET    /logout             Logout
GET    /register           Registration page
POST   /register           Create new user
GET    /dashboard          Role-based dashboard
```

### User Management (Admin)
```
GET    /manage-users       User list page
GET    /api/users          Get all users (JSON)
POST   /api/users/{id}/deactivate  Deactivate user
```

### Analytics (Admin) 
```
GET    /admin/analytics                    Analytics dashboard page
GET    /api/analytics/comprehensive        All analytics data
GET    /api/analytics/users                User metrics & trends
GET    /api/analytics/mental-health        Mental health statistics
GET    /api/analytics/appointments         Appointment analytics
GET    /api/analytics/modules              Module engagement data
GET    /api/analytics/self-care            Self-care activity metrics
GET    /api/analytics/forum                Forum engagement stats
GET    /api/analytics/reports              Report statistics
GET    /api/analytics/admin-activity       Admin action logs
```

### Appointments
```
GET    /appointments                   Appointment list
GET    /appointments/book              Booking form
POST   /appointments/save              Create appointment
GET    /appointments/api/{id}          Get appointment details
POST   /appointments/api/{id}/approve  Professional: approve
POST   /appointments/api/{id}/reject   Professional: reject
```

### Mental Health Concerns
```
GET    /concerns                    Concerns list page
GET    /concerns/api/my-reports     Get user's reports
POST   /concerns/save               Submit new concern
GET    /concerns/{id}               View concern details
POST   /concerns/{id}/resolve       Resolve concern (staff/prof)
```

### Educational Modules
```
GET    /modules              Module list
GET    /modules/{id}         View module content
POST   /modules/{id}/progress  Track progress
```

### Forum
```
GET    /forum               Forum posts list
GET    /forum/{id}          View post details
POST   /forum/new           Create new post
POST   /forum/save          Save post
```

## Project Structure

```
src/main/java/com/mentalhealthhub/
├── MentalHealthHubApplication.java     # Application entry point
├── config/                             # Spring configuration
├── controller/                         # Request handlers (MVC)
│   ├── AuthController.java             # Authentication & auth routes
│   ├── AdminAnalyticsController.java    # Admin analytics page
│   ├── AnalyticsController.java         # Analytics REST API
│   ├── AppointmentController.java       # Appointment management
│   ├── ConcernController.java           # Mental health concerns
│   ├── EducationalModuleController.java # Course modules
│   ├── ForumController.java             # Discussion forums
│   ├── NotificationController.java      # Notifications
│   ├── PageController.java              # Utility pages
│   └── UserController.java              # User management
├── model/                              # Entity classes
│   ├── User.java
│   ├── Appointment.java
│   ├── Report.java
│   ├── EducationalModule.java
│   ├── ForumPost.java
│   └── ...
├── repository/                         # Data access layer
│   ├── UserRepository.java
│   ├── AppointmentRepository.java
│   └── ...
├── service/                            # Business logic
│   ├── AnalyticsService.java           # Analytics calculations
│   ├── UserService.java                # User operations
│   ├── ReportService.java              # Report processing
│   └── ...
└── util/                               # Utility classes
```

```
src/main/resources/
├── application.yml                     # Configuration
├── db/migration/                       # Flyway migrations
└── templates/
    ├── layout.html                     # Master template
    ├── auth/login.html                 # Login page
    ├── dashboard/
    │   ├── student-dashboard.html
    │   ├── staff-dashboard.html
    │   ├── professional-dashboard.html
    │   └── admin-dashboard.html
    ├── admin/analytics-report.html     # Analytics dashboard
    ├── appointments/                   # Appointment pages
    ├── concerns/                       # Concern/report pages
    ├── modules/                        # Module pages
    ├── forum/                          # Forum pages
    └── fragments/                      # Reusable components
```

## Configuration

### Application Settings

Edit `src/main/resources/application.yml`:

```yaml
server:
  port: 8888                                    # Application port
  servlet:
    context-path: /mentalhealthhub              # Web context path

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mental_health_hub  # Database URL
    username: root                              # Database user
    password: [your-password]                   # Database password
  
  jpa:
    hibernate:
      ddl-auto: update                          # Schema auto-update
    show-sql: false                             # SQL logging

logging:
  level:
    root: INFO                                  # Root log level
    com.mentalhealthhub: DEBUG                  # App-specific logging
```

### Database

**Development**: Uses H2 in-memory database (no setup required)

**Production**: Configure MySQL connection in `application.yml`

Run migrations:
```bash
mvn flyway:migrate
```

## Building and Deployment

### Build JAR
```bash
mvn clean package
```
Creates: `target/mentalhealthhub-1.0.0.jar`

### Run JAR
```bash
java -jar target/mentalhealthhub-1.0.0.jar
```

### Build Options
```bash
mvn clean install              # Compile and test
mvn spring-boot:run            # Run locally with hot reload
mvn clean package -DskipTests  # Skip tests for faster build
```

## Technologies

| Component | Technology |
|-----------|-----------|
| **Framework** | Spring Boot 3.2 |
| **Web** | Spring MVC + Thymeleaf |
| **Database** | Spring Data JPA, Hibernate, MySQL/H2 |
| **Security** | Session-based authentication |
| **Frontend** | Bootstrap 5.3, Chart.js, JavaScript |
| **Build** | Maven |
| **Java Version** | JDK 17+ |

## Key Dependencies

- `spring-boot-starter-web` - Web framework
- `spring-boot-starter-data-jpa` - ORM and database access
- `spring-boot-starter-security` - Authentication
- `spring-boot-starter-thymeleaf` - Template engine
- `mysql-connector-java` - MySQL driver
- `itextpdf` - PDF generation
- `bootstrap` - UI framework (frontend)
- `chart.js` - Analytics charts (frontend)

## Getting Help

### Documentation

- **Setup Guides**: See docs/ folder for detailed setup instructions
- **Architecture**: Review ARCHITECTURE.md for system design
- **Analytics**: See ANALYTICS_SETUP_GUIDE.md for dashboard configuration
- **API Details**: Check ANALYTICS_IMPLEMENTATION.md for endpoint specifications

### Troubleshooting

**Port in use**: Change `server.port` in `application.yml` if port 8888 is occupied

**Database connection error**:
```bash
# Verify MySQL is running
mysql -u root -p mental_health_hub

# Check application.yml datasource settings
# Ensure database exists: CREATE DATABASE mental_health_hub;
```

**Build failures**: Clear Maven cache
```bash
rm -rf ~/.m2/repository/com/mentalhealthhub/
mvn clean install
```

**Template not found**: Verify files exist in `src/main/resources/templates/`

## Contributing

We welcome contributions to improve the Mental Health Hub!

### How to Contribute

1. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes**
   - Follow existing code style
   - Keep commits focused and descriptive

3. **Test your changes**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Push and create a pull request**
   ```bash
   git push origin feature/your-feature-name
   ```

### Code Style

- Use meaningful variable names
- Keep methods small and focused
- Add JavaDoc for public methods
- Follow Spring Boot conventions

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Support & Contact

For questions, issues, or suggestions:

- **Create an Issue**: Report bugs or request features on GitHub
- **Documentation**: Check the docs/ folder for comprehensive guides
- **Team**: Contact the development team for direct support

---

**Version**: 1.0.0  
**Last Updated**: January 2026  
**Status**: Production Ready

