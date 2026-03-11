package com.passwordmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/*
 * PageController
 * ---------------
 * This controller is responsible for handling navigation between
 * different frontend pages in the Password Manager application.
 * Each endpoint returns the name of the HTML page to be rendered.
 */
@Controller
public class PageController {

    /*
     * Redirects the root URL to the login page.
     * Example: http://localhost:8080/  →  /login
     */
    @GetMapping("/")
    public String home()
    {
        return "redirect:/login";
    }

    /*
     * Displays the login page where users can enter
     * their username/email and password to authenticate.
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /*
     * Displays the user registration page
     * for creating a new account.
     */
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    /*
     * Displays the main dashboard page after successful login.
     * The dashboard shows password statistics and user data.
     */
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    /*
     * Displays the verification page used for
     * OTP or two-factor authentication verification.
     */
    @GetMapping("/verification")
    public String verification() {
        return "verification";
    }

    /*
     * Displays the security questions page where users
     * can set or answer security questions for account recovery.
     */
    @GetMapping("/security-questions")
    public String securityQuestions() {
        return "security-questions";
    }

    /*
     * Displays the forgot password page where users
     * initiate the password recovery process.
     */
    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    /*
     * Displays the reset password page where users
     * can set a new password after verification.
     */
    @GetMapping("/reset-password")
    public String resetPassword() {
        return "reset-password";
    }

    /*
     * Displays the page for importing or exporting
     * password vault data.
     */
    @GetMapping("/import-export")
    public String importExport() {
        return "import-export";
    }

    /*
     * Displays the access denied page when a user tries
     * to access a resource without proper authorization.
     */
    @GetMapping("/no-access")
    public String noAccess() {
        return "no-access"; // maps to no-access.html
    }
}