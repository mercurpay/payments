package tech.claudioed.payments.domain.service;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.claudioed.payments.domain.Requester;
import tech.claudioed.payments.domain.exception.InvalidRequester;

/**
 * @author claudioed on 2019-03-02.
 * Project payments
 */
@Service
public class CheckRequesterService {

  private final RestTemplate restTemplate;

  private final String requesterSvcUrl;

  private final Counter requesterCounter;

  public CheckRequesterService(RestTemplate restTemplate,
      @Value("${requester.service.url}") String requesterSvcUrl,
      @Qualifier("requesterCounter") Counter requesterCounter) {
    this.restTemplate = restTemplate;
    this.requesterSvcUrl = requesterSvcUrl;
    this.requesterCounter = requesterCounter;
  }

  @Timed(value = "transaction.requester.time.seconds")
  public Requester requester(@NonNull String id){
    final String path = requesterSvcUrl + "/api/requesters/{id}";
    try{
      final ResponseEntity<Requester> entity = this.restTemplate
          .getForEntity(path, Requester.class, id);
      requesterCounter.increment();
      return entity.getBody();
    }catch (Exception ex){
      throw new InvalidRequester("Invalid Requester");
    }
  }

}
