package tech.claudioed.payments.domain.service;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.util.Collections;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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

  private final Tracer tracer;

  public RegisterPaymentService(
      RestTemplate restTemplate,
      @Value("${register.service.url}") String registerSvcUrl,
      @Qualifier("registerCounter") Counter registerCounter, Tracer tracer) {
    this.tracer = tracer;
    log.info("REGISTER SERVICE URL: {}", registerSvcUrl);
    this.restTemplate = restTemplate;
    this.registerSvcUrl = registerSvcUrl;
    this.registerCounter = registerCounter;
  }

  @Timed(value = "transaction.register.time.seconds")
  public RegisteredPayment registerPayment(@NonNull TransactionRequest request,@NonNull String requestId) {
    final Span activeSpan = this.tracer.activeSpan();
    final Span registerPaymentSpan = this.tracer.buildSpan("register-payment").asChildOf(activeSpan).start();
    try (Scope scope = tracer.scopeManager().activate(registerPaymentSpan, false)) {
      log.info("Registering transaction  : {}", request);
      final String path = registerSvcUrl + "/api/payments";
      PaymentRegisterRequest paymentRegisterRequest =
          PaymentRegisterRequest.builder()
              .customerId(request.getCustomerId())
              .requesterId(requestId)
              .value(request.getValue())
              .crmId(request.getCrmId())
              .token(request.getToken())
              .orderId(request.getOrderId())
              .build();
      log.info("Request for register payment {}",paymentRegisterRequest);
      final HttpHeaders headers = new HttpHeaders();
      headers.set("requester-id", requestId);
      final HttpEntity<PaymentRegisterRequest> dataForRequest = new HttpEntity<>(paymentRegisterRequest,
          headers);
      final ResponseEntity<RegisteredPayment> entity =
          this.restTemplate.postForEntity(path, dataForRequest, RegisteredPayment.class);
      final RegisteredPayment transaction = entity.getBody();
      registerPaymentSpan.setTag("payment-id",transaction.getId());
      registerCounter.increment();
      log.info("Transaction {} registered successfully",transaction );
      registerPaymentSpan.finish();
      return entity.getBody();
    } catch (Exception ex) {
      log.error("Error on register transaction " + request.toString(), ex);
      throw new RegisterPaymentException("Error on register transaction");
    }
  }
}
