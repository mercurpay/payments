package tech.claudioed.payments.domain.service.data;

import java.math.BigDecimal;
import lombok.Data;

/**
 * @author claudioed on 2019-04-06.
 * Project payments
 */
@Data
public class RequestCheckAuthCode {

  private String id;

  private String userId;

  private BigDecimal value;

}
