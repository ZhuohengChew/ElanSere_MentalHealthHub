# Installation & Deployment Guide

## Local Development Setup

### Step 1: Prerequisites Installation

1. **Java Development Kit (JDK) 17+**
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Or use OpenJDK: https://adoptopenjdk.net/
   - Verify installation:
     ```bash
     java -version
     javac -version
     ```

2. **Maven 3.6+**
   - Download from: https://maven.apache.org/download.cgi
   - Extract and add to PATH
   - Verify installation:
     ```bash
     mvn -v
     ```

3. **Git**
   - Download from: https://git-scm.com/download
   - Verify installation:
     ```bash
     git --version
     ```

### Step 2: Project Setup

1. **Clone/Extract Project**
   ```bash
   cd Mental-Health-Hub-Backend
   ```

2. **Build with Maven**
   ```bash
   mvn clean install
   ```
   This will:
   - Download all dependencies
   - Compile Java source code
   - Run tests
   - Package the application

3. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```
   
   Or:
   ```bash
   java -jar target/mental-health-hub-1.0.0.jar
   ```

4. **Access the Application**
   - Open browser: http://localhost:8080
   - You'll be redirected to the login page

### Step 3: Test the Application

1. **Login with Demo Credentials**
   - Role: Student
   - Email: student@university.edu
   - Password: any value

2. **Explore Features**
   - Student Dashboard: View quick stats and activities
   - Self-Assessment: Create and view assessments
   - Telehealth: Book and manage appointments
   - Forum: Browse and create forum posts
   - (Admin only) Manage Users: View and manage system users

## Database Configuration

### H2 Database (Development - Default)

The application uses H2 in-memory database by default.

1. **Access H2 Console**
   - URL: http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `sa`
   - Password: (leave empty)

2. **Data Persistence**
   - Data is lost when application restarts
   - For persistent data, switch to file-based H2:
     ```yaml
     spring:
       datasource:
         url: jdbc:h2:file:./data/testdb
     ```

### MySQL Database (Production)

1. **Install MySQL**
   - Download: https://dev.mysql.com/downloads/mysql/
   - Or use Docker:
     ```bash
     docker run -d -e MYSQL_ROOT_PASSWORD=password -p 3306:3306 mysql:8.0
     ```

2. **Create Database**
   ```sql
   CREATE DATABASE mental_health_hub;
   USE mental_health_hub;
   ```

3. **Update application.yml**
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
           dialect: org.hibernate.dialect.MySQL8Dialect
   ```

4. **Rebuild and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

## IDE Setup (IntelliJ IDEA / Eclipse)

### IntelliJ IDEA

1. **Open Project**
   - File → Open → Select Mental-Health-Hub-Backend folder
   - Click Open as Project

2. **Configure JDK**
   - File → Project Structure → Project
   - Set Project SDK to JDK 17+

3. **Maven Configuration**
   - View → Tool Windows → Maven
   - Reload Maven projects

4. **Run Application**
   - Right-click MentalHealthHubApplication
   - Click Run
   - Or use Shift+F10

### Eclipse

1. **Import Project**
   - File → Import → Maven → Existing Maven Projects
   - Browse to project folder
   - Click Finish

2. **Configure JDK**
   - Right-click project → Properties
   - Java Compiler → Compiler compliance level: 17

3. **Run Application**
   - Right-click project → Run As → Spring Boot App

## Docker Deployment

### Build Docker Image

1. **Create Dockerfile**
   ```dockerfile
   FROM openjdk:17-slim
   COPY target/mental-health-hub-1.0.0.jar app.jar
   ENTRYPOINT ["java","-jar","/app.jar"]
   ```

2. **Build Image**
   ```bash
   docker build -t mental-health-hub:1.0.0 .
   ```

3. **Run Container**
   ```bash
   docker run -p 8080:8080 mental-health-hub:1.0.0
   ```

### Docker Compose (with MySQL)

1. **Create docker-compose.yml**
   ```yaml
   version: '3.8'
   services:
     db:
       image: mysql:8.0
       environment:
         MYSQL_ROOT_PASSWORD: root
         MYSQL_DATABASE: mental_health_hub
       ports:
         - "3306:3306"
       volumes:
         - db-data:/var/lib/mysql
     
     app:
       build: .
       ports:
         - "8080:8080"
       environment:
         - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/mental_health_hub
         - SPRING_DATASOURCE_USERNAME=root
         - SPRING_DATASOURCE_PASSWORD=root
       depends_on:
         - db
   
   volumes:
     db-data:
   ```

2. **Run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

## Production Deployment

### Using AWS EC2

1. **Create EC2 Instance**
   - Launch Ubuntu 20.04 LTS instance
   - Configure security group (allow ports 8080, 80, 443)

2. **Install Dependencies**
   ```bash
   sudo apt update
   sudo apt install -y openjdk-17-jdk mysql-server
   ```

3. **Deploy Application**
   ```bash
   sudo mkdir /opt/app
   sudo cp target/mental-health-hub-1.0.0.jar /opt/app/
   sudo java -jar /opt/app/mental-health-hub-1.0.0.jar &
   ```

### Using Heroku

1. **Create Procfile**
   ```
   web: java -Dserver.port=$PORT $JAVA_OPTS -jar target/mental-health-hub-1.0.0.jar
   ```

2. **Deploy**
   ```bash
   heroku login
   heroku create mental-health-hub
   git push heroku main
   ```

## Troubleshooting

### Port 8080 Already in Use
```bash
# Find process using port 8080
lsof -i :8080
# Or Windows:
netstat -ano | findstr :8080

# Kill process or use different port:
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Maven Build Fails
```bash
# Clear Maven cache
mvn clean install -U

# Check Java version
java -version

# Verify Maven settings
mvn --version
```

### Database Connection Issues
```bash
# Check MySQL is running
sudo service mysql status

# Check database exists
mysql -u root -p mental_health_hub

# Reset database
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.jpa.hibernate.ddl-auto=create-drop"
```

### Application Won't Start
1. Check logs: `logs/spring.log`
2. Verify configuration in `application.yml`
3. Ensure all dependencies installed: `mvn dependency:resolve`
4. Check for port conflicts
5. Verify database connectivity

## Performance Optimization

### JVM Tuning
```bash
java -Xmx512m -Xms256m -jar target/mental-health-hub-1.0.0.jar
```

### Database Connection Pool
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000
```

### Caching
Enable caching for frequently accessed data:
```yaml
spring:
  cache:
    type: simple
```

## Security Considerations for Production

1. **Change Default Credentials**
2. **Enable HTTPS/TLS**
3. **Configure Spring Security**
4. **Set up WAF (Web Application Firewall)**
5. **Regular Security Audits**
6. **Enable Logging & Monitoring**

## Monitoring & Logging

### Actuator Endpoints
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
  endpoint:
    health:
      show-details: always
```

Access metrics: http://localhost:8080/actuator

## Backup & Recovery

### Database Backup
```bash
# MySQL
mysqldump -u root -p mental_health_hub > backup.sql

# Restore
mysql -u root -p mental_health_hub < backup.sql
```

## Regular Maintenance

- Monitor application logs
- Check database performance
- Update dependencies monthly
- Test backup and recovery procedures
- Review security settings

## Support & Documentation

- Spring Boot Docs: https://spring.io/projects/spring-boot
- Thymeleaf Docs: https://www.thymeleaf.org/
- MySQL Docs: https://dev.mysql.com/doc/

---

For additional support, contact the development team.
