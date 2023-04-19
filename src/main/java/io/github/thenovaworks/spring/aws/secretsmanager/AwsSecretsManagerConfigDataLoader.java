package io.github.thenovaworks.spring.aws.secretsmanager;

import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.context.config.ConfigDataLoader;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;

import java.io.IOException;
import java.util.Collections;

public class AwsSecretsManagerConfigDataLoader implements ConfigDataLoader<AwsSecretsManagerConfigDataResource> {

    public boolean isLoadable(ConfigDataLoaderContext context, AwsSecretsManagerConfigDataResource resource) {
        return ConfigDataLoader.super.isLoadable(context, resource);
    }

    public ConfigData load(ConfigDataLoaderContext context, AwsSecretsManagerConfigDataResource resource)
            throws IOException, ConfigDataResourceNotFoundException {
        try {
            SecretsManagerPropertySource propertySource = resource.getPropertySource();
            if (propertySource != null) {
                return new ConfigData(Collections.singletonList(propertySource));
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new ConfigDataResourceNotFoundException(resource, e);
        }
    }
}
