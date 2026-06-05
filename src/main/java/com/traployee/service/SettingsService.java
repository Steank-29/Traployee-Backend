package com.traployee.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.traployee.model.Settings;
import com.traployee.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class SettingsService {
    
    @Autowired
    private SettingsRepository settingsRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public Map<String, Object> getSettings(String section) {
        Optional<Settings> settingsOpt = settingsRepository.findBySection(section);
        if (settingsOpt.isPresent()) {
            try {
                String jsonData = settingsOpt.get().getSettingsData();
                if (jsonData != null && !jsonData.isEmpty()) {
                    return objectMapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }
    
    public boolean saveSettings(String section, Map<String, Object> data, String updatedBy) {
        try {
            String jsonData = objectMapper.writeValueAsString(data);
            Optional<Settings> existingOpt = settingsRepository.findBySection(section);
            
            if (existingOpt.isPresent()) {
                Settings existing = existingOpt.get();
                existing.setSettingsData(jsonData);
                existing.setUpdatedAt(LocalDateTime.now());
                existing.setUpdatedBy(updatedBy);
                settingsRepository.save(existing);
            } else {
                Settings newSettings = new Settings();
                newSettings.setSection(section);
                newSettings.setSettingsData(jsonData);
                newSettings.setUpdatedAt(LocalDateTime.now());
                newSettings.setUpdatedBy(updatedBy);
                settingsRepository.save(newSettings);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateSetting(String section, String key, Object value, String updatedBy) {
        Map<String, Object> settings = getSettings(section);
        settings.put(key, value);
        return saveSettings(section, settings, updatedBy);
    }
    
    public <T> T getSetting(String section, String key, Class<T> type) {
        Map<String, Object> settings = getSettings(section);
        Object value = settings.get(key);
        if (value != null) {
            try {
                return type.cast(value);
            } catch (ClassCastException e) {
                return null;
            }
        }
        return null;
    }
}