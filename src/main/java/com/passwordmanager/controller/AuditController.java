package com.passwordmanager.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.passwordmanager.entity.User;
import com.passwordmanager.service.PasswordVaultService;
import com.passwordmanager.service.UserService;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    // Create a logger for this class to track method calls and events
    private static final Logger logger = LoggerFactory.getLogger(AuditController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordVaultService vaultService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getAuditReport(@PathVariable Long userId,
                                            @RequestParam(defaultValue = "90") int daysThreshold) {

        // Log that this method has been entered
        logger.info("Entered getAuditReport method for userId: {}", userId);

        // Log that we are retrieving the user details
        logger.info("Fetching user details from UserService");
        User user = userService.getUserById(userId);

        // Log that we are generating the audit report
        logger.info("Generating audit report with daysThreshold: {}", daysThreshold);

        Map<String, Object> report = new HashMap<>();

        // Log before fetching weak passwords
        logger.info("Fetching weak passwords for userId: {}", userId);
        report.put("weakPasswords", vaultService.getWeakPasswords(user));

        // Log before fetching reused passwords
        logger.info("Fetching reused passwords for userId: {}", userId);
        report.put("reusedPasswords", vaultService.getReusedPasswords(user));

        // Log before fetching old passwords
        logger.info("Fetching old passwords for userId: {} with threshold: {}", userId, daysThreshold);
        report.put("oldPasswords", vaultService.getOldPasswords(user, daysThreshold));

        // Log that we are about to return the report
        logger.info("Exiting getAuditReport method for userId: {}", userId);

        return ResponseEntity.ok(report);
    }
}