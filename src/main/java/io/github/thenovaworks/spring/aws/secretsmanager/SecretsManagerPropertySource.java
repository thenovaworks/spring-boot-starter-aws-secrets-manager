package io.github.thenovaworks.spring.aws.secretsmanager;

import org.springframework.core.env.EnumerablePropertySource;

public class SecretsManagerPropertySource extends EnumerablePropertySource<AwsSecretsManagerSupport> {

    public SecretsManagerPropertySource(String name, AwsSecretsManagerSupport source) {
        super(name, source);
    }

    public String[] getPropertyNames() {
        return source.keys(super.getName());
    }

    public Object getProperty(String name) {
        final String keyName = super.getName() + "." + name;
        return source.getValue(keyName);
    }

}
