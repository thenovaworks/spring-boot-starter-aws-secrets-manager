package io.symplesims.spring.aws.secretsmanager;

import io.symplesims.spring.aws.secretsmanager.ripebeans.SampleHelloBean;
import io.symplesims.spring.aws.secretsmanager.ripebeans.SampleWorldBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("local")
@SpringBootTest
class SecretsValueAnnotationTests {

    @Autowired
    private SampleHelloBean sampleHelloBean;

    @Autowired
    private SampleWorldBean sampleWorldBean;

    @Test
    void contextLoads() {
        assertNotNull(sampleHelloBean);
        assertNotNull(sampleWorldBean);
    }

    @Test
    public void test_hello() {
        String region = sampleHelloBean.getAwsRegion();
        Assertions.assertEquals("ap-northeast-2", region);
        System.out.println("region: " + region);

        Map<String, String> secretInfo = sampleHelloBean.getSecretInfo();
        assertNotNull(secretInfo);
        System.out.println("alertnowRdsInfo: " + secretInfo);

        Map<String, String> secretData = sampleHelloBean.getSecretData();
        assertNotNull(secretData);
        System.out.println("spiderRdsInfo: " + secretData);

    }

    @Test
    public void test_world() {
        String region = sampleWorldBean.getAwsRegion();
        Assertions.assertEquals("us-east-1", region);
        System.out.println("region: " + region);

        Map<String, String> secretInfo = sampleWorldBean.getSecretInfo();
        assertNotNull(secretInfo);
        System.out.println("secretInfo: " + secretInfo);

        Map<String, String> secretData = sampleWorldBean.getSecretData();
        assertNotNull(secretData);
        System.out.println("secretData: " + secretData);
    }

}

