package com.upm.institutional.service;

import com.upm.institutional.model.SiteSetting;
import com.upm.institutional.repository.SiteSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SiteSettingService {

    private final SiteSettingRepository siteSettingRepository;

    public String getSetting(String key, String defaultValue) {
        return siteSettingRepository.findById(key)
                .map(SiteSetting::getSettingValue)
                .orElse(defaultValue);
    }

    public void updateSetting(String key, String value) {
        SiteSetting setting = new SiteSetting(key, value);
        siteSettingRepository.save(setting);
    }
}
