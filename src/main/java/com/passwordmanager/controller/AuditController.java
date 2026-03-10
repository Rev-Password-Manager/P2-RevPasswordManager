package com.passwordmanager.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.passwordmanager.entity.User;
import com.passwordmanager.service.PasswordVaultService;
import com.passwordmanager.service.UserService;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordVaultService vaultService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getAuditReport(@PathVariable Long userId,
                                            @RequestParam(defaultValue = "90") int daysThreshold) {
        User user = userService.getUserById(userId);

        Map<String, Object> report = new HashMap<>();
        report.put("weakPasswords", vaultService.getWeakPasswords(user));
        report.put("reusedPasswords", vaultService.getReusedPasswords(user));
        report.put("oldPasswords", vaultService.getOldPasswords(user, daysThreshold));

        return ResponseEntity.ok(report);
    }
}
