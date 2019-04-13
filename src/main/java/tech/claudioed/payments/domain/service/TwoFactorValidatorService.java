package tech.claudioed.payments.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.claudioed.payments.domain.service.data.CheckedAuthCode;
import tech.claudioed.payments.domain.service.data.RequestCheckAuthCode;

/** @author claudioed on 2019-04-11. Project payments */
@Slf4j
@Service
public class TwoFactorValidatorService {

  private final String paymentAuthorizationUrl;

  private final RestTemplate restTemplate;

  public TwoFactorValidatorService(
      @Value("${payment.authorization.host}") String paymentAuthorizationUrl,
      RestTemplate restTemplate) {
    log.info("Payment Authorization URL {}",paymentAuthorizationUrl);
    this.paymentAuthorizationUrl = paymentAuthorizationUrl;
    this.restTemplate = restTemplate;
  }

  public CheckedAuthCode check(RequestCheckAuthCode request) {
    log.info("Checking AuthCode {} for userId {}",request.getId(),request.getUserId());
    this.restTemplate.put(this.paymentAuthorizationUrl + "/api/authorizations" + "/{id}", request, request.getId());
    log.info("AuthCode ID {} is valid",request.getId());
    return CheckedAuthCode.builder().id(request.getId()).build();
  }
}
