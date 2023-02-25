package io.symplesims.spring.aws.secretsmanager;

import io.symplesims.spring.aws.secretsmanager.samples.SampleHelloBean;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("local")
@SpringBootTest
class SecretsValueAnnotationTests {

    final Logger log = LoggerFactory.getLogger(SecretsValueAnnotationTests.class);

    @Autowired
    private SampleHelloBean sampleHelloBean;

    @Test
    void contextLoads() {
        assertNotNull(sampleHelloBean);
    }

    @Test
    public void test_hello() {
        String region = sampleHelloBean.getAwsRegion();
        // Assertions.assertEquals("ap-northeast-2", region);
        log.info("region: {}", region);

        Map<String, String> info = sampleHelloBean.getOauthInfo();
        assertNotNull(info);
        log.info("oauthInfo: {}", info);
    }

}
