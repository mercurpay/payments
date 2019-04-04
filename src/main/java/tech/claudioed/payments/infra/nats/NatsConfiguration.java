package tech.claudioed.payments.infra.nats;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import java.io.IOException;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class NatsConfiguration {

  @Bean
  public Connection createConnection(
      @Value("${nats.host}") String host,
      @Value("${nats.username}") String username,
      @Value("${nats.password}") String password
  ) throws IOException, InterruptedException {
    log.info("Configuring NATS Connection, Host[{}]", host);
    return Nats.connect(new Options.Builder()
        .connectionTimeout(Duration.ofSeconds(2))
        .pingInterval(Duration.ofSeconds(10))
        .reconnectWait(Duration.ofSeconds(1))
        .userInfo(username,password)
        .maxReconnects(-1)
        .reconnectBufferSize(-1)
        .connectionName(System.getenv("HOSTNAME"))
        .server(host)
        .build());
  }

}