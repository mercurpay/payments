package tech.claudioed.payments.domain.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.claudioed.customer.grpc.CustomerFindRequest;
import tech.claudioed.customer.grpc.CustomerFindResponse;
import tech.claudioed.customer.grpc.CustomerServiceGrpc;
import tech.claudioed.customer.grpc.CustomerServiceGrpc.CustomerServiceBlockingStub;
import tech.claudioed.payments.domain.Customer;

/** @author claudioed on 2019-04-11. Project payments */
@Slf4j
@Service
public class CustomerService {

  private final String customerHost;

  private final Integer customerPort;

  private final ManagedChannel managedChannel;

  public CustomerService(
      @Value("${customer.service.host}") String customerHost,
      @Value("${customer.service.port}") Integer customerPort) {
    log.info("Customer SVC URL {}", customerHost);
    log.info("Customer SVC PORT {}", customerPort);
    this.customerHost = customerHost;
    this.customerPort = customerPort;
    this.managedChannel =
        ManagedChannelBuilder.forAddress(this.customerHost, this.customerPort)
            .usePlaintext()
            .build();
  }

  public Customer customer(String id) {
    log.info("Finding customer ID {} data", id);
    final CustomerServiceBlockingStub stub =
        CustomerServiceGrpc.newBlockingStub(this.managedChannel)
            .withDeadlineAfter(500, TimeUnit.MILLISECONDS);
    val request = CustomerFindRequest.newBuilder().setId(id).build();
    final CustomerFindResponse response = stub.findCustomer(request);
    log.info("Customer ID {} is valid ", id);
    return Customer.builder()
        .id(response.getId())
        .twoFactorEnabled(Boolean.valueOf(response.getTwoFactorEnabled()))
        .build();
  }
}
