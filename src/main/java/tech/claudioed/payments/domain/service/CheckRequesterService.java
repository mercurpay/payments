package tech.claudioed.payments.domain.service;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.claudioed.payments.domain.Requester;
import tech.claudioed.payments.domain.exception.InvalidRequester;

/** @author claudioed on 2019-03-02. Project payments */
@Slf4j
@Service
public class CheckRequesterService {

  private final RestTemplate restTemplate;

  private final String requesterSvcUrl;

  private final Counter requesterCounter;

  private final Tracer tracer;

  private final Timer requesterTimer;

  public CheckRequesterService(
      RestTemplate restTemplate,
      @Value("${requester.service.url}") String requesterSvcUrl,
      @Qualifier("requesterCounter") Counter requesterCounter, Tracer tracer,
      @Qualifier("requesterTimer")Timer requesterTimer) {
    log.info("REQUESTER SERVICE URL: {}", requesterSvcUrl);
    this.tracer = tracer;
    this.requesterTimer = requesterTimer;
    this.restTemplate = restTemplate;
    this.requesterSvcUrl = requesterSvcUrl;
    this.requesterCounter = requesterCounter;
  }

  public Requester requester(@NonNull String id) {
    return this.requesterTimer.record(() -> {
      final Span activeSpan = this.tracer.activeSpan();
      final Span requesterSpan = this.tracer.buildSpan("checking-requester").asChildOf(activeSpan).start()
          .setTag("requester-id", id).setBaggageItem("requester-id", id);
      try (Scope scope = tracer.scopeManager().activate(requesterSpan, false)) {
        log.info("Checking Requester ID : {}", id);
        final String path = requesterSvcUrl + "/api/requesters/{id}";
        final HttpHeaders headers = new HttpHeaders();
        headers.set("requester-id", id);
        final ResponseEntity<Requester> entity = this.restTemplate
            .exchange(path, HttpMethod.GET, new HttpEntity<>(headers),
                Requester.class, id);
        requesterCounter.increment();
        log.info("Requester ID : {} is valid", id);
        requesterSpan.finish();
        return entity.getBody();
      } catch (Exception ex) {
        log.error("Invalid Requester " + id, ex);
        throw new InvalidRequester("Invalid Requester");
      }
    });
  }
}
