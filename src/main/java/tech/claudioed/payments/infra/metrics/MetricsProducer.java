package tech.claudioed.payments.infra.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author claudioed on 2019-03-02.
 * Project register
 */
@Configuration
public class MetricsProducer {

  @Bean("requesterCounter")
  public Counter requesterCounter(PrometheusMeterRegistry registry){
    return registry.counter("transaction.requester.total", "prod","infra");
  }

  @Bean("registerCounter")
  public Counter registerCounter(PrometheusMeterRegistry registry){
    return registry.counter("transaction.register.total", "prod","infra");
  }

  @Bean("transactionCounter")
  public Counter transactionCounter(PrometheusMeterRegistry registry){
    return registry.counter("transaction.transactions.total", "prod","business");
  }

}
