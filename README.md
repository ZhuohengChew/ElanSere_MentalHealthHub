<<<<<<< HEAD
# Mental Health Hub - Spring Boot + Thymeleaf Backend

A complete backend implementation of the Digital Mental Health Literacy Hub for universities, built with Spring Boot 3.2 and Thymeleaf templating engine.

## Project Structure

```
Mental-Health-Hub-Backend/
├── src/
│   ├── main/
│   │   ├── java/com/mentalhealthhub/
│   │   │   ├── MentalHealthHubApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── AssessmentController.java
│   │   │   │   ├── AppointmentController.java
│   │   │   │   └── ForumController.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── UserRole.java
│   │   │   │   ├── Assessment.java
│   │   │   │   ├── Appointment.java
│   │   │   │   └── ForumPost.java
│   │   │   └── repository/
│   │   │       ├── UserRepository.java
│   │   │       ├── AssessmentRepository.java
│   │   │       ├── AppointmentRepository.java
│   │   │       └── ForumPostRepository.java
│   │   ├── resources/
│   │   │   ├── application.yml (Configuration)
│   │   │   ├── templates/
│   │   │   │   ├── auth/
│   │   │   │   │   └── login.html
│   │   │   │   ├── dashboard/
│   │   │   │   │   ├── student-dashboard.html
│   │   │   │   │   ├── staff-dashboard.html
│   │   │   │   │   ├── professional-dashboard.html
│   │   │   │   │   └── admin-dashboard.html
│   │   │   │   ├── assessments/
│   │   │   │   │   ├── list.html
│   │   │   │   │   ├── new.html
│   │   │   │   │   └── view.html
│   │   │   │   ├── appointments/
│   │   │   │   │   ├── list.html
│   │   │   │   │   └── book.html
│   │   │   │   ├── forum/
│   │   │   │   │   ├── list.html
│   │   │   │   │   ├── view.html
│   │   │   │   │   └── new.html
│   │   │   │   ├── users/
│   │   │   │   │   ├── manage-users.html
│   │   │   │   │   └── user-detail.html
│   │   │   │   ├── fragments/
│   │   │   │   │   └── sidebar.html
│   │   │   │   └── layout.html
│   │   │   └── static/
│   │   │       ├── css/
│   │   │       │   └── style.css
│   │   │       └── js/
│   │   │           └── main.js
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
├── pom.xml (Maven Configuration)
└── README.md
```

## Features Implemented

### Authentication & Authorization
- Login page with role selection (Student, Staff, Professional, Admin)
- Session management
- Role-based dashboard routing

### User Management (Admin)
- View all users
- User detail pages
- User deactivation
- Role assignment

### Self-Assessment (Students)
- Create new assessments
- View assessment history
- Track assessment scores
- View assessment results

### Telehealth Appointments
- Browse available professionals
- Book appointments
- View appointment history
- Real-time appointment status

### Peer Support Forum
- Create forum posts
- View forum discussions
- Track post views and replies
- Community engagement

### Role-Based Dashboards
- **Student Dashboard**: Self-assessment, appointments, forum access, progress tracking
- **Staff Dashboard**: Student referrals, resource management, ticket handling
- **Professional Dashboard**: Client management, session scheduling, clinical records
- **Admin Dashboard**: System analytics, user management, feature usage, system health

## Technologies Used

- **Backend Framework**: Spring Boot 3.2
- **Template Engine**: Thymeleaf
- **Database**: H2 (Development) / MySQL (Production)
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security (ready for implementation)
- **Build Tool**: Maven
- **Frontend**: Bootstrap 5.3, Chart.js
- **Icons**: Bootstrap Icons
- **Java Version**: JDK 17+

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Maven 3.6 or higher
- Git

### Installation & Setup

1. **Clone the repository**
```bash
git clone <repository-url>
cd Mental-Health-Hub-Backend
```

2. **Build the project**
```bash
mvn clean install
```

3. **Run the application**
```bash
mvn spring-boot:run
```

The application will start at `http://localhost:8080`

### Database Setup

The application uses H2 database in-memory for development:
- H2 Console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

For production, configure MySQL in `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mental_health_hub
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
```

## API Endpoints

### Authentication
- `GET /` - Redirect to login
- `GET /login` - Login page
- `POST /login` - Process login
- `GET /logout` - Logout

### Dashboard
- `GET /dashboard` - Role-based dashboard

### Assessments
- `GET /assessments` - List assessments
- `GET /assessments/new` - New assessment form
- `POST /assessments/save` - Save assessment
- `GET /assessments/{id}` - View assessment

### Appointments
- `GET /appointments` - List appointments
- `GET /appointments/book` - Book appointment form
- `POST /appointments/save` - Save appointment

### Forum
- `GET /forum` - List posts
- `GET /forum/{id}` - View post
- `GET /forum/new` - New post form
- `POST /forum/save` - Save post

### Users (Admin)
- `GET /users` - List all users
- `GET /users/{id}` - View user
- `POST /users/{id}/deactivate` - Deactivate user

## Default Login Credentials

For testing purposes, you can use any email format:

| Role | Email | Password |
|------|-------|----------|
| Student | student@university.edu | any |
| Staff | staff@university.edu | any |
| Professional | prof@university.edu | any |
| Admin | admin@university.edu | any |

## File Explanations

### Java Classes

#### Controllers
- **AuthController**: Handles authentication, login, logout, and dashboard routing
- **UserController**: Manages user listing, viewing, and deactivation
- **AssessmentController**: Handles assessment CRUD operations
- **AppointmentController**: Manages appointment booking and viewing
- **ForumController**: Handles forum post creation and viewing

#### Models
- **User**: Represents system users with role and status
- **Assessment**: Stores self-assessment data and results
- **Appointment**: Manages telehealth appointments
- **ForumPost**: Represents community forum posts

#### Repositories
- JPA Repository interfaces for database operations
- Custom queries for specific data retrieval

### Templates (HTML)

#### Login Page
- `auth/login.html` - Responsive login interface with role selection

#### Dashboards
- `dashboard/student-dashboard.html` - Quick stats, activities, resources
- `dashboard/staff-dashboard.html` - Student referrals, resource management
- `dashboard/professional-dashboard.html` - Client management, schedule
- `dashboard/admin-dashboard.html` - System analytics, feature usage, user management

#### Feature Pages
- Assessment management (list, create, view)
- Appointment booking and management
- Forum functionality (list, create, view posts)
- User management interface

### Static Files

#### CSS
- `static/css/style.css` - Custom styling with color scheme matching Figma design

#### JavaScript
- `static/js/main.js` - Client-side functionality and interactions

## Configuration

### Application Properties

The `application.yml` file contains all configuration:

```yaml
spring:
  application:
    name: mental-health-hub
  jpa:
    hibernate:
      ddl-auto: update
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
  thymeleaf:
    cache: false
    encoding: UTF-8
server:
  port: 8080
```

## Security Considerations

The current implementation includes:
- Session management for authentication
- Role-based access control
- Password handling (basic)

**Future Enhancements**:
- Implement Spring Security with JWT tokens
- Add password encryption with BCrypt
- CSRF protection
- HTTPS/TLS support

## Future Enhancements

1. **Advanced Features**
   - Email notifications
   - Real-time chat with professionals
   - Video conferencing integration
   - Mobile app support

2. **Analytics**
   - Advanced usage analytics
   - Trend analysis
   - Report generation

3. **Security**
   - Two-factor authentication
   - API key management
   - Audit logging

4. **Integration**
   - Calendar sync (Google Calendar, Outlook)
   - Payment processing
   - SMS notifications

## Troubleshooting

### Application won't start
- Ensure Java 17+ is installed: `java -version`
- Check Maven installation: `mvn -v`
- Verify port 8080 is not in use

### Database issues
- Check H2 console is accessible
- Verify JPA/Hibernate configuration
- Check entity relationships

### Template rendering issues
- Verify Thymeleaf prefix/suffix in `application.yml`
- Check template file locations
- Enable Thymeleaf debugging if needed

## Contributing

1. Create a feature branch
2. Commit changes
3. Push to branch
4. Create pull request

## License

This project is part of the Digital Mental Health Literacy Hub initiative.

## Contact & Support

For questions or support, please contact the development team.

---

**Version**: 1.0.0  
**Last Updated**: December 2024  
**Framework**: Spring Boot 3.2 + Thymeleaf

