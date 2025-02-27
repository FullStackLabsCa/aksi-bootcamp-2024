package org.reactivestax.canada_active_life.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() throws Exception {
        return RestTemplateFactory.createRestTemplate("src/main/resources/cal-keystore.p12", "src/main/resources/cal-truststore.p12", "changeit");
    }
}
