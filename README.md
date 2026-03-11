🔐 RevPasswordManager Web Application
Full-Stack Monolithic Password Management Application

Revature Training Program | Phase 2 Project | PES Mandya Batch

🔐 Welcome to RevPasswordManager P2 WebApplication

RevPasswordManager is a full-stack monolithic password management web application developed as part of the Revature Training Program (Phase 2) by the PES Mandya Batch.

The application allows users to securely store, manage, and generate passwords for different online accounts. It focuses on enterprise-level security practices including password encryption, security questions, two-factor authentication, verification codes, and password strength analysis.

🚀 Tech Stack & Tools
💻 Frontend

HTML

CSS

JavaScript

Bootstrap

⚙️ Backend

Spring Boot

Spring Security

REST APIs

🗄️ Database

Oracle Database

🔐 Security

BCrypt Password Encryption

Two-Factor Authentication (Google Authenticator)

Verification Code Authentication

🧰 Development Tools

IntelliJ IDEA

Maven

Git & GitHub

Postman (API Testing)

🏗️ Architecture & Design

✨ Enterprise-Level System Design

Layered Monolithic Architecture

MVC (Model-View-Controller) Design Pattern

Secure Authentication & Authorization

Oracle Database Integration

Logging & Exception Handling

Password Encryption using BCrypt

Two-Factor Authentication Support

Input Validation & Error Handling

👤 User Functionalities
🔐 Authentication & Account Management

User Registration

Secure Login using Master Password

Password Encryption using BCrypt

Logout functionality

Profile Management

Forgot Password using Security Questions

🔑 Password Vault Management

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

Filter Passwords by Category

Mark Passwords as Favourite

View Favourite Passwords

🔑 Password Generator

The application provides a secure password generator with customizable options:

Password Length (8–64 characters)

Uppercase Letters

Lowercase Letters

Numbers

Special Characters

Exclude Similar Characters

Additional Features

Password Strength Indicator

Copy Generated Password

Directly Save Generated Password to Vault

🔐 Security Features

To ensure maximum security, the system implements:

BCrypt Password Hashing

Security Questions for Account Recovery

Two-Factor Authentication (2FA)

Verification Codes for Sensitive Operations

Password Strength Analysis

Security Audit for Weak Passwords

Encrypted Password Storage

🧰 Core Modules

Authentication & Account Management

Password Vault Management

Password Generator

Security Questions Management

Two-Factor Authentication

Verification Code Authentication

Security Audit & Password Strength Analysis

👥 Group Members

Sai Sucharitha • Revanth Gowd • Shiva Kumar • Dhanush HD

``📂 Project Structure
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
           └── repository``

🗄️ Database Tables (Oracle)

USERS

PASSWORD_ENTRIES

SECURITY_QUESTIONS

USER_SECURITY_ANSWERS

VERIFICATION_CODES

📂 Repository Information

📦 Project: RevPasswordManager (Password Management System)
🏫 Batch: PES Mandya – Revature Java Full Stack Training Program
🛠️ Architecture: Layered Monolithic (Spring Boot MVC)
🗄️ Database: Oracle
