package io.github.thenovaworks.spring.aws.secretsmanager;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SecretsValue {
    String value();
    boolean fullname() default false;
}
