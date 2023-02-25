# spring-boot-starter-aws-secrets-manager

spring-boot-starter-aws-secrets-manager 프로젝트는 AWS Secrets Manager 를 액세스 하는 spring-boot 의 Auto Configuration 구성을 지원 합니다.

<br>

## Usage

이 모듈은 Spring Framework 제공 하는 확장 기능 중 하나인 [BeanPostProcessor](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-extension-bpp) 을 이용 하여,  
`SecretsValue` 어노테이션에 해당하는 속성 값을 Spring Bean 에 자동적으로 주입합니다.

<br>

- `dev/simplydemo/oauth` Secrets Manager 보안 암호 이름 예시 

```
    @SecretsValue("dev/simplydemo/oauth")
    private Map<String, String> oauthInfo;
```

이 결과로, Bean 클래스의 username 속성은 dev/simplydemo/oauth 의 보안 암호는 oauthInfo Map 객체에 바인딩 됩니다.



## What to do First?

AWS Secrets Manager 를 액세스 하려면 Spring Boot 애플리케이션이 제대로 동작 하도록 spring-boot-starter-aws-secrets-manager 를 추가 하기만 하면 됩니다.

- Maven

```
    <dependencies>
        <dependency>
          <groupId>io.github.thenovaworks</groupId>
          <artifactId>spring-boot-starter-aws-secrets-manager</artifactId>
          <version>0.9.1</version>
        </dependency>
    </dependencies>
```

- Gradle

```
dependencies {
	implementation 'io.github.thenovaworks:spring-boot-starter-aws-secrets-manager:0.9.1'
}
```

### Application Properties

spring-boot 의 [application-properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html) 설정 방식과 동일 하게 설정 합니다.

`application.yaml` 또는 `application.properties` 설정 파일에 아래와 같이 AWS Secrets Manager 를 액세스 할 수 있도록 "spring.cloud.aws.secrets-manager.provider-type" 속성을 설정 합니다.

#### For Production

Production 서비스 환경을 위해 "spring.cloud.aws.ssm.provider-type" 값을 "default" 으로 설정 합니다.

이렇게 하면, EC2, ECS, Lambda 와 같은 애플리케이션을 구현 했을 때 인증을 위해 내부적으로 [AssumeRole](https://docs.aws.amazon.com/STS/latest/APIReference/API_AssumeRole.html) 을 사용 하게 되고,
[DefaultCredentialsProvider](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/auth/credentials/DefaultCredentialsProvider.html) 을 통해 자동적으로 인증 하게 됩니다.
이렇게 하면 소스 코드 에서 accessKey 가 노출 되지 않고 안전 하게 액세스 할 수 있습니다.

```
spring:
  cloud:
    aws:
      secrets-manager:
        provider-type: default        
```

#### For Local Test

로컬 테스트 환경을 위해선 아래와 같이 "provider-type" 과 "profile" 속성을 설정 하고 AWS Secrets Manager 에 저장된 경로의 보안 값을 액세스 할 수 있는지 확인 할 수 있습니다.      
AWS Profile 에 관련된 설정은 AWS [Configuration and credential file settings](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html) 가이드를 참고 합니다.

- AWS Profile 을 참조하여 보안 문자열을 액세스 합니다. 
```
spring:
  cloud:
    aws:
      ssm:
        provider-type: profile
        profile: <your_profile>
```

 
- AWS Environments 환경 변수를 참조하여 보안 문자열을 액세스 합니다. 

```
spring:
  cloud:
    aws:
      ssm:
        provider-type: environment
```



### Spring Bean

아래 `SampleHelloBean` 클래스와 같이  쉽게 사용할 수 있습니다. 
```
import io.symplesims.spring.aws.secretsmanager.autoconfigure.SecretsValue;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SampleHelloBean {

    @SecretsValue("dev/simplydemo/oauth")
    private Map<String, String> oauthInfo;

    @SecretsValue(value = "dev/simplydemo/mysql", fullname = true)
    private Map<String, String> mysqlInfo;
 
}
```


<br>

## Appendix

### spring-cloud-aws-starter-parameter-store

AWS Secrets Manager 값을 참조하는 기능은 [spring-cloud](https://spring.io/projects/spring-cloud) 프로젝트의 [spring-cloud-aws-starter-parameter-store](https://github.com/awspring/spring-cloud-aws/tree/main/spring-cloud-aws-starters/spring-cloud-aws-starter-secrets-manager) 모듈에서 이미 구현해 놓았습니다.  
참고로, 해당 기능은 [PropertySource](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.external-config) 에 Parameter Value 값을 주입하고 @Value 어노테이션으로 액세스 합니다.  
참고로, spring-cloud-aws-starter-parameter-store 모듈은 값을 침저할 때 JSON 노드를 탐색해야 하는 조금의 불편함이 있어서 Map 객체로 바로 바인딩 하도록 이 프로젝트를 구현하였습니다. 


### Reference Documentation

* [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/3.0.x/reference/html/)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.x/maven-plugin/reference/htmlsingle/)


