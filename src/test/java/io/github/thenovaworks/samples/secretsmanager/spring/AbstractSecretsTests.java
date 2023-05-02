package io.github.thenovaworks.samples.secretsmanager.spring;

import io.github.thenovaworks.spring.aws.secretsmanager.AwsSecretsManagerProperties;
import io.github.thenovaworks.spring.aws.secretsmanager.AwsSecretsManagerSupport;
import io.github.thenovaworks.spring.aws.secretsmanager.ProviderType;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

public abstract class AbstractSecretsTests {


    protected AwsSecretsManagerSupport getAwsSecretsManagerSupport() {
        return getAwsSecretsManagerSupport("simplydemo");
    }

    protected AwsSecretsManagerSupport getAwsSecretsManagerSupport(final String profile) {
        AwsSecretsManagerProperties properties = new AwsSecretsManagerProperties();
        properties.setProviderType(ProviderType.PROFILE);
        properties.setProfile(profile);
        return new AwsSecretsManagerSupport(buildClient(properties));
    }

    private SecretsManagerClient buildClient(AwsSecretsManagerProperties properties) {
        return SecretsManagerClient.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(ProfileCredentialsProvider.create(properties.getProfile()))
                .build();
    }

}
