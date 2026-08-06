package com.talnova.tesp.employeeservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MongoCsfleConfig {

    private static final Logger log = LoggerFactory.getLogger(MongoCsfleConfig.class);

    @Value("${app.csfle.kms-key-arn:arn:aws:kms:us-east-1:123456789012:key/tesp-csfle-key}")
    private String kmsKeyArn;

    @Value("${app.csfle.key-vault-namespace:tesp_employee_db.__keyVault}")
    private String keyVaultNamespace;

    public String getKmsKeyArn() {
        return kmsKeyArn;
    }

    public String getKeyVaultNamespace() {
        return keyVaultNamespace;
    }

    public Map<String, Map<String, Object>> createLocalKmsProviderMap() {
        byte[] localMasterKey = new byte[96];
        new SecureRandom().nextBytes(localMasterKey);

        Map<String, Object> localKeyDetails = new HashMap<>();
        localKeyDetails.put("key", localMasterKey);

        Map<String, Map<String, Object>> kmsProviders = new HashMap<>();
        kmsProviders.put("local", localKeyDetails);

        log.info("Initialized MongoDB CSFLE local fallback KMS provider map for key vault namespace '{}'", keyVaultNamespace);
        return kmsProviders;
    }
}
