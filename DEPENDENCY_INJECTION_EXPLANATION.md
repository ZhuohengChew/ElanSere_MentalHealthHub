# Dependency Injection Implementation Guide

## Overview
This document explains how Dependency Injection (DI) was implemented in the Mental Health Hub project using Spring Framework's Inversion of Control (IoC) container.

---

## 1. Component Annotation Strategy

### Services
All service classes are annotated with `@Service`:
```java
@Service
public class UserService {
    // Service implementation
}
```

### Repositories
All repository interfaces are annotated with `@Repository`:
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Repository methods
}
```

### Controllers
All controllers are annotated with `@Controller` or `@RestController`:
```java
@Controller
public class AuthController {
    // Controller implementation
}
```

### Configuration Classes
Configuration classes use `@Configuration`:
```java
@Configuration
public class SecurityConfig {
    // Configuration beans
}
```

---

## 2. Constructor-Based Dependency Injection (Primary Method)

### ✅ **AFTER (Current Implementation - Recommended)**

**Service Example:**
```java
@Service
public class UserService {
    
    // Final fields - immutable dependencies
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection - Spring automatically calls this
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // Methods use the injected dependencies
    public User registerUser(String email, String password, String name, UserRole role) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }
        String hashedPassword = passwordEncoder.encode(password);
        // ... rest of implementation
    }
}
```

**Controller Example:**
```java
@Controller
public class AuthController {
    
    // All dependencies as final fields
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserService userService;
    // ... more dependencies

    // Constructor with all dependencies
    public AuthController(
            UserRepository userRepository,
            AppointmentRepository appointmentRepository,
            UserService userService,
            // ... more parameters
    ) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.userService = userService;
        // ... assign all dependencies
    }
}
```

### ❌ **BEFORE (Old Implementation - Not Recommended)**

```java
@Service
public class UserService {
    
    // Field injection - not recommended
    @Autowired
    private UserRepository userRepository;
    
    // Manual instantiation - tight coupling
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // No constructor needed - dependencies injected via reflection
}
```

---

## 3. Bean Configuration for Third-Party Dependencies

### PasswordEncoder Bean
Instead of manually creating `PasswordEncoder` instances, we created a Spring bean:

**SecurityConfig.java:**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Bean method - Spring manages this instance
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // Now PasswordEncoder can be injected anywhere
}
```

**Usage in Service:**
```java
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder; // Injected by Spring
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder; // Spring provides the bean
    }
}
```

---

## 4. How Spring IoC Container Works

### Step-by-Step Process:

1. **Component Scanning**
   - Spring scans the package `com.mentalhealthhub` (and subpackages)
   - Finds all classes annotated with `@Component`, `@Service`, `@Repository`, `@Controller`
   - Registers them as beans in the IoC container

2. **Dependency Resolution**
   - When Spring creates a bean (e.g., `UserService`), it looks at the constructor
   - It identifies required dependencies (`UserRepository`, `PasswordEncoder`)
   - It finds or creates those dependencies from the container
   - It injects them via the constructor

3. **Bean Lifecycle**
   ```
   Application Start
        ↓
   Spring scans for @Component/@Service/@Repository/@Controller
        ↓
   Creates bean instances (singletons by default)
        ↓
   Resolves dependencies via constructor
        ↓
   Calls constructor with resolved dependencies
        ↓
   Bean is ready to use
   ```

### Example: Creating UserService

```java
// 1. Spring finds @Service annotation
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    // 2. Spring sees constructor needs 2 dependencies
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        // 3. Spring provides:
        //    - UserRepository (already a bean from @Repository)
        //    - PasswordEncoder (from @Bean method in SecurityConfig)
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
}
```

---

## 5. Benefits of This Approach

### ✅ **Immutability**
```java
private final UserRepository userRepository; // Cannot be changed after construction
```
- Dependencies are `final`, preventing accidental reassignment
- Ensures thread-safety
- Makes dependencies explicit

### ✅ **Testability**
```java
// Easy to test with mocks
@Test
void testUserService() {
    UserRepository mockRepo = mock(UserRepository.class);
    PasswordEncoder mockEncoder = mock(PasswordEncoder.class);
    
    UserService service = new UserService(mockRepo, mockEncoder);
    // Test the service
}
```

### ✅ **Clear Dependencies**
- Constructor signature shows exactly what's needed
- Compile-time checking (can't create object without dependencies)
- No hidden dependencies

### ✅ **No Manual Instantiation**
```java
// ❌ OLD WAY (Tight Coupling)
private UserRepository userRepository = new UserRepositoryImpl();
private PasswordEncoder encoder = new BCryptPasswordEncoder();

// ✅ NEW WAY (Loose Coupling)
private final UserRepository userRepository; // Spring provides
private final PasswordEncoder passwordEncoder; // Spring provides
```

---

## 6. Complete Example: AnalyticsService

This service has 10 dependencies - all injected via constructor:

```java
@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    // All dependencies as final fields
    private final UserRepository userRepository;
    private final AssessmentRepository assessmentRepository;
    private final AppointmentRepository appointmentRepository;
    private final ModuleProgressRepository moduleProgressRepository;
    private final SelfCareRepository selfCareRepository;
    private final ForumPostRepository forumPostRepository;
    private final ForumCommentRepository forumCommentRepository;
    private final ReportRepository reportRepository;
    private final AuditLogRepository auditLogRepository;
    private final EducationalModuleRepository educationalModuleRepository;

    // Constructor injection - Spring automatically provides all 10 dependencies
    public AnalyticsService(
            UserRepository userRepository,
            AssessmentRepository assessmentRepository,
            AppointmentRepository appointmentRepository,
            ModuleProgressRepository moduleProgressRepository,
            SelfCareRepository selfCareRepository,
            ForumPostRepository forumPostRepository,
            ForumCommentRepository forumCommentRepository,
            ReportRepository reportRepository,
            AuditLogRepository auditLogRepository,
            EducationalModuleRepository educationalModuleRepository) {
        // Assign all dependencies
        this.userRepository = userRepository;
        this.assessmentRepository = assessmentRepository;
        this.appointmentRepository = appointmentRepository;
        this.moduleProgressRepository = moduleProgressRepository;
        this.selfCareRepository = selfCareRepository;
        this.forumPostRepository = forumPostRepository;
        this.forumCommentRepository = forumCommentRepository;
        this.reportRepository = reportRepository;
        this.auditLogRepository = auditLogRepository;
        this.educationalModuleRepository = educationalModuleRepository;
    }
    
    // Methods can now use all dependencies
    public UserAnalyticsDTO getUserAnalytics() {
        dto.setTotalUsers(userRepository.countTotalUsers());
        dto.setActiveUsers(userRepository.countActiveUsers());
        // ... uses all repositories
    }
}
```

---

## 7. Dependency Injection Types Used

### ✅ **Constructor Injection (Primary - Used Everywhere)**
- **When**: For all mandatory dependencies
- **Why**: Immutable, testable, explicit
- **Example**: All services and controllers

### ✅ **Bean Method Injection (For Configuration)**
- **When**: Creating beans for third-party classes
- **Why**: Spring needs to know how to create instances
- **Example**: `PasswordEncoder` bean in `SecurityConfig`

### ❌ **Field Injection (Removed)**
- **Before**: `@Autowired private UserRepository repo;`
- **After**: Constructor injection with final fields
- **Why Removed**: Not recommended, harder to test, not immutable

### ❌ **Setter Injection (Not Used)**
- **When**: For optional dependencies (not needed in this project)
- **Why Not Used**: All dependencies are mandatory

---

## 8. Spring Container Flow Diagram

```
┌─────────────────────────────────────────┐
│   Spring IoC Container                 │
│                                         │
│  ┌──────────────────────────────────┐  │
│  │  Component Scanner               │  │
│  │  Finds: @Service, @Repository    │  │
│  │         @Controller, @Component  │  │
│  └──────────────────────────────────┘  │
│              ↓                          │
│  ┌──────────────────────────────────┐  │
│  │  Bean Registry                    │  │
│  │  - UserService                    │  │
│  │  - UserRepository                 │  │
│  │  - PasswordEncoder (from @Bean)   │  │
│  │  - AuthController                 │  │
│  └──────────────────────────────────┘  │
│              ↓                          │
│  ┌──────────────────────────────────┐  │
│  │  Dependency Resolver              │  │
│  │  - Analyzes constructors          │  │
│  │  - Finds matching beans           │  │
│  │  - Creates dependency graph       │  │
│  └──────────────────────────────────┘  │
│              ↓                          │
│  ┌──────────────────────────────────┐  │
│  │  Bean Creation                    │  │
│  │  1. Create UserRepository         │  │
│  │  2. Create PasswordEncoder        │  │
│  │  3. Create UserService            │  │
│  │     (inject repo + encoder)      │  │
│  │  4. Create AuthController         │  │
│  │     (inject all dependencies)     │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

---

## 9. Key Principles Applied

### 1. **Inversion of Control (IoC)**
- **Before**: Classes created their own dependencies
- **After**: Spring container creates and manages dependencies
- **Benefit**: Loose coupling, easier testing

### 2. **Dependency Inversion Principle**
- **Before**: Depended on concrete implementations
- **After**: Depends on abstractions (interfaces)
- **Benefit**: Can swap implementations easily

### 3. **Single Responsibility Principle**
- Each class has one reason to change
- Dependencies are injected, not created
- **Benefit**: Better separation of concerns

### 4. **Immutability**
- All dependencies are `final`
- Set once in constructor, never change
- **Benefit**: Thread-safe, predictable

---

## 10. Real-World Example Flow

### Scenario: User logs in

```
1. Request: POST /login
   ↓
2. Spring routes to AuthController.login()
   ↓
3. AuthController needs UserService
   ↓
4. Spring provides UserService (already created)
   ↓
5. UserService needs UserRepository + PasswordEncoder
   ↓
6. Spring provides both (already created)
   ↓
7. UserService.authenticateUser() executes
   ↓
8. Uses injected UserRepository to find user
   ↓
9. Uses injected PasswordEncoder to verify password
   ↓
10. Returns result to controller
```

**All dependencies are provided automatically by Spring!**

---

## 11. Summary

### What Changed:
1. ✅ Removed `@Autowired` field injection
2. ✅ Added constructor injection with final fields
3. ✅ Created `@Bean` for `PasswordEncoder`
4. ✅ All services, controllers, and configs use constructor DI
5. ✅ No manual `new` keyword for dependencies

### Benefits:
- ✅ **Loose Coupling**: Classes don't create dependencies
- ✅ **Testability**: Easy to mock dependencies
- ✅ **Immutability**: Final fields prevent changes
- ✅ **Clarity**: Constructor shows all dependencies
- ✅ **Spring Best Practice**: Recommended approach

### Result:
- All features work exactly as before
- Code is more maintainable and testable
- Follows Spring Framework best practices
- Ready for future enhancements

