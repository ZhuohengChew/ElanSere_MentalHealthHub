# React to Spring Boot + Thymeleaf Migration Guide

## Overview

This document provides a comprehensive guide for migrating from the React-based Figma prototype to the complete Spring Boot + Thymeleaf backend implementation.

## Architecture Comparison

### Original Architecture (React)
```
React Client
├── Components (TSX)
├── UI Library (Radix UI)
├── Styling (Tailwind CSS)
├── State Management (React Hooks)
└── Local Mock Data
```

### New Architecture (Spring Boot + Thymeleaf)
```
Spring Boot Backend
├── Controllers (Handle HTTP Requests)
├── Services (Business Logic)
├── Models (Data Entities)
├── Repositories (Database Access)
├── Thymeleaf Templates (HTML Rendering)
├── Static Assets (CSS, JS)
└── Persistent Database (H2/MySQL)
```

## Component Mapping

### React TSX Components → Thymeleaf Templates

| React Component | TSX Path | Thymeleaf Template | Purpose |
|---|---|---|---|
| LoginPage | `components/LoginPage.tsx` | `templates/auth/login.html` | Authentication |
| StudentDashboard | `components/StudentDashboard.tsx` | `templates/dashboard/student-dashboard.html` | Student home |
| AdminDashboard | `components/AdminDashboard.tsx` | `templates/dashboard/admin-dashboard.html` | Admin system view |
| StaffDashboard | `components/StaffDashboard.tsx` | `templates/dashboard/staff-dashboard.html` | Staff management |
| ProfessionalDashboard | `components/ProfessionalDashboard.tsx` | `templates/dashboard/professional-dashboard.html` | Professional interface |
| SelfAssessment | `components/SelfAssessment.tsx` | `templates/assessments/` | Assessment features |
| TelehealthBooking | `components/TelehealthBooking.tsx` | `templates/appointments/` | Appointment booking |
| PeerSupportForum | `components/PeerSupportForum.tsx` | `templates/forum/` | Forum discussions |
| ManageUsers | `components/ManageUsers.jsx` | `templates/users/` | User management |
| Settings | `components/Settings.tsx` | `templates/settings.html` | User preferences |
| Notifications | `components/Notifications.tsx` | `templates/notifications.html` | Alerts |

## Key Differences

### 1. State Management
**Before (React)**
```tsx
const [user, setUser] = useState<User | null>(null);
const [currentPage, setCurrentPage] = useState<Page>('login');
```

**After (Spring Boot)**
```java
HttpSession session;
session.setAttribute("user", user);
session.getAttribute("user");
```

### 2. Data Fetching
**Before (React)**
```tsx
// Mock data in component
const users = [
  { id: '1', name: 'Ali', role: 'student', ... }
];
```

**After (Spring Boot)**
```java
// Database queries via repositories
List<User> users = userRepository.findAll();
model.addAttribute("users", users);
```

### 3. Form Handling
**Before (React)**
```tsx
<form onSubmit={handleSubmit}>
  <input value={email} onChange={(e) => setEmail(e.target.value)} />
</form>
```

**After (Thymeleaf)**
```html
<form method="post" action="/login">
  <input type="email" name="email" required>
  <input type="password" name="password" required>
</form>
```

### 4. Styling
**Before (React)**
```tsx
className="flex grid-cols-1 md:grid-cols-4 gap-6"
// Tailwind CSS utility classes
```

**After (Thymeleaf + Bootstrap)**
```html
class="row mb-4"
<div class="col-md-3">...</div>
<!-- Bootstrap grid system -->
```

### 5. Icons
**Before (React)**
```tsx
import { Users, Activity } from 'lucide-react';
<Users className="h-4 w-4" />
```

**After (Thymeleaf)**
```html
<i class="bi bi-people"></i>
<!-- Bootstrap Icons -->
```

## File Structure Migration

```
Old Structure (React)
src/
├── components/
│   ├── AdminDashboard.tsx
│   ├── StudentDashboard.tsx
│   ├── ui/
│   │   ├── card.tsx
│   │   ├── button.tsx
│   │   └── ...
│   └── figma/
│       └── ImageWithFallback.tsx
├── styles/
│   └── globals.css
└── App.tsx

New Structure (Spring Boot)
src/main/
├── java/com/mentalhealthhub/
│   ├── controller/        # Replaces App.tsx routing
│   ├── model/            # Data models
│   ├── repository/       # Data access
│   └── service/          # Business logic (future)
└── resources/
    ├── templates/        # Replaces components/
    │   ├── auth/
    │   ├── dashboard/
    │   ├── assessments/
    │   ├── appointments/
    │   ├── forum/
    │   ├── users/
    │   └── fragments/    # Reusable template parts
    └── static/
        ├── css/         # Bootstrap + custom
        ├── js/          # Client-side logic
        └── img/         # Assets
```

## Feature-by-Feature Migration

### 1. Authentication
**React Implementation**
- Mock login with email/password
- Client-side state management
- Local storage for session

**Spring Boot Implementation**
- Server-side authentication
- HTTP session management
- Database-backed user storage

**Migration Steps**
1. Create User entity with JPA
2. Implement UserRepository
3. Create AuthController with login endpoint
4. Build login.html template
5. Session-based auth (future: JWT)

### 2. Dashboard Display
**React Implementation**
- Hard-coded statistics in component
- Inline data arrays
- Client-side rendering

**Spring Boot Implementation**
- Query database for real data
- Pass data through model attributes
- Server-side rendering with Thymeleaf

**Migration Example**
```java
// Controller
@GetMapping("/dashboard")
public String dashboard(HttpSession session, Model model) {
    User user = (User) session.getAttribute("user");
    
    // Fetch real data
    List<Assessment> assessments = assessmentRepository
        .findByUserOrderByCreatedAtDesc(user);
    
    model.addAttribute("assessments", assessments);
    return "dashboard/student-dashboard";
}

// Template
<tr th:each="assessment : ${assessments}">
    <td th:text="${assessment.title}"></td>
</tr>
```

### 3. Form Submissions
**React**
- Client-side form state
- Synthetic event handlers
- In-memory data updates

**Spring Boot**
- Server-side form processing
- HTTP POST requests
- Database persistence

**Migration Example**
```java
@PostMapping("/assessments/save")
public String saveAssessment(@RequestParam String title,
                             @RequestParam String content,
                             HttpSession session) {
    User user = (User) session.getAttribute("user");
    Assessment assessment = Assessment.builder()
        .user(user)
        .title(title)
        .content(content)
        .createdAt(LocalDateTime.now())
        .build();
    assessmentRepository.save(assessment);
    return "redirect:/assessments";
}
```

### 4. Navigation
**React**
```tsx
const handleNavigate = (page: Page) => {
  setCurrentPage(page);
};
// Used everywhere to switch pages
```

**Spring Boot**
```html
<!-- Traditional URL-based navigation -->
<a href="/dashboard">Dashboard</a>
<a href="/assessments">Assessments</a>
<a th:href="@{/assessments/{id}(id=${assessment.id})}">View</a>
```

### 5. UI Components
**React**
- Radix UI components imported
- Custom wrapper components
- Props-based configuration

**Spring Boot/Thymeleaf**
- Bootstrap components directly
- HTML/Thymeleaf directives
- Server-side rendering

**Migration Example**
```tsx
// React UI component
import { Card, CardContent, CardHeader } from './ui/card';
<Card>
  <CardHeader>
    <CardTitle>Title</CardTitle>
  </CardHeader>
  <CardContent>Content</CardContent>
</Card>
```

```html
<!-- Thymeleaf Bootstrap -->
<div class="card">
  <div class="card-header">
    <h6 class="card-title">Title</h6>
  </div>
  <div class="card-body">Content</div>
</div>
```

## Data Model Mapping

### React User Type
```tsx
interface User {
  id: string;
  name: string;
  email: string;
  role: UserRole;
}
```

### Spring Boot User Entity
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Enumerated(EnumType.STRING)
    private UserRole role;
    // ... more fields
}
```

## API Endpoints

### Login Flow
```
Request:  POST /login
Body:     email=user@uni.edu&password=pass&role=STUDENT
Response: Redirect to /dashboard + session cookie

Request:  GET /dashboard
Response: HTML with user-specific data
```

### Assessment Management
```
List:     GET /assessments
Create:   GET /assessments/new (form page)
Save:     POST /assessments/save
View:     GET /assessments/{id}
```

### Forum Discussion
```
List:     GET /forum
View:     GET /forum/{id}
Create:   GET /forum/new (form page)
Save:     POST /forum/save
```

## Configuration Changes

### Application Configuration
```yaml
# application.yml replaces environment variables
spring:
  application:
    name: mental-health-hub
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    hibernate:
      ddl-auto: update
server:
  port: 8080
```

### Dependencies
**React**
```json
"react": "^18.3.1",
"@radix-ui/*": "latest",
"tailwindcss": "^3.x"
```

**Spring Boot**
```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

## Development Workflow Comparison

### React Development
```bash
npm install          # Install dependencies
npm run dev          # Start dev server (Vite)
npm run build        # Build for production
# Test in browser at http://localhost:5173
```

### Spring Boot Development
```bash
mvn clean install    # Download dependencies & build
mvn spring-boot:run  # Start application
# Test in browser at http://localhost:8080
```

## Performance Considerations

### React Advantages
- Client-side rendering
- Instant page transitions
- Single page app experience

### Spring Boot Advantages
- Server-side rendering
- Better SEO
- Reduced client bandwidth
- Better for data-heavy operations

### Hybrid Approach (Future)
- Keep Spring Boot backend
- Add REST APIs for future React frontend
- Use AJAX for interactive features
- Maintain traditional server-rendered pages

## Security Improvements

### React Implementation
- Mock authentication
- No real security

### Spring Boot Implementation
- Server-side session management
- Database integrity
- Ready for Spring Security integration

**Next Steps for Security**
1. Implement Spring Security
2. Add password encryption (BCrypt)
3. Configure CSRF protection
4. Enable HTTPS/TLS
5. Implement JWT tokens (if adding REST API)

## Testing Strategy

### Unit Tests
```java
// Test repositories
@DataJpaTest
class UserRepositoryTest {
    // Repository tests
}

// Test controllers
@WebMvcTest(AuthController.class)
class AuthControllerTest {
    // Controller tests
}
```

### Integration Tests
```java
@SpringBootTest
class MentalHealthHubApplicationTests {
    // Full application tests
}
```

## Deployment Differences

### React Deployment
- Build static files
- Deploy to CDN or static hosting
- Separate backend not included

### Spring Boot Deployment
- Single JAR file contains everything
- Deploy to server/container
- Includes templates and static files

## Migration Checklist

- [ ] Set up Spring Boot project
- [ ] Create database schema
- [ ] Implement JPA entities
- [ ] Create repositories
- [ ] Build controllers
- [ ] Create Thymeleaf templates
- [ ] Set up authentication
- [ ] Add styling (Bootstrap + CSS)
- [ ] Add client-side JavaScript
- [ ] Test all features
- [ ] Performance optimization
- [ ] Security hardening
- [ ] Deploy to production

## Future Enhancements

### Phase 1 (Complete)
- Server-side rendering with Thymeleaf ✓
- Database persistence ✓
- Multi-role support ✓
- Core features ✓

### Phase 2 (Recommended)
- REST API for mobile apps
- WebSocket for real-time features
- Advanced caching
- Microservices architecture

### Phase 3 (Optional)
- React frontend + Spring Boot API
- Mobile app (React Native)
- Advanced analytics
- AI/ML integration

## Resource Links

- Spring Boot Guide: https://spring.io/guides/gs/serving-web-content/
- Thymeleaf Tutorial: https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html
- Bootstrap Documentation: https://getbootstrap.com/docs/5.3/
- MySQL & JPA: https://spring.io/guides/gs/accessing-data-mysql/

## Contact & Support

For questions about the migration or new implementation, contact the development team.

---

**Version**: 1.0.0  
**Migration Date**: December 2024
