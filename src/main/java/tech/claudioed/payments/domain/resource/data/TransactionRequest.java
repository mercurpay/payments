package tech.claudioed.payments.domain.resource.data;

import java.math.BigDecimal;
import lombok.Data;

/**
 * @author claudioed on 2019-03-02.
 * Project payments
 */
@Data
public class TransactionRequest {

  private BigDecimal value;

  private String customerId;

  private String orderId;

  private String type;

  private String crmUrl;

  private String city;

  private String authCode;

}
