package io.github.thenovaworks.spring.aws.secretsmanager;

import org.springframework.core.env.EnumerablePropertySource;

public class SecretsManagerPropertySource extends EnumerablePropertySource<AwsSecretsManagerSupport> {

    public SecretsManagerPropertySource(String secretName, AwsSecretsManagerSupport source) {
        super(secretName, source);
    }

    public String[] getPropertyNames() {
        return source.keys(super.getName());
    }

    public Object getProperty(String name) {
        return source.getValue(super.getName(), name);
    }

}
