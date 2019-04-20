package tech.claudioed.payments.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nats.client.Connection;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.claudioed.payments.domain.Transaction;

@Service
@Slf4j
public class FraudService {

  private final Connection connection;

  private final ObjectMapper objectMapper;

  private final Tracer tracer;

  public FraudService(Connection connection, ObjectMapper objectMapper,
      Tracer tracer) {
    this.connection = connection;
    this.objectMapper = objectMapper;
    this.tracer = tracer;
  }

  public void analyzeTransaction(@NonNull Transaction transaction) throws JsonProcessingException {
    final Span activeSpan = this.tracer.activeSpan();
    final Span fraudSpan = this.tracer.buildSpan("finding-customer-data").asChildOf(activeSpan).start()
        .setTag("transaction-id", transaction.getId());
    try (Scope scope = tracer.scopeManager().activate(fraudSpan, false)) {
      log.info("Received {} to be analyzed", transaction);
      connection.publish("fraud-analyze-topic", objectMapper.writeValueAsBytes(transaction));
    }
  }

}
