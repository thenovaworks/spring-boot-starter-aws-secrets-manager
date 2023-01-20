package io.symplesims.spring.aws.secretsmanager.autoconfigure;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClientBuilder;

@EnableConfigurationProperties(AwsSecretsManagerProperties.class)
@ConditionalOnClass({SecretsManagerClient.class, SecretsManagerClientBuilder.class})
@AutoConfiguration
public class AwsSecretsManagerAutoConfiguration {

    private final AwsSecretsManagerProperties properties;

    private SecretsManagerClient buildClient() {
        final SecretsManagerClientBuilder builder = SecretsManagerClient.builder();
        if (properties.getRegion() != null) {
            builder.region(Region.of(properties.getRegion()));
        }
        if (properties.getProfile() != null) {
            builder.credentialsProvider(ProfileCredentialsProvider.create(properties.getProfile()));
        }
        return builder.build();
    }

    public AwsSecretsManagerAutoConfiguration(AwsSecretsManagerProperties properties) {
        this.properties = properties;
    }

    @Bean
    AwsSecretsManagerValueResolver autowireCandidateResolver() {
        return new AwsSecretsManagerValueResolver(buildClient());
    }

    @Bean
    public AwsSecretsManagerBeanPostProcessor awsSsmParameterValueBeanPostProcessor(final ConfigurableListableBeanFactory configurableBeanFactory, final AwsSecretsManagerValueResolver candidateResolver, final AwsSecretsManagerProperties properties) {
        return new AwsSecretsManagerBeanPostProcessor(configurableBeanFactory, candidateResolver, properties);
    }

}
