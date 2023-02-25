package io.symplesims.spring.aws.secretsmanager.samples;

import io.symplesims.spring.aws.secretsmanager.autoconfigure.SecretsValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SampleHelloBean {

    @Value("${spring.cloud.aws.ssm.region:ap-northeast-2}")
    private String awsRegion;

    @SecretsValue("dev/simplydemo/oauth")
    private Map<String, String> oauthInfo;

    @SecretsValue(value = "dev/simplydemo/mysql", fullname = true)
    private Map<String, String> mysqlInfo;

    public String getAwsRegion() {
        return awsRegion;
    }

    public Map<String, String> getOauthInfo() {
        return oauthInfo;
    }

    public Map<String, String> getMysqlInfo() {
        return mysqlInfo;
    }
}
