package tech.claudioed.payments.infra.nats;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import java.io.IOException;
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
    Options options = new Options.Builder()
        .server("nats://" + host + ":4222")
        .userInfo(username, password)
        .build();

    Connection connection = Nats.connect(options);
    log.info("Configured NATS Connection, Status=[{}]", connection.getStatus());
    return connection;
  }

}