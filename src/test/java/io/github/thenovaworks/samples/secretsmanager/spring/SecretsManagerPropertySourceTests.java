package io.github.thenovaworks.samples.secretsmanager.spring;

import io.github.thenovaworks.spring.aws.secretsmanager.SecretsManagerPropertySource;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class SecretsManagerPropertySourceTests extends AbstractSecretsTests {

    @Test
    void print_secrets() {
        final String secretName = "dev/simplydemo/apple";

        SecretsManagerPropertySource source = new SecretsManagerPropertySource(secretName, super.getAwsSecretsManagerSupport());
        System.out.println(source.getProperty("%s.username".formatted(secretName)));
        System.out.println(source.getProperty("%s.password".formatted(secretName)));
        System.out.println(source.getProperty("%s.dbClusterIdentifier".formatted(secretName)));
        System.out.println(source.getProperty("%s.engine".formatted(secretName)));
        System.out.println(source.getProperty("%s.port".formatted(secretName)));
        System.out.println(source.getProperty("%s.host".formatted(secretName)));
    }

    @Test
    void get_keys() {
        SecretsManagerPropertySource source = new SecretsManagerPropertySource("dev/aurora/apple", super.getAwsSecretsManagerSupport());
        System.out.println(Arrays.toString(source.getPropertyNames()));
    }

}
