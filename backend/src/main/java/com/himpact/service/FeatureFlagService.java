package com.himpact.service;

import com.himpact.entity.FeatureFlag;
import com.himpact.repository.FeatureFlagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for checking and managing dynamic Feature Flags.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeatureFlagService {

    private final FeatureFlagRepository featureFlagRepository;

    @Transactional(readOnly = true)
    @SuppressWarnings("null") // Eclipse false-positive: Optional.map() guarantees non-null argument to FeatureFlag::isEnabled
    public boolean isFeatureEnabled(String flagName) {
        return featureFlagRepository.findByFlagName(flagName)
                .map(FeatureFlag::isEnabled)
                .orElse(true); // Default to enabled if flag record not created yet
    }

    @Transactional(readOnly = true)
    public List<FeatureFlag> getAllFlags() {
        return featureFlagRepository.findAll();
    }

    @Transactional
    public FeatureFlag toggleFlag(String flagName, boolean enabled) {
        FeatureFlag flag = featureFlagRepository.findByFlagName(flagName)
                .orElseGet(() -> FeatureFlag.builder().flagName(flagName).enabled(enabled).build());
        flag.setEnabled(enabled);
        log.info("Feature flag [{}] set to [{}]", flagName, enabled);
        return featureFlagRepository.save(flag);
    }
}
