package tech.claudioed.payments.domain.service;

import io.micrometer.core.instrument.Counter;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tech.claudioed.payments.domain.RegisteredPayment;
import tech.claudioed.payments.domain.Requester;
import tech.claudioed.payments.domain.Transaction;
import tech.claudioed.payments.domain.exception.TransactionErrorException;
import tech.claudioed.payments.domain.repository.TransactionRepository;
import tech.claudioed.payments.domain.resource.data.TransactionRequest;

/**
 * @author claudioed on 2019-03-02.
 * Project payments
 */
@Slf4j
@Service
public class TransactionService {

  private final TransactionRepository transactionRepository;

  private final CheckRequesterService checkRequesterService;

  private final RegisterPaymentService registerPaymentService;

  private final Counter transactionCounter;

  public TransactionService(TransactionRepository transactionRepository,
      CheckRequesterService checkRequesterService,
      RegisterPaymentService registerPaymentService,
      @Qualifier("transactionCounter") Counter transactionCounter) {
    this.transactionRepository = transactionRepository;
    this.checkRequesterService = checkRequesterService;
    this.registerPaymentService = registerPaymentService;
    this.transactionCounter = transactionCounter;
  }

  public Transaction processTransaction(@NonNull TransactionRequest request,String requesterId){
    log.info("Processing transaction  : {}", request);
    try{
      final Requester requester = this.checkRequesterService.requester(requesterId);
      final RegisteredPayment registeredPayment = this.registerPaymentService
          .registerPayment(request);
      final Transaction transaction = Transaction.builder().customerId(request.getCustomerId())
          .requesterId(requester.getId()).id(
              UUID.randomUUID().toString()).type(request.getType()).orderId(request.getOrderId())
          .paymentId(registeredPayment.getId()).value(request.getValue()).build();
      transactionCounter.increment();
      log.info("New transaction created ID  : {}", transaction.getId());
      return this.transactionRepository.save(transaction);
    }catch (Exception ex){
      log.error("Error on processing transaction " + request.toString(),ex);
      throw new TransactionErrorException("Invalid Transaction");
    }
  }

}
