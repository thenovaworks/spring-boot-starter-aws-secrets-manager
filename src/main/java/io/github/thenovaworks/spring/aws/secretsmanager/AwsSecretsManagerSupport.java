package io.github.thenovaworks.spring.aws.secretsmanager;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AwsSecretsManagerSupport {
    private final SecretsManagerClient client;

    private final ObjectMapper objectMapper;

    private final ConcurrentHashMap<String, Map<String, String>> CACHE = new ConcurrentHashMap<>();

    private final TypeReference<Map<String, String>> typeRef = new TypeReference<>() {
    };

    public AwsSecretsManagerSupport(SecretsManagerClient client) {
        super();
        this.client = client;
        this.objectMapper = new ObjectMapper();
    }

    private Map<String, String> getSecrets(final String secretName, final boolean fullname) {
        final Map<String, String> resultMap = new HashMap<>();
        final GetSecretValueRequest request = GetSecretValueRequest.builder().secretId(secretName).build();
        final GetSecretValueResponse response = client.getSecretValue(request);
        final String result = response.secretString();

        if (!StringUtils.hasText(result)) {
            return resultMap;
        }

        try {
            final Map<String, String> valueMap = objectMapper.readValue(result, typeRef);
            if (!fullname) {
                return valueMap;
            }
            for (Map.Entry<String, String> map : valueMap.entrySet()) {
                final String key = secretName + "/" + map.getKey();
                resultMap.put(key, map.getValue());
            }
            return resultMap;
        } catch (JacksonException je) {
            try {
                if (fullname) {
                    resultMap.put(secretName, result);
                } else {
                    String[] parts = response.name().split("/");
                    final String key = parts[parts.length - 1];
                    resultMap.put(key, result);
                }
                return resultMap;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Map<String, String> getValueMap(String secretName, boolean fullname, String cacheKey) {
        if (CACHE.containsValue(cacheKey)) {
            return CACHE.get(cacheKey);
        }
        synchronized (this.CACHE) {
            if (CACHE.get(cacheKey) != null) {
                return CACHE.get(cacheKey);
            }
            final Map<String, String> value = getSecrets(secretName, fullname);
            assert value != null;
            CACHE.put(cacheKey, value);
            return value;
        }
    }

    public Map<String, String> getMap(final String secretName) {
        boolean fullname = false;
        final String cacheKey = secretName + "." + fullname;
        return getValueMap(secretName, fullname, cacheKey);
    }

    public Map<String, String> getMap(final SecretsValue secretsValue) {
        final String secretName = secretsValue.value();
        final boolean fullname = secretsValue.fullname();
        final String cacheKey = secretName + "." + fullname;
        return getValueMap(secretName, fullname, cacheKey);
    }

    private String[] split(final String name) {
        if (name == null || !name.contains(".")) {
            throw new IllegalArgumentException("The name attribute must be hava a  delimiter with `.`. Ex) `my-secret-name.username`");
        }
        return name.split("\\.");
    }

    public String getValue(final String name) {
        String[] var = split(name);
        final String secretName = var[0];
        final String keyName = var[1];
        final Map<String, String> map = getMap(secretName);
        return map.get(keyName);
    }

    public String[] keys(final String secretName) {
        final Map<String, String> map = getMap(secretName);
        if (map == null) {
            return null;
        }
        return map.keySet().toArray(new String[map.keySet().size()]);
    }
}
