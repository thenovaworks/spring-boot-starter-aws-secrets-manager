package io.github.thenovaworks.spring.aws.secretsmanager;

import org.springframework.boot.BootstrapContext;
import org.springframework.boot.BootstrapRegistry;
import org.springframework.boot.context.config.*;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import java.util.*;

public class AwsSecretsManagerConfigDataResolver
        implements ConfigDataLocationResolver<AwsSecretsManagerConfigDataResource> {

    public static final String SECRET_MANAGER_IDENTITY = "secretsmanager:";
    private String prefix = SECRET_MANAGER_IDENTITY;

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

    protected AwsSecretsManagerSupport secretsManagerSupport(BootstrapContext context) {
        final AwsSecretsManagerProperties properties = context.get(AwsSecretsManagerProperties.class);
        try {
            final SecretsManagerClient client = SecretsManagerClientFactory.getInstance().buildClient(properties);
            return new AwsSecretsManagerSupport(client);
        } catch (IllegalStateException e) {
            e.printStackTrace();
            // log.warn("Bean of type AwsSecretsManagerConfigDataResolver is not registered: " + e.getMessage());
            throw e;
        }
    }

    protected Set<String> getContexts(String keys) {
        if (StringUtils.hasLength(keys)) {
            return new HashSet<String>(Arrays.asList(keys.split(";")));
        }
        return new HashSet<String>();
    }

    public List<AwsSecretsManagerConfigDataResource> resolveProfileSpecific(ConfigDataLocationResolverContext context, ConfigDataLocation location, Profiles profiles) throws ConfigDataLocationNotFoundException {
        final AwsSecretsManagerProperties properties = loadAwsProperties(context.getBinder());
        registerBean(context, AwsSecretsManagerProperties.class, properties);
        final AwsSecretsManagerSupport support = secretsManagerSupport(context.getBootstrapContext());
        registerBean(context, AwsSecretsManagerSupport.class, support);

        final List<AwsSecretsManagerConfigDataResource> locations = new ArrayList<>();
        final Set<String> contexts = getContexts(location.getNonPrefixedValue(getPrefix()));
        for (final String secretName : contexts) {
            support.getMap(secretName);
            // System.out.println("----- " + secretName);
            SecretsManagerPropertySource propertySource = new SecretsManagerPropertySource(secretName, support);
            AwsSecretsManagerConfigDataResource resource = new AwsSecretsManagerConfigDataResource(secretName, propertySource);
            locations.add(resource);
        }
        if (!location.isOptional() && locations.isEmpty()) {
            throw new ConfigDataLocationNotFoundException(location);
            // throw new RuntimeException("No Secrets Manager keys provided in `spring.config.import=secretsmanager:dev/simplydemo/apple`");
        }
        return locations;
    }

}
