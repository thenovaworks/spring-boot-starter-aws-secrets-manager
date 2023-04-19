package io.github.thenovaworks.spring.aws.secretsmanager;

import org.springframework.boot.context.config.ConfigDataResource;

public class AwsSecretsManagerConfigDataResource extends ConfigDataResource {

    private final String context;

    private final SecretsManagerPropertySource propertySource;

    public AwsSecretsManagerConfigDataResource(final String context,
                                               final SecretsManagerPropertySource propertySource) {
        this.context = context;
        this.propertySource = propertySource;
    }

    public String getContext() {
        return this.context;
    }

    public SecretsManagerPropertySource getPropertySource() {
        return this.propertySource;
    }


}
