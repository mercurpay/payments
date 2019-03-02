package tech.claudioed.payments.domain.exception;

/**
 * @author claudioed on 2019-03-02.
 * Project payments
 */
public class TransactionErrorException extends RuntimeException {

  public TransactionErrorException(String message) {
    super(message);
  }

}
