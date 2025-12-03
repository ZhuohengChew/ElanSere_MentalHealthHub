# Mental Health Hub - Developer Quick Reference

## 🚀 Quick Start (5 minutes)

### Prerequisites
- JDK 17+
- Maven 3.6+

### Run Locally
```bash
cd Mental-Health-Hub-Backend
mvn spring-boot:run
```
**URL**: http://localhost:8080

### Default Login
- **Role**: STUDENT
- **Email**: student@university.edu
- **Password**: any value

---

## 📁 Project Structure at a Glance

```
Mental-Health-Hub-Backend/
├── README.md                    # Full documentation
├── INSTALLATION.md              # Setup guide
├── MIGRATION_GUIDE.md          # React to Spring Boot migration
├── COMPLETION_SUMMARY.md       # Project overview
│
├── src/main/java/com/mentalhealthhub/
│   ├── controller/             # HTTP request handlers
│   ├── model/                  # Database entities
│   ├── repository/             # Data access (JPA)
│   └── MentalHealthHubApplication.java
│
├── src/main/resources/
│   ├── application.yml         # Configuration
│   ├── templates/              # Thymeleaf HTML
│   │   ├── auth/login.html
│   │   ├── dashboard/
│   │   ├── assessments/
│   │   ├── appointments/
│   │   ├── forum/
│   │   ├── users/
│   │   └── fragments/
│   └── static/
│       ├── css/style.css
│       └── js/main.js
│
├── pom.xml                     # Maven dependencies
└── src/test/                   # Tests (to be added)
```

---

## 🔑 Key Directories

| Path | Purpose | Contains |
|------|---------|----------|
| `controller/` | Request handling | 6 Java files |
| `model/` | Data entities | 5 Java files |
| `repository/` | Database queries | 4 Java files |
| `templates/` | HTML rendering | 17 templates |
| `static/css/` | Styling | CSS file |
| `static/js/` | Client logic | JavaScript |

---

## 🎯 Common Tasks

### 1. Add New Feature

**Step 1**: Create Model (Entity)
```java
// src/main/java/com/mentalhealthhub/model/NewFeature.java
@Entity
@Table(name = "new_features")
public class NewFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // ... fields
}
```

**Step 2**: Create Repository
```java
// src/main/java/com/mentalhealthhub/repository/NewFeatureRepository.java
@Repository
public interface NewFeatureRepository extends JpaRepository<NewFeature, Long> {
    // Custom queries if needed
}
```

**Step 3**: Create Controller
```java
// src/main/java/com/mentalhealthhub/controller/NewFeatureController.java
@Controller
@RequestMapping("/new-feature")
public class NewFeatureController {
    @Autowired
    private NewFeatureRepository repository;
    
    @GetMapping
    public String list(Model model) {
        model.addAttribute("items", repository.findAll());
        return "new-feature/list";
    }
}
```

**Step 4**: Create Templates
```
templates/new-feature/
├── list.html
├── new.html
└── view.html
```

### 2. Add Database Field

```java
// In Entity class
@Column(nullable = false)
private String newField;
```
- Restart app (JPA auto-creates schema)
- Or run: `mvn spring-boot:run -Dspring.jpa.hibernate.ddl-auto=update`

### 3. Query Database

```java
// In Repository
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
}

// In Controller
List<User> admins = userRepository.findByRole(UserRole.ADMIN);
```

### 4. Access Session Data in Template

```html
<!-- In any Thymeleaf template -->
<p th:text="${#session.getAttribute('user').name}"></p>

<!-- Or from model -->
<p th:text="${user.name}"></p>
```

### 5. Add Form

```java
// Controller
@PostMapping("/save")
public String save(@RequestParam String field, HttpSession session) {
    User user = (User) session.getAttribute("user");
    // Process...
    return "redirect:/path";
}
```

```html
<!-- Template -->
<form method="post" action="/save">
    <input type="text" name="field" required>
    <button type="submit">Save</button>
</form>
```

---

## 🗄️ Database Tables

### users
```sql
id | email | password | name | role | active | profile_picture
```

### assessments
```sql
id | user_id | title | description | content | score | result | created_at | updated_at
```

### appointments
```sql
id | student_id | professional_id | appointment_date | status | notes | created_at
```

### forum_posts
```sql
id | user_id | title | content | created_at | updated_at | views | replies
```

---

## 🛣️ URL Routes

### Authentication
```
GET  /              → redirect to /login
GET  /login         → login page
POST /login         → process login
GET  /logout        → logout
GET  /dashboard     → user dashboard (role-based)
```

### Assessments
```
GET  /assessments           → list assessments
GET  /assessments/new       → new assessment form
POST /assessments/save      → save assessment
GET  /assessments/{id}      → view assessment
```

### Appointments
```
GET  /appointments          → list appointments
GET  /appointments/book     → booking form
POST /appointments/save     → save appointment
```

### Forum
```
GET  /forum                 → list posts
GET  /forum/{id}            → view post
GET  /forum/new             → new post form
POST /forum/save            → save post
```

### Admin
```
GET  /users                 → list users
GET  /users/{id}            → user details
POST /users/{id}/deactivate → deactivate user
```

### Other
```
GET  /settings              → settings page
GET  /notifications         → notifications
```

---

## 🏗️ Adding a New Page

### 1. Create Controller Method
```java
@GetMapping("/my-page")
public String myPage(HttpSession session, Model model) {
    User user = (User) session.getAttribute("user");
    if (user == null) return "redirect:/login";
    
    model.addAttribute("user", user);
    model.addAttribute("data", someData);
    return "my-page";
}
```

### 2. Create Template
```html
<!-- src/main/resources/templates/my-page.html -->
<div class="container-fluid">
    <h1>My Page</h1>
    <p th:text="${data}"></p>
</div>
```

### 3. Add Navigation Link
```html
<!-- In templates/fragments/sidebar.html -->
<a href="/my-page" class="nav-link">
    <i class="bi bi-icon"></i> My Page
</a>
```

---

## 🎨 Styling Tips

### Use Bootstrap Classes
```html
<div class="container-fluid">
    <div class="row">
        <div class="col-md-6">
            <div class="card">
                <div class="card-body">Content</div>
            </div>
        </div>
    </div>
</div>
```

### Use Custom Variables (in style.css)
```css
color: var(--primary-color);    /* #6A8EAE */
background: var(--secondary-color); /* #7FB685 */
color: var(--accent-color);     /* #9B7EDE */
```

### Common Classes
- `.container-fluid` - Full width
- `.row` - Bootstrap row
- `.col-md-6` - 50% width on medium+
- `.card` - Card component
- `.btn btn-primary` - Button
- `.table table-hover` - Table
- `.form-control` - Input field
- `.badge bg-success` - Badge
- `.alert alert-info` - Alert

---

## 🐛 Debugging

### Check Logs
```
Terminal output while running app
Maven: [INFO], [WARNING], [ERROR]
Spring: Started MentalHealthHubApplication in X.XXX seconds
```

### Test Routes
```
Browser: http://localhost:8080/assessments
Check if template renders
Check for errors in browser console
```

### Database Issues
```
H2 Console: http://localhost:8080/h2-console
Run SQL: SELECT * FROM users;
Check data persistence
```

### Common Issues

**Issue**: Port 8080 in use
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

**Issue**: Template not found
- Check file location: `src/main/resources/templates/`
- Check controller return value matches template name
- Check Thymeleaf configuration in application.yml

**Issue**: Data not displaying
- Check model.addAttribute() in controller
- Check Thymeleaf syntax: `th:text="${variable}"`
- Verify data exists in database

---

## 📦 Maven Commands

```bash
mvn clean              # Clean build directory
mvn compile            # Compile source code
mvn test               # Run tests
mvn package            # Create JAR file
mvn clean install      # Clean + compile + package
mvn spring-boot:run    # Run application
mvn help               # Maven help
```

---

## 💾 File Locations

### Controllers
```
src/main/java/com/mentalhealthhub/controller/
├── AuthController.java
├── UserController.java
├── AssessmentController.java
├── AppointmentController.java
├── ForumController.java
└── PageController.java
```

### Models
```
src/main/java/com/mentalhealthhub/model/
├── User.java
├── UserRole.java
├── Assessment.java
├── Appointment.java
└── ForumPost.java
```

### Repositories
```
src/main/java/com/mentalhealthhub/repository/
├── UserRepository.java
├── AssessmentRepository.java
├── AppointmentRepository.java
└── ForumPostRepository.java
```

### Templates
```
src/main/resources/templates/
├── auth/login.html
├── dashboard/
├── assessments/
├── appointments/
├── forum/
├── users/
├── fragments/
├── settings.html
├── notifications.html
└── layout.html
```

### Static Files
```
src/main/resources/static/
├── css/style.css
└── js/main.js
```

---

## 🔐 User Roles

| Role | Access |
|------|--------|
| STUDENT | Dashboard, assessments, appointments, forum |
| STAFF | Staff dashboard, referrals, resources |
| PROFESSIONAL | Professional dashboard, client sessions |
| ADMIN | All + user management, settings, analytics |

### Role Check in Controller
```java
User user = (User) session.getAttribute("user");
if ("ADMIN".equals(user.getRole().toString())) {
    // Admin only code
}
```

### Role Check in Template
```html
<div th:if="${user.role == 'ADMIN'}">Admin content</div>
<div th:unless="${user.role == 'STUDENT'}">Not student</div>
```

---

## 🔗 External Links

- Spring Boot Docs: https://spring.io/projects/spring-boot
- Thymeleaf: https://www.thymeleaf.org/
- Bootstrap: https://getbootstrap.com/docs/5.3/
- JPA/Hibernate: https://hibernate.org/orm/
- Bootstrap Icons: https://icons.getbootstrap.com/
- Chart.js: https://www.chartjs.org/

---

## 📚 Learning Path

1. **Basics** (1-2 hours)
   - Run application locally
   - Explore existing features
   - Check database in H2 console

2. **Code Review** (2-3 hours)
   - Read controllers
   - Understand models and repositories
   - Review templates

3. **Modification** (2-3 hours)
   - Modify existing page
   - Add new field to model
   - Create simple new feature

4. **Advanced** (3-4 hours)
   - Add complete feature
   - Implement complex queries
   - Add validations

---

## 🎓 Best Practices

✓ **Do**:
- Keep controllers thin
- Use repositories for data access
- Follow MVC pattern
- Add validation
- Write meaningful commit messages

✗ **Don't**:
- Put database logic in controller
- Hard-code data
- Skip null checks
- Mix HTML and Java logic
- Commit without testing

---

## 📞 Quick Help

### Can't start app?
1. Check Java: `java -version`
2. Check Maven: `mvn -v`
3. Clean build: `mvn clean install`

### Can't access page?
1. Check route exists in controller
2. Check template file exists
3. Check template name matches return value

### Can't find data?
1. Check database: `mvn clean install`
2. Check model.addAttribute()
3. Check Thymeleaf syntax

### Need to change port?
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=9000"
```

---

## ✅ Testing Checklist

- [ ] Application starts
- [ ] Login works
- [ ] Dashboard displays
- [ ] Create assessment
- [ ] Book appointment
- [ ] Create forum post
- [ ] Admin can manage users
- [ ] Logout works
- [ ] No errors in console

---

**Version**: 1.0.0  
**Last Updated**: December 2024  
**Framework**: Spring Boot 3.2 + Thymeleaf

---

For detailed information, see:
- README.md - Full documentation
- INSTALLATION.md - Setup guide
- MIGRATION_GUIDE.md - Architecture details
