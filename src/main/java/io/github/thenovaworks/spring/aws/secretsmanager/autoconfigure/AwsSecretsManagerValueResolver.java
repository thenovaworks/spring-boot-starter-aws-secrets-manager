package io.github.thenovaworks.spring.aws.secretsmanager.autoconfigure;

import io.github.thenovaworks.spring.aws.secretsmanager.AwsSecretsManagerSupport;
import io.github.thenovaworks.spring.aws.secretsmanager.SecretsValue;
import org.springframework.beans.factory.support.AutowireCandidateResolver;

public class AwsSecretsManagerValueResolver implements AutowireCandidateResolver {

    private final AwsSecretsManagerSupport support;

    public AwsSecretsManagerValueResolver(AwsSecretsManagerSupport support) {
        this.support = support;
    }

    public Object getValue(final SecretsValue secretsValue) {
        return support.getMap(secretsValue);
    }
}
