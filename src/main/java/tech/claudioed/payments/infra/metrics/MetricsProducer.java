package tech.claudioed.payments.infra.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author claudioed on 2019-03-02.
 * Project register
 */
@Configuration
public class MetricsProducer {

  @Bean("requesterCounter")
  public Counter requesterCounter(PrometheusMeterRegistry registry){
    return registry.counter("transaction_requester", "type","infra");
  }

  @Bean("registerCounter")
  public Counter registerCounter(PrometheusMeterRegistry registry){
    return registry.counter("transaction_register", "type","infra");
  }

  @Bean("transactionCounter")
  public Counter transactionCounter(PrometheusMeterRegistry registry){
    return registry.counter("transaction.transactions.total", "type","business","operation","transaction");
  }

  @Bean("acquirerTimer")
  public Timer acquirerTimer(PrometheusMeterRegistry registry){
    return registry.timer("acquirer_integration", "operation","register","type","infra");
  }

  @Bean("requesterTimer")
  public Timer requesterTimer(PrometheusMeterRegistry registry){
    return registry.timer("requester_integration", "operation","check-requester","type","infra");
  }

  @Bean("paymentTimer")
  public Timer paymentTimer(PrometheusMeterRegistry registry){
    return registry.timer("payments", "operation","transaction","type","infra");
  }

  @Bean
  MeterRegistryCustomizer<MeterRegistry> registerCommonTags(Environment environment) {
    final String applicationName = environment.getProperty("spring.application.name");
    return registry -> registry.config().commonTags(
        "app_name", applicationName)
        .namingConvention(new MetricsNamingConvention(applicationName));
  }

}
