package io.github.thenovaworks.spring.aws.secretsmanager;

import org.springframework.boot.BootstrapContext;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.context.config.*;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClientBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AwsSecretsManagerConfigDataResolver
        implements ConfigDataLocationResolver<AwsSecretsManagerConfigDataResource> {

    public static final String PREFIX = "secretsmanager:";

    private String prefix = PREFIX;

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    protected String getPrefix() {
        return this.prefix;
    }

    public boolean isResolvable(ConfigDataLocationResolverContext context, ConfigDataLocation location) {
        return location.hasPrefix(getPrefix());
    }

    public List<AwsSecretsManagerConfigDataResource> resolve(ConfigDataLocationResolverContext context, ConfigDataLocation location) throws ConfigDataLocationNotFoundException, ConfigDataResourceNotFoundException {
        return Collections.emptyList();
    }

    protected AwsSecretsManagerProperties loadAwsProperties(Binder binder) {
        return binder.bind(AwsSecretsManagerProperties.CONFIG_PREFIX, Bindable.of(AwsSecretsManagerProperties.class)).orElseGet(AwsSecretsManagerProperties::new);
    }

    protected <C> void registerBean(ConfigDataLocationResolverContext context, Class<C> type, C instance) {
        context.getBootstrapContext().registerIfAbsent(type, BootstrapRegistry.InstanceSupplier.of(instance));
    }

    private SecretsManagerClient buildClient(AwsSecretsManagerProperties properties) {
        final SecretsManagerClientBuilder builder = SecretsManagerClient.builder();
        if (properties.getRegion() != null) {
            builder.region(Region.of(properties.getRegion()));
        }
        final ProviderType providerType = properties.getProviderType();
        if (providerType == ProviderType.PROFILE) {
            builder.credentialsProvider(ProfileCredentialsProvider.create(properties.getProfile()))
                    .overrideConfiguration(ClientOverrideConfiguration.builder().defaultProfileName(properties.getProfile()).build());
        } else if (providerType == ProviderType.ENVIRONMENT) {
            builder.credentialsProvider(EnvironmentVariableCredentialsProvider.create());
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }
        return builder.build();
    }

    protected AwsSecretsManagerSupport secretsManagerSupport(BootstrapContext context) {
        final AwsSecretsManagerProperties properties = context.get(AwsSecretsManagerProperties.class);
        try {
            final SecretsManagerClient client = buildClient(properties);
            return new AwsSecretsManagerSupport(client);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            // log.debug("Bean of type AwsClientConfigurerSecretsManager is not registered: " + e.getMessage());
            return null;
        }
    }

    protected List<String> getContexts(String keys) {
        if (StringUtils.hasLength(keys)) {
            return Arrays.asList(keys.split(";"));
        }
        return Collections.emptyList();
    }

    public List<AwsSecretsManagerConfigDataResource> resolveProfileSpecific(ConfigDataLocationResolverContext context, ConfigDataLocation location, Profiles profiles) throws ConfigDataLocationNotFoundException {
        final AwsSecretsManagerProperties properties = loadAwsProperties(context.getBinder());
        registerBean(context, AwsSecretsManagerProperties.class, properties);

        final AwsSecretsManagerSupport support = secretsManagerSupport(context.getBootstrapContext());
        registerBean(context, AwsSecretsManagerSupport.class, support);

        List<AwsSecretsManagerConfigDataResource> locations = new ArrayList<>();
        List<String> contexts = getContexts(location.getNonPrefixedValue(PREFIX));

        for (final String secretName : contexts) {
            support.getMap(secretName);

//  Cached-Source
//            System.out.println(secretName);
//            Object ooo = support.getMap(secretName);
//            System.out.println("--------------");
//            System.out.println(ooo);
//            System.out.println("--------------");
            SecretsManagerPropertySource propertySource = new SecretsManagerPropertySource(secretName, support);
            AwsSecretsManagerConfigDataResource resource = new AwsSecretsManagerConfigDataResource(secretName, propertySource);
            locations.add(resource);
        }
        if (!location.isOptional() && locations.isEmpty()) {
            throw new ConfigDataLocationNotFoundException(location);
            // throw new RuntimeException("No Secrets Manager keys provided in `spring.cloud.aws.secrets-manager.secret-names=secret/primary,secret/secondary");
        }
        return locations;
    }

}
