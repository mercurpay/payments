package tech.claudioed.payments.domain.service;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.claudioed.payments.domain.PaymentRegisterRequest;
import tech.claudioed.payments.domain.RegisteredPayment;
import tech.claudioed.payments.domain.exception.RegisterPaymentException;
import tech.claudioed.payments.domain.resource.data.TransactionRequest;

/** @author claudioed on 2019-03-02. Project payments */
@Slf4j
@Service
public class RegisterPaymentService {

  private final RestTemplate restTemplate;

  private final String registerSvcUrl;

  private final Counter registerCounter;

  public RegisterPaymentService(
      RestTemplate restTemplate,
      @Value("${register.service.url}") String registerSvcUrl,
      @Qualifier("registerCounter") Counter registerCounter) {
    log.info("REGISTER SERVICE URL: {}", registerSvcUrl);
    this.restTemplate = restTemplate;
    this.registerSvcUrl = registerSvcUrl;
    this.registerCounter = registerCounter;
  }

  @Timed(value = "transaction.register.time.seconds")
  public RegisteredPayment registerPayment(@NonNull TransactionRequest request,@NonNull String requestId) {
    log.info("Registering transaction  : {}", request);
    final String path = registerSvcUrl + "/api/payments";
    try {
      PaymentRegisterRequest paymentRegisterRequest =
          PaymentRegisterRequest.builder()
              .customerId(request.getCustomerId())
              .requesterId(requestId)
              .value(request.getValue())
              .build();
      final ResponseEntity<RegisteredPayment> entity =
          this.restTemplate.postForEntity(path, paymentRegisterRequest, RegisteredPayment.class);
      registerCounter.increment();
      log.info("Transaction {} register successfully", request);
      return entity.getBody();
    } catch (Exception ex) {
      log.error("Error on register transaction " + request.toString(), ex);
      throw new RegisterPaymentException("Error on register transaction");
    }
  }
}
