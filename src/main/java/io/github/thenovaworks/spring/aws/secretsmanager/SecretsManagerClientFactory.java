package io.github.thenovaworks.spring.aws.secretsmanager;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import java.util.Optional;

public class SecretsManagerClientFactory {

    private static final SecretsManagerClientFactory INSTANCE = new SecretsManagerClientFactory();

    private static final String AWS_PROFILE = "AWS_PROFILE";

    private SecretsManagerClientFactory() {
        super();
    }

    public static SecretsManagerClientFactory getInstance() {
        return SecretsManagerClientFactory.INSTANCE;
    }

    public SecretsManagerClient buildClient(AwsSecretsManagerProperties properties) {
        final software.amazon.awssdk.services.secretsmanager.SecretsManagerClientBuilder builder = SecretsManagerClient.builder();
        if (properties.getRegion() != null) {
            builder.region(Region.of(properties.getRegion()));
        }
        final ProviderType providerType = properties.getProviderType();
        if (providerType == ProviderType.PROFILE) {
            final String profile = Optional.ofNullable(properties.getProfile())
                    .orElse(System.getenv().getOrDefault(AWS_PROFILE,
                            System.getProperty(AWS_PROFILE, "default")));
            builder.credentialsProvider(ProfileCredentialsProvider.create(profile));
        } else if (providerType == ProviderType.ENVIRONMENT) {
            builder.credentialsProvider(EnvironmentVariableCredentialsProvider.create());
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }
        return builder.build();
    }
}
