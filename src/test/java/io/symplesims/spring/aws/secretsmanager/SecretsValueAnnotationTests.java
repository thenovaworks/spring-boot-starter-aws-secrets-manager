package io.symplesims.spring.aws.secretsmanager;

import io.symplesims.spring.aws.secretsmanager.samples.SampleHelloBean;
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

    @Test
    void contextLoads() {
        assertNotNull(sampleHelloBean);
    }

    @Test
    public void test_hello() {
        String region = sampleHelloBean.getAwsRegion();
        Assertions.assertEquals("ap-northeast-2", region);
        System.out.println("region: " + region);

        Map<String, String> secretInfo = sampleHelloBean.getOauthInfo();
        assertNotNull(secretInfo);
        System.out.println("alertnowRdsInfo: " + secretInfo);

        Map<String, String> secretData = sampleHelloBean.getMysqlInfo();
        assertNotNull(secretData);
        System.out.println("spiderRdsInfo: " + secretData);
    }

}
