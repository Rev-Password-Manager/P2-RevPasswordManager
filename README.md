🔐 RevPasswordManager Web Application

RevPasswordManager is a full-stack monolithic password management web application developed as part of the Revature Training Program (Phase 2).

The system allows users to securely store, manage, and generate passwords for different online accounts. It focuses on strong security practices including password encryption, security questions, two-factor authentication, verification codes, and password strength analysis.

🚀 Tech Stack & Tools
Frontend

HTML

CSS

JavaScript

Bootstrap

Backend

Spring Boot

Spring Security

REST APIs

Database

Oracle Database

Security

BCrypt Password Encryption

Two-Factor Authentication (Google Authenticator)

Verification Code Authentication

Development Tools

IntelliJ IDEA

Maven

Git & GitHub

Postman (API Testing)

🏗️ Architecture & Design

Layered Monolithic Architecture

MVC (Model-View-Controller) Pattern

Spring Boot REST API Architecture

Secure Authentication & Authorization

Oracle Database Integration

Exception Handling & Validation

Password Encryption using BCrypt

Two-Factor Authentication Support

👤 User Functionalities
Authentication & Account Management

User Registration

Secure Login using Master Password

Password Encryption using BCrypt

Logout functionality

Profile Management

Forgot Password using Security Questions

🔐 Password Vault Management

Users can securely store credentials including:

Account Name

Website URL

Username / Email

Password

Category

Notes

Features

Add Password Entry

Update Password Entry

Delete Password Entry

Search Passwords

Filter by Category

Mark Passwords as Favourite

View Favourite Passwords

🔑 Password Generator

The application provides a secure password generator with customizable options:

Password Length (8–64 characters)

Uppercase letters

Lowercase letters

Numbers

Special Characters

Exclude Similar Characters

Additional Features:

Password Strength Indicator

Copy generated password

Directly save generated password to vault

🔐 Security Features

To ensure maximum security, the system implements:

BCrypt password hashing

Security questions for account recovery

Two-Factor Authentication (2FA)

Verification codes for sensitive operations

Password strength analysis

Security audit for weak passwords

Encrypted password storage

📊 Core Modules

Authentication & Account Management

Password Vault Management

Password Generator

Security Questions Management

Two-Factor Authentication

Security Audit & Password Strength Analysis

Verification Code Authentication

📂 Project Structure
src
├── main
│    ├── java/com/passwordmanager
│    │     ├── controller
│    │     ├── service
│    │     ├── repository
│    │     ├── entity
│    │     ├── security
│    │     ├── dto
│    │     ├── config
│    │     └── exception
│    └── resources
│
└── test
     └── java/com/passwordmanager
           ├── controller
           ├── service
           └── repository
🗄️ Database Tables (Oracle)

USERS

PASSWORD_ENTRIES

SECURITY_QUESTIONS

USER_SECURITY_ANSWERS

VERIFICATION_CODES

📦 Repository Information

Project: RevPasswordManager
Program: Revature Java Full Stack Training (Phase 2)
Architecture: Layered Monolithic – Spring Boot MVC
Database: Oracle

🔒 Note

🚧 This project is developed for educational and training purposes as part of the Revature Full Stack Program.

The repository contains the complete implementation of RevPasswordManager including backend APIs, frontend UI, and database integration.
