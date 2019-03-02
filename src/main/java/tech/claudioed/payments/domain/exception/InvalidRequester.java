package tech.claudioed.payments.domain.exception;

/**
 * @author claudioed on 2019-03-02.
 * Project payments
 */
public class InvalidRequester extends RuntimeException {

  public InvalidRequester(String message) {
    super(message);
  }

}
