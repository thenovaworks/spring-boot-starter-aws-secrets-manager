package io.github.thenovaworks.samples.secretsmanager.mybean;

import io.github.thenovaworks.spring.aws.secretsmanager.SecretsValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SampleHelloBean {

    @Value("${spring.cloud.aws.secrets-manager.region:ap-northeast-2}")
    private String awsRegion;

    @Value("${username}")
    private String username;

    public String getUsername() {
        return this.username;
    }

    @Value("${spring.datasource.password}")
    private String password;

    public String getPassword() {
        return this.password;
    }

    @Value("${client_id}")
    private String clientId;

    public String getClientId() {
        return this.clientId;
    }

    @SecretsValue("dev/aurora/apple")
    private Map<String, String> oauthInfo;

    public String getAwsRegion() {
        return awsRegion;
    }

    public Map<String, String> getOauthInfo() {
        return oauthInfo;
    }

}
