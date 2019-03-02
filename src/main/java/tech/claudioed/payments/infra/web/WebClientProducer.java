package tech.claudioed.payments.infra.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author claudioed on 2019-03-02.
 * Project payments
 */
@Configuration
public class WebClientProducer {

  @Bean
  public RestTemplate restTemplate(){
    return new RestTemplate();
  }

}
