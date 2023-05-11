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

이 결과로, Bean 클래스의 username 속성은 `dev/simplydemo/oauth` 의 보안 암호는 oauthInfo Map 객체에 바인딩 됩니다.



## What to do First?

AWS Secrets Manager 를 액세스 하려면 Spring Boot 애플리케이션이 제대로 동작 하도록 spring-boot-starter-aws-secrets-manager 를 추가 하기만 하면 됩니다.

- Maven

```
    <dependencies>
        <dependency>
          <groupId>io.github.thenovaworks</groupId>
          <artifactId>spring-boot-starter-aws-secrets-manager</artifactId>
          <version>1.0.3</version>
        </dependency>
    </dependencies>
```

- Gradle

```
dependencies {
	implementation 'io.github.thenovaworks:spring-boot-starter-aws-secrets-manager:1.0.3'
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
  config:
    import: "secretsmanager:dev/simplydemo/apple;dev/simplydemo/oauth"        
```

`spring.config.import` 속성은 참조할 secret name 을 정의 합니다.  
secretsmanager 를 식별 하기 위한 접두어로 'secretsmanager:' 로 시작하며, 하나 이상일 경우 ';' 캐릭터를 구분자로 사용 합니다.   
위 예제는 'dev/simplydemo/apple' 와 'dev/simplydemo/oauth' secret name 을 참조 합니다. 

<br>

#### For Local Test

로컬 테스트 환경을 위해선 아래와 같이 "provider-type" 과 "profile" 속성을 설정 하고 AWS Secrets Manager 에 저장된 경로의 보안 값을 액세스 할 수 있습니다.      
AWS Profile 에 관련된 설정은 AWS [Configuration and credential file settings](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html) 가이드를 참고 합니다.

- AWS Profile 을 참조 속성을 아래와 같이 정의 하여 Secrets Manager 를 액세스 할 수 있습니다. 
```
spring:
  cloud:
    aws:
      secrets-manager:
        provider-type: profile
        profile: <your_profile>
  config:
    import: "secretsmanager:dev/simplydemo/apple"        
```

- `AWS_PROFILE` OS 환경 변수를 참조 하려면 아래와 같이 설정 할 수 있습니다. 
```
$ export AWS_PROFILE=your-aws-profile
$ java jar myapp.jar
```

```
spring:
  cloud:
    aws:
      secrets-manager:
        provider-type: profile
  config:
    import: "secretsmanager:dev/simplydemo/apple"   
```

- 위 설정에서 Java 애플리케이션 실행시 사용자 정의 환경 변수를 지정하여 profile 을 정의 할 수 있습니다.
```
java jar -DAWS_PROFILE=your-aws-profile myapp.jar
```


<br>

- AWS Environments 환경 변수를 참조하여 보안 문자열을 액세스 합니다. 

```
spring:
  cloud:
    aws:
      secrets-manager:
        provider-type: environment
  config:
    import: "secretsmanager:dev/simplydemo/apple"
```

environment 인증 방식은 아래와 같이 `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` 와 `AWS_SESSION_TOKEN` 환경 변수를 설정 하여야 합니다. 

```
$ export AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
$ export AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
$ export AWS_SESSION_TOKEN=AQoDYXdzEJr...<remainder of security token>

$ java jar myapp.jar
```
[guide_credentials_environment](https://docs.aws.amazon.com/ko_kr/sdk-for-php/v3/developer-guide/guide_credentials_environment.html) 참조 

<br>

### Spring Bean 에서의 참조
Spring Bean 컴포넌트 내에서 secrets manager 의 secret name 를 참조하는 예제 입니다.   

`SampleHelloBean` Spring Bean 컴포넌트를 정의하여 `@SecretsValue` 또는 `@Value` 어노테이션으로 쉽게 참조 할 수 있습니다.  
```
import io.github.thenovaworks.spring.aws.secretsmanager.SecretsValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SampleHelloBean {

    @SecretsValue("dev/simplydemo/oauth")
    private Map<String, String> oauthInfo;

    @Value("dev/simplydemo/oauth.client_id")
    private String clientId;

}
```

<br>

### Spring Proeprties 파일에서의 참조
Spring Proeprties 설정 파일 내에서 secret name 의 속성을 참조 할 수 있습니다. 

아래 예시는 `dev/simplydemo/apple` secret name 의 `username` 과 `password` 속성을 참조 하여 spring 에서 datasource 를 액세스하기 위한 구성 정보로 사용 되었습니다.  

```yaml
spring:
 datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: "jdbc:mysql://${dev/simplydemo/apple.host}:3306?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useSSL=true&serverTimezone=UTC&tinyInt1isBit=false"
    username: "${dev/simplydemo/apple.username}"
    password: "${dev/simplydemo/apple.password}"
```


<br>

## Appendix

### spring-cloud-aws-starter-parameter-store

AWS Secrets Manager 값을 참조하는 기능은 [spring-cloud](https://spring.io/projects/spring-cloud) 프로젝트의 [spring-cloud-aws-starter-parameter-store](https://github.com/awspring/spring-cloud-aws/tree/main/spring-cloud-aws-starters/spring-cloud-aws-starter-secrets-manager) 모듈에서 이미 구현해 놓았습니다.  
참고로, 해당 기능은 [PropertySource](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.external-config) 에 Parameter Value 값을 주입하고 @Value 어노테이션으로 액세스 합니다.  
참고로, spring-cloud-aws-starter-parameter-store 모듈은 값을 참조할 때 JSON 노드를 탐색해야 하는 조금의 불편함이 있어서 Map 객체로 바로 바인딩 하도록 이 프로젝트를 구현하였습니다. 


### Reference Documentation

* [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/3.0.x/reference/html/)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.x/maven-plugin/reference/htmlsingle/)


