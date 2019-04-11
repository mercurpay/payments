package tech.claudioed.payments.domain.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.claudioed.customer.grpc.CustomerFindRequest;
import tech.claudioed.customer.grpc.CustomerFindResponse;
import tech.claudioed.customer.grpc.CustomerServiceGrpc;
import tech.claudioed.customer.grpc.CustomerServiceGrpc.CustomerServiceBlockingStub;
import tech.claudioed.payments.domain.Customer;

/** @author claudioed on 2019-04-11. Project payments */
@Service
public class CustomerService {

  private final String customerHost;

  private final Integer customerPort;

  private final ManagedChannel managedChannel;

  public CustomerService(
      @Value("${customer.host}") String customerHost,
      @Value("${customer.port}") Integer customerPort) {
    this.customerHost = customerHost;
    this.customerPort = customerPort;
    this.managedChannel =
        ManagedChannelBuilder.forAddress(this.customerHost, this.customerPort).build();
  }

  public Customer customer(String id) {
    final CustomerServiceBlockingStub stub =
        CustomerServiceGrpc.newBlockingStub(this.managedChannel);
    val request = CustomerFindRequest.newBuilder().setId(id).build();
    final CustomerFindResponse response = stub.findCustomer(request);
    return Customer.builder()
        .id(response.getId())
        .twoFactorEnabled(Boolean.valueOf(response.getTwoFactorEnabled()))
        .build();
  }

}
