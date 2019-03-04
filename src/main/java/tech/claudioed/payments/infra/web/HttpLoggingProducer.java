package tech.claudioed.payments.infra.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * @author claudioed on 2019-03-04.
 * Project payment-checker
 */
@Configuration
public class HttpLoggingProducer {

  @Bean
  public CommonsRequestLoggingFilter requestLoggingFilter() {
    CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
    loggingFilter.setIncludeClientInfo(true);
    loggingFilter.setIncludeQueryString(false);
    loggingFilter.setIncludePayload(false);
    loggingFilter.setIncludeHeaders(true);
    return loggingFilter;
  }

}
