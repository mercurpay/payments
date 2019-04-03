package tech.claudioed.payments.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.claudioed.payments.domain.Transaction;

@Service
@Slf4j
public class FraudService {

  private final Connection connection;
  private final ObjectMapper objectMapper;

  public FraudService(Connection connection, ObjectMapper objectMapper) {
    this.connection = connection;
    this.objectMapper = objectMapper;
  }

  public void analyzeTransaction(@NonNull Transaction transaction) throws JsonProcessingException {
    log.info("Received {} to be analyzed", transaction);
    connection.publish("fraud-analyze-topic", objectMapper.writeValueAsBytes(transaction));
  }

}
