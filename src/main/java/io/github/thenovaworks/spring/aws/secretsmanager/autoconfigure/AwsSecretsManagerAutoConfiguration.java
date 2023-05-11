package io.github.thenovaworks.spring.aws.secretsmanager.autoconfigure;

import io.github.thenovaworks.spring.aws.secretsmanager.AwsSecretsManagerProperties;
import io.github.thenovaworks.spring.aws.secretsmanager.AwsSecretsManagerSupport;
import io.github.thenovaworks.spring.aws.secretsmanager.SecretsManagerClientFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClientBuilder;

@EnableConfigurationProperties(AwsSecretsManagerProperties.class)
@ConditionalOnClass({SecretsManagerClient.class, SecretsManagerClientBuilder.class})
@AutoConfiguration
public class AwsSecretsManagerAutoConfiguration {

    private final AwsSecretsManagerProperties properties;

    public AwsSecretsManagerAutoConfiguration(AwsSecretsManagerProperties properties) {
        this.properties = properties;
    }

    @Bean
    AwsSecretsManagerSupport awsSecretsManagerSupport() {
        final SecretsManagerClient client = SecretsManagerClientFactory.getInstance().buildClient(this.properties);
        return new AwsSecretsManagerSupport(client);
    }

    @Bean
    AwsSecretsManagerValueResolver autowireCandidateResolver() {
        return new AwsSecretsManagerValueResolver(awsSecretsManagerSupport());
    }

    @Bean
    public AwsSecretsManagerBeanPostProcessor awsSsmParameterValueBeanPostProcessor(final ConfigurableListableBeanFactory configurableBeanFactory, final AwsSecretsManagerValueResolver candidateResolver, final AwsSecretsManagerProperties properties) {
        return new AwsSecretsManagerBeanPostProcessor(configurableBeanFactory, candidateResolver, properties);
    }

}
