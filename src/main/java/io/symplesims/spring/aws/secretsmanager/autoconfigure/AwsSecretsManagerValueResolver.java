package io.symplesims.spring.aws.secretsmanager.autoconfigure;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AwsSecretsManagerValueResolver implements AutowireCandidateResolver {

    private final SecretsManagerClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    public AwsSecretsManagerValueResolver(SecretsManagerClient client) {
        this.client = client;
    }

    private final TypeReference<Map<String, String>> typeRef = new TypeReference<>() {
    };

    private Map<String, String> getSecrets(final String secretName, final boolean fullname) {
        final Map<String, String> resultMap = new LinkedHashMap<>();
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

    public Object getValue(final SecretsValue secretsValue) {
        final String secretName = secretsValue.value();
        final boolean fullname = secretsValue.fullname();
        final String cacheKey = secretName + "." + fullname;
        if (cache.containsValue(cacheKey)) {
            return cache.get(cacheKey);
        }
        synchronized (this.cache) {
            if (cache.get(cacheKey) != null) {
                return cache.get(cacheKey);
            }
            final Object value = getSecrets(secretName, fullname);
            assert value != null;
            cache.put(cacheKey, value);
            return value;
        }
    }
}
