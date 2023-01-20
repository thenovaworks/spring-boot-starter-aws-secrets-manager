package io.symplesims.spring.aws.secretsmanager.ripebeans;

import io.symplesims.spring.aws.secretsmanager.autoconfigure.SecretsValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SampleWorldBean {

    @Value("${spring.cloud.aws.ssm.region:us-east-1}")
    private String awsRegion;

    @SecretsValue("dev/aurora/alertnow")
    private Map<String, String> secretInfo;

    @SecretsValue(value = "events!connection/ecstask-event/090f617d-b649-46ae-b392-7d0fbe4a3b9c", fullname = true)
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
