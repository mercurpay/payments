package tech.claudioed.payments.domain.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tech.claudioed.payments.domain.Customer;
import tech.claudioed.payments.domain.RegisteredPayment;
import tech.claudioed.payments.domain.Requester;
import tech.claudioed.payments.domain.Transaction;
import tech.claudioed.payments.domain.exception.TransactionErrorException;
import tech.claudioed.payments.domain.exception.TransactionNotFound;
import tech.claudioed.payments.domain.repository.TransactionRepository;
import tech.claudioed.payments.domain.resource.data.TransactionRequest;
import tech.claudioed.payments.domain.service.data.CheckedAuthCode;
import tech.claudioed.payments.domain.service.data.RequestCheckAuthCode;

/** @author claudioed on 2019-03-02. Project payments */
@Slf4j
@Service
public class TransactionService {

  private final TransactionRepository transactionRepository;

  private final CheckRequesterService checkRequesterService;

  private final RegisterPaymentService registerPaymentService;

  private final Counter transactionCounter;

  private final FraudService fraudService;

  private final CustomerService customerService;

  private final TwoFactorValidatorService twoFactorValidatorService;

  private final Tracer tracer;

  private final Timer transactionTimer;

  public TransactionService(
      TransactionRepository transactionRepository,
      CheckRequesterService checkRequesterService,
      RegisterPaymentService registerPaymentService,
      @Qualifier("transactionCounter") Counter transactionCounter,
      FraudService fraudService,
      CustomerService customerService,
      TwoFactorValidatorService twoFactorValidatorService, Tracer tracer,
      @Qualifier("paymentTimer") Timer transactionTimer) {
    this.transactionRepository = transactionRepository;
    this.checkRequesterService = checkRequesterService;
    this.registerPaymentService = registerPaymentService;
    this.transactionCounter = transactionCounter;
    this.fraudService = fraudService;
    this.customerService = customerService;
    this.twoFactorValidatorService = twoFactorValidatorService;
    this.tracer = tracer;
    this.transactionTimer = transactionTimer;
  }

  public Transaction processTransaction(
      @NonNull TransactionRequest request, @NonNull String requesterId) {
    return this.transactionTimer.record(() -> {
      Span transactionSpan = tracer
          .buildSpan("process-transaction")
          .start()
          .setTag("order-id", request.getOrderId())
          .setTag("customer-id", request.getCustomerId())
          .setTag("requester-id", requesterId);
      log.info("Processing transaction for order id : {} ", request.getOrderId());
      try (Scope scope = tracer.scopeManager().activate(transactionSpan, false)) {
        final Requester requester = this.checkRequesterService.requester(requesterId);
        final Customer customer = this.customerService.customer(request.getCustomerId());
        final Span activeSpan = tracer.activeSpan();
        activeSpan.setTag("two-factor-enabled",customer.getTwoFactorEnabled()).setTag("customer-id",customer.getId());
        if (customer.twoFactorEnabled()) {
          log.info("Customer {} has two factor enabled",request.getCustomerId());
          final CheckedAuthCode checkedAuthCode = this.twoFactorValidatorService.check(RequestCheckAuthCode.builder()
              .id(request.getAuthCode())
              .userId(request.getCustomerId())
              .value(request.getValue())
              .build());
          log.info("AuthCode {} is valid ", request.getAuthCode());
        }else{
          log.info("Customer {} hasn't two factor enabled",request.getCustomerId());
        }
        final RegisteredPayment registeredPayment =
            this.registerPaymentService.registerPayment(request, requesterId);
        final Transaction transaction =
            Transaction.builder()
                .customerId(request.getCustomerId())
                .requesterId(requester.getId())
                .id(UUID.randomUUID().toString())
                .type(request.getType())
                .orderId(request.getOrderId())
                .paymentId(registeredPayment.getId())
                .value(request.getValue())
                .city(request.getCity())
                .build();
        transactionCounter.increment();
        log.info("New transaction created ID  : {}", transaction.getId());
        fraudService.analyzeTransaction(transaction);
        transactionSpan.finish();
        return this.transactionRepository.save(transaction);
      } catch (Exception ex) {
        log.error("Error on processing transaction " + request.toString(), ex);
        throw new TransactionErrorException("Invalid Transaction");
      }
    });
  }

  public Transaction find(String id) {
    log.info("Finding transaction  : {}", id);
    final Optional<Transaction> transaction = this.transactionRepository.findById(id);
    if (transaction.isPresent()) {
      return transaction.get();
    }
    log.error("Transaction id {} not found ");
    throw new TransactionNotFound("Transaction not Found");
  }
}
