package tech.claudioed.payments.domain.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.claudioed.payments.domain.service.data.CheckedAuthCode;
import tech.claudioed.payments.domain.service.data.RequestCheckAuthCode;

/** @author claudioed on 2019-04-11. Project payments */
@Service
public class TwoFactorValidatorService {

  private final String paymentAuthorizationUrl;

  private final RestTemplate restTemplate;

  public TwoFactorValidatorService(
      @Value("${payment.authorization.host}") String paymentAuthorizationUrl,
      RestTemplate restTemplate) {
    this.paymentAuthorizationUrl = paymentAuthorizationUrl;
    this.restTemplate = restTemplate;
  }

  public CheckedAuthCode check(RequestCheckAuthCode request) {
    this.restTemplate.put(this.paymentAuthorizationUrl + "/{id}", request, request.getId());
    return CheckedAuthCode.builder().id(request.getId()).build();
  }
}
