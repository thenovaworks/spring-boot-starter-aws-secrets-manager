package io.github.thenovaworks.spring.aws.secretsmanager.autoconfigure;

import io.github.thenovaworks.spring.aws.secretsmanager.AwsSecretsManagerProperties;
import io.github.thenovaworks.spring.aws.secretsmanager.SecretsValue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @see org.springframework.beans.factory.annotation.Value
 * @see org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
 * @see org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver
 */
public class AwsSecretsManagerBeanPostProcessor implements BeanPostProcessor {

    private final ConfigurableListableBeanFactory configurableBeanFactory;

    private final AwsSecretsManagerValueResolver candidateResolver;

    private final List<String> packages;

    private static boolean doScanPackage(final List<String> packages, final String packageName) {
        if (packages == null) {
            return false;
        }
        for (final String pkg : packages) {
            if (pkg != null && packageName.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }

    private String getBasePackage() {
        final Map<String, Object> boot = configurableBeanFactory.getBeansWithAnnotation(SpringBootApplication.class);
        return boot.values().stream().findFirst().map(v -> v.getClass().getPackageName()).orElse(null);
    }

    public AwsSecretsManagerBeanPostProcessor(ConfigurableListableBeanFactory configurableBeanFactory, AwsSecretsManagerValueResolver candidateResolver, AwsSecretsManagerProperties properties) {
        this.configurableBeanFactory = configurableBeanFactory;
        this.candidateResolver = candidateResolver;
        String basePackage = getBasePackage();
        List<String> values = properties.getPackages();
        if (values != null) {
            values.add(basePackage);
            this.packages = values;
        } else {
            this.packages = new ArrayList<>();
            this.packages.add(basePackage);
        }
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!doScanPackage(this.packages, bean.getClass().getPackageName())) {
            return bean;
        }
        if (configurableBeanFactory.containsBeanDefinition(beanName)) {
            ReflectionUtils.doWithLocalFields(bean.getClass(), field -> {
                final SecretsValue secretsValue = field.getAnnotation(SecretsValue.class);
                if (secretsValue != null) {
                    final Object value = candidateResolver.getValue(secretsValue);
                    if (value != null) {
                        ReflectionUtils.makeAccessible(field);
                        field.set(bean, value);
                    }
                }
            });
        }
        return bean;
    }

}
