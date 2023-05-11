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

    private Map<String, String> getSecrets(final String secretName) {
        final GetSecretValueRequest request = GetSecretValueRequest.builder().secretId(secretName).build();
        final GetSecretValueResponse response = client.getSecretValue(request);
        final String result = response.secretString();

        if (!StringUtils.hasText(result)) {
            return new HashMap<>();
        }

        try {
            return objectMapper.readValue(result, typeRef);
        } catch (JacksonException je) {
            throw new RuntimeException(je.getMessage());
        }
    }

    private Map<String, String> getValueMap(String secretName) {
        if (CACHE.containsValue(secretName)) {
            return CACHE.get(secretName);
        }
        synchronized (this.CACHE) {
            if (CACHE.get(secretName) != null) {
                return CACHE.get(secretName);
            }
            final Map<String, String> value = getSecrets(secretName);
            assert value != null;
            CACHE.put(secretName, value);
            return value;
        }
    }

    public Map<String, String> getMap(final String secretName) {
        return getValueMap(secretName);
    }

    public Map<String, String> getMap(final SecretsValue secretsValue) {
        final String secretName = secretsValue.value();
        return getValueMap(secretName);
    }

    public Object getValue(final SecretsValue secretsValue) {
        final String secretName = secretsValue.value();
        final String name = secretsValue.name();
        if ("".equals(name)) {
            return getValueMap(secretName);
        }
        return getValueMap(secretName).get(name);
    }

    public String getValue(final String secretName, final String name) {
        if (!name.contains(secretName)) {
            return null;
        }
        final String propertyName = name.substring(secretName.length() + 1);
        final Map<String, String> map = getMap(secretName);
        return map.get(propertyName);
    }

    public String[] keys(final String secretName) {
        final Map<String, String> map = getMap(secretName);
        if (map == null) {
            return null;
        }
        return map.keySet().toArray(new String[map.keySet().size()]);
    }

}
