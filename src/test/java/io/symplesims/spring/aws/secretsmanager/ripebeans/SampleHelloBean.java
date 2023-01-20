package io.symplesims.spring.aws.secretsmanager.ripebeans;

import io.symplesims.spring.aws.secretsmanager.autoconfigure.SecretsValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SampleHelloBean {

    @Value("${spring.cloud.aws.ssm.region:ap-northeast-2}")
    private String awsRegion;

    @SecretsValue("dev/aurora/alertnow")
    private Map<String, String> secretInfo;

    @SecretsValue(value = "SPIDER/DEV/RDS/COST", fullname = true)
    private Map<String, String> secretData;

    public String getAwsRegion() {
        return awsRegion;
    }

    public Map<String, String> getSecretInfo() {
        return secretInfo;
    }

    public Map<String, String> getSecretData() {
        return secretData;
    }
}
