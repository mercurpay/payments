package tech.claudioed.payments.domain.exception;

/**
 * @author claudioed on 2019-03-05.
 * Project payments
 */
public class TransactionNotFound extends RuntimeException {

  public TransactionNotFound(String message) {
    super(message);
  }

}
