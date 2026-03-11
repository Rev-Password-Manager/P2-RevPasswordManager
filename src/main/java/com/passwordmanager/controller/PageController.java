package com.passwordmanager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // for logging
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    // Logger to track method calls and page requests
    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    @GetMapping("/") 
    public String home() { 
        logger.info("Accessed home page, redirecting to login");
        return "redirect:/login"; 
    }

    @GetMapping("/login")
    public String login() {
        logger.info("Accessed login page");
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        logger.info("Accessed register page");
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        logger.info("Accessed dashboard page");
        return "dashboard"; 
    }

    @GetMapping("/verification")
    public String verification() {
        logger.info("Accessed verification page");
        return "verification";
    }

    @GetMapping("/security-questions")
    public String securityQuestions() {
        logger.info("Accessed security questions page");
        return "security-questions";
    }

    @GetMapping("/updatesecurityquestions")
    public String updateSecurityQuestionsPage() {
        logger.info("Accessed update security questions page");
        return "updatesecurityquestions";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        logger.info("Accessed forgot password page");
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword() {
        logger.info("Accessed reset password page");
        return "reset-password";
    }

    @GetMapping("/backup")
    public String backup() { 
        logger.info("Accessed backup page");
        return "backup"; 
    }

    @GetMapping("/no-access")
    public String noAccess() {
        logger.info("Accessed no-access page");
        return "no-access"; 
    }

    @GetMapping("/vault")
    public String vault() {
        logger.info("Accessed vault page");
        return "vault";
    }

    @GetMapping("/generator")
    public String generator() {
        logger.info("Accessed generator page");
        return "generator";
    }

    @GetMapping("/audit")
    public String audit() {
        logger.info("Accessed audit page");
        return "audit";
    }

    @GetMapping("/profile")
    public String profile() {
        logger.info("Accessed profile page");
        return "profile";
    }
}