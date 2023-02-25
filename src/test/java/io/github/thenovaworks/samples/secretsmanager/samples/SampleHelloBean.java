package io.github.thenovaworks.samples.secretsmanager.samples;

import io.github.thenovaworks.spring.aws.secretsmanager.autoconfigure.SecretsValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SampleHelloBean {

    @Value("${spring.cloud.aws.secrets-manager.region:ap-northeast-2}")
    private String awsRegion;

    @SecretsValue("dev/simplydemo/oauth")
    private Map<String, String> oauthInfo;

    public String getAwsRegion() {
        return awsRegion;
    }

    public Map<String, String> getOauthInfo() {
        return oauthInfo;
    }

}
