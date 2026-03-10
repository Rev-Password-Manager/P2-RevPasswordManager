package com.passwordmanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

	@GetMapping("/") 
	public String home() 
	{ 
		return "redirect:/login"; 
	}

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/verification")
    public String verification() {
        return "verification";
    }

    @GetMapping("/security-questions")
    public String securityQuestions() {
        return "security-questions";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword() {
        return "reset-password";
    }

    @GetMapping("/import-export")
    public String importExport() {
        return "import-export";
    }
    
    @GetMapping("/no-access")
    public String noAccess() {
        return "no-access"; // maps to no-access.html
    }

    
}