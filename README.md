🏫 OD / Attendance Management System

A secure, location-based On-Duty attendance management system built using Spring Boot, Spring Security, and MySQL.
Designed for colleges to automate event-based attendance, on-duty leave (OD) generation, and email reporting with strong security and scalability.

✨ Key Features

✅ Secure Organizer Login

Role-based access using Spring Security

Passwords encrypted using BCrypt

📍 Location-Based Attendance (Geofencing)

Students can check in only when they are within the allowed GPS radius of the event location

🔗 Token-Based Student Check-in

Each student receives a unique, one-time attendance token

Prevents duplicate or fraudulent check-ins

🗓️ Session-Based Attendance Tracking

Supports multiple sessions per event

Tracks session-wise attendance completion

📄 Automatic OD (On Duty) Generation

Duty Leave is generated automatically when attendance conditions are met

📧 Automated Email Notifications

Sends attendance confirmations and OD reports via SMTP

Tutor/Organizer-based email segmentation

📊 Organizer Dashboard

View events, sessions, students, and attendance logs

Simple UI built with Thymeleaf + HTML/CSS

🛠️ Tech Stack
Language	Java (JDK 17+)
Backend	Spring Boot 3.x
Security	Spring Security, BCrypt
ORM	Spring Data JPA (Hibernate)
Database : MySQL
Frontend : Thymeleaf, HTML, CSS
Build Tool : Maven
Email	Spring Mail (SMTP)
Deployment	Render (Web Service + Cloud DB)
📂 Project Structure
od-attendance-app
├── src/main/java
│   └── com.odapp.attendance
│       ├── controller
│       ├── service
│       ├── repository
│       ├── model
│       └── config
├── src/main/resources
│   ├── templates
│   ├── static
│   └── application.properties
└── pom.xml

⚙️ Local Development Setup
🔹 Prerequisites

Java 17 or higher

Maven 3.x

MySQL Server (Local or Cloud)

🔹 1. Database Setup

Create a MySQL database:

CREATE DATABASE od_attendance_db;

🔹 2. Application Configuration

Update src/main/resources/application.properties:

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/db_name?useSSL=false&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# SMTP Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_16_digit_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

🔹 3. Run the Application
mvn clean package
java -jar target/attendance-0.0.1-SNAPSHOT.jar


Application will start at:

http://localhost:8080

☁️ Deployment (cloud)
🔹 1. Build Command 
mvn clean package

🔹 2. Start Command
java -jar target/attendance-0.0.1-SNAPSHOT.jar

🔐 Security Highlights

Passwords hashed with BCrypt

One-time attendance tokens

Location validation

Session-based attendance locking

🤝 Contribution

Contributions, suggestions, and issues are welcome!
