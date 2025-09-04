package com.hospital.service;

import org.springframework.stereotype.Service;
import java.util.*;
import java.time.LocalDateTime;

@Service
public class SystemSettingsService {

    // In a real application, these would be stored in database or configuration files
    private Map<String, String> systemSettings = new HashMap<>();
    
    public SystemSettingsService() {
        initializeDefaultSettings();
    }

    private void initializeDefaultSettings() {
        // General Settings
        systemSettings.put("hospital.name", "City General Hospital");
        systemSettings.put("hospital.address", "123 Medical Center Drive, Healthcare City");
        systemSettings.put("hospital.phone", "+1-555-HOSPITAL");
        systemSettings.put("hospital.email", "info@citygeneralhospital.com");
        systemSettings.put("hospital.website", "www.citygeneralhospital.com");
        
        // System Settings
        systemSettings.put("system.timezone", "America/New_York");
        systemSettings.put("system.dateFormat", "MM/dd/yyyy");
        systemSettings.put("system.timeFormat", "12-hour");
        systemSettings.put("system.language", "English");
        systemSettings.put("system.currency", "USD");
        
        // Security Settings
        systemSettings.put("security.sessionTimeout", "30");
        systemSettings.put("security.passwordMinLength", "8");
        systemSettings.put("security.requireSpecialChars", "true");
        systemSettings.put("security.maxLoginAttempts", "5");
        systemSettings.put("security.twoFactorAuth", "false");
        
        // Notification Settings
        systemSettings.put("notifications.email.enabled", "true");
        systemSettings.put("notifications.sms.enabled", "false");
        systemSettings.put("notifications.appointment.reminder", "true");
        systemSettings.put("notifications.system.alerts", "true");
        
        // Backup Settings
        systemSettings.put("backup.autoBackup", "true");
        systemSettings.put("backup.frequency", "daily");
        systemSettings.put("backup.retentionDays", "30");
        systemSettings.put("backup.location", "./backups/");
        
        // Performance Settings
        systemSettings.put("performance.cacheEnabled", "true");
        systemSettings.put("performance.maxConcurrentUsers", "100");
        systemSettings.put("performance.sessionCleanupInterval", "60");
        
        // Integration Settings
        systemSettings.put("integration.apiEnabled", "true");
        systemSettings.put("integration.webhooksEnabled", "false");
        systemSettings.put("integration.externalSystems", "none");
    }

    public Map<String, Object> getAllSettings() {
        Map<String, Object> categorizedSettings = new HashMap<>();
        
        categorizedSettings.put("general", getGeneralSettings());
        categorizedSettings.put("system", getSystemSettings());
        categorizedSettings.put("security", getSecuritySettings());
        categorizedSettings.put("notifications", getNotificationSettings());
        categorizedSettings.put("backup", getBackupSettings());
        categorizedSettings.put("performance", getPerformanceSettings());
        categorizedSettings.put("integration", getIntegrationSettings());
        
        return categorizedSettings;
    }

    public Map<String, String> getGeneralSettings() {
        Map<String, String> general = new HashMap<>();
        general.put("hospitalName", systemSettings.get("hospital.name"));
        general.put("hospitalAddress", systemSettings.get("hospital.address"));
        general.put("hospitalPhone", systemSettings.get("hospital.phone"));
        general.put("hospitalEmail", systemSettings.get("hospital.email"));
        general.put("hospitalWebsite", systemSettings.get("hospital.website"));
        return general;
    }

    public Map<String, String> getSystemSettings() {
        Map<String, String> system = new HashMap<>();
        system.put("timezone", systemSettings.get("system.timezone"));
        system.put("dateFormat", systemSettings.get("system.dateFormat"));
        system.put("timeFormat", systemSettings.get("system.timeFormat"));
        system.put("language", systemSettings.get("system.language"));
        system.put("currency", systemSettings.get("system.currency"));
        return system;
    }

    public Map<String, String> getSecuritySettings() {
        Map<String, String> security = new HashMap<>();
        security.put("sessionTimeout", systemSettings.get("security.sessionTimeout"));
        security.put("passwordMinLength", systemSettings.get("security.passwordMinLength"));
        security.put("requireSpecialChars", systemSettings.get("security.requireSpecialChars"));
        security.put("maxLoginAttempts", systemSettings.get("security.maxLoginAttempts"));
        security.put("twoFactorAuth", systemSettings.get("security.twoFactorAuth"));
        return security;
    }

    public Map<String, String> getNotificationSettings() {
        Map<String, String> notifications = new HashMap<>();
        notifications.put("emailEnabled", systemSettings.get("notifications.email.enabled"));
        notifications.put("smsEnabled", systemSettings.get("notifications.sms.enabled"));
        notifications.put("appointmentReminder", systemSettings.get("notifications.appointment.reminder"));
        notifications.put("systemAlerts", systemSettings.get("notifications.system.alerts"));
        return notifications;
    }

    public Map<String, String> getBackupSettings() {
        Map<String, String> backup = new HashMap<>();
        backup.put("autoBackup", systemSettings.get("backup.autoBackup"));
        backup.put("frequency", systemSettings.get("backup.frequency"));
        backup.put("retentionDays", systemSettings.get("backup.retentionDays"));
        backup.put("location", systemSettings.get("backup.location"));
        return backup;
    }

    public Map<String, String> getPerformanceSettings() {
        Map<String, String> performance = new HashMap<>();
        performance.put("cacheEnabled", systemSettings.get("performance.cacheEnabled"));
        performance.put("maxConcurrentUsers", systemSettings.get("performance.maxConcurrentUsers"));
        performance.put("sessionCleanupInterval", systemSettings.get("performance.sessionCleanupInterval"));
        return performance;
    }

    public Map<String, String> getIntegrationSettings() {
        Map<String, String> integration = new HashMap<>();
        integration.put("apiEnabled", systemSettings.get("integration.apiEnabled"));
        integration.put("webhooksEnabled", systemSettings.get("integration.webhooksEnabled"));
        integration.put("externalSystems", systemSettings.get("integration.externalSystems"));
        return integration;
    }

    public boolean updateSetting(String category, String key, String value) {
        try {
            String settingKey = category + "." + key;
            systemSettings.put(settingKey, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateMultipleSettings(Map<String, String> settings) {
        try {
            systemSettings.putAll(settings);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getSetting(String key) {
        return systemSettings.get(key);
    }

    public Map<String, Object> getSystemInfo() {
        Map<String, Object> systemInfo = new HashMap<>();
        
        // System Information
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("osVersion", System.getProperty("os.version"));
        systemInfo.put("serverTime", LocalDateTime.now());
        systemInfo.put("uptime", getSystemUptime());
        
        // Memory Information
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        systemInfo.put("maxMemory", formatMemory(maxMemory));
        systemInfo.put("totalMemory", formatMemory(totalMemory));
        systemInfo.put("usedMemory", formatMemory(usedMemory));
        systemInfo.put("freeMemory", formatMemory(freeMemory));
        systemInfo.put("memoryUsagePercent", (usedMemory * 100) / totalMemory);
        
        // Application Information
        systemInfo.put("applicationName", "Hospital Management System");
        systemInfo.put("version", "1.0.0");
        systemInfo.put("buildDate", "2024-01-01");
        systemInfo.put("environment", "Development");
        
        return systemInfo;
    }

    private String formatMemory(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    private String getSystemUptime() {
        long uptimeMillis = System.currentTimeMillis() - getStartTime();
        long seconds = uptimeMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return String.format("%d days, %d hours", days, hours % 24);
        } else if (hours > 0) {
            return String.format("%d hours, %d minutes", hours, minutes % 60);
        } else {
            return String.format("%d minutes", minutes);
        }
    }

    private long getStartTime() {
        // This is a simplified version - in a real app, you'd track actual start time
        return System.currentTimeMillis() - (2 * 60 * 60 * 1000); // 2 hours ago
    }

    public List<Map<String, Object>> getSystemLogs() {
        List<Map<String, Object>> logs = new ArrayList<>();
        
        // Mock system logs - in a real application, these would come from actual log files
        Map<String, Object> log1 = new HashMap<>();
        log1.put("timestamp", LocalDateTime.now().minusMinutes(5));
        log1.put("level", "INFO");
        log1.put("message", "User admin logged in successfully");
        log1.put("source", "AuthController");
        logs.add(log1);
        
        Map<String, Object> log2 = new HashMap<>();
        log2.put("timestamp", LocalDateTime.now().minusMinutes(15));
        log2.put("level", "INFO");
        log2.put("message", "Database backup completed successfully");
        log2.put("source", "DatabaseService");
        logs.add(log2);
        
        Map<String, Object> log3 = new HashMap<>();
        log3.put("timestamp", LocalDateTime.now().minusMinutes(30));
        log3.put("level", "WARN");
        log3.put("message", "High memory usage detected: 85%");
        log3.put("source", "SystemMonitor");
        logs.add(log3);
        
        Map<String, Object> log4 = new HashMap<>();
        log4.put("timestamp", LocalDateTime.now().minusHours(1));
        log4.put("level", "INFO");
        log4.put("message", "System settings updated by admin");
        log4.put("source", "SettingsController");
        logs.add(log4);
        
        return logs;
    }

    public boolean resetToDefaults() {
        try {
            systemSettings.clear();
            initializeDefaultSettings();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean exportSettings() {
        try {
            // In a real application, this would export settings to a file
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean importSettings(Map<String, String> importedSettings) {
        try {
            systemSettings.putAll(importedSettings);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}