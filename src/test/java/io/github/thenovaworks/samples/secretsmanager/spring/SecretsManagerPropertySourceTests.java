package io.github.thenovaworks.samples.secretsmanager.spring;

import io.github.thenovaworks.spring.aws.secretsmanager.SecretsManagerPropertySource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;

import java.util.Arrays;

public class SecretsManagerPropertySourceTests extends AbstractSecretsTests {

    private Object getValue(ConfigurationPropertySource source, String name) {
        ConfigurationProperty property = source.getConfigurationProperty(ConfigurationPropertyName.of(name));
        return (property != null) ? property.getValue() : null;
    }

    @Test
    void get_username() {
        SecretsManagerPropertySource source = new SecretsManagerPropertySource("dev/aurora/apple", super.getAwsSecretsManagerSupport());
        System.out.println(source.getProperty("username"));
        System.out.println(source.getProperty("password"));
        System.out.println(source.getProperty("dbClusterIdentifier"));
        System.out.println(source.getProperty("engine"));
        System.out.println(source.getProperty("port"));
        System.out.println(source.getProperty("host"));
    }

    @Test
    void get_keys() {
        SecretsManagerPropertySource source = new SecretsManagerPropertySource("dev/aurora/apple", super.getAwsSecretsManagerSupport());
        System.out.println(Arrays.toString(source.getPropertyNames()));
    }

}
