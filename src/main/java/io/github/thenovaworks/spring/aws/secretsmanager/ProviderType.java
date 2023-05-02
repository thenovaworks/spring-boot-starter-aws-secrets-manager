package io.github.thenovaworks.spring.aws.secretsmanager;

/**
 * @see <a href="DefaultCredentialsProvider">https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/auth/credentials/DefaultCredentialsProvider.html</a>
 */
public enum ProviderType {
    DEFAULT,
    PROFILE,
    ENVIRONMENT,
}

