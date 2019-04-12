package tech.claudioed.payments.domain.service.data;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author claudioed on 2019-04-06.
 * Project payments
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestCheckAuthCode {

  private String id;

  private String userId;

  private BigDecimal value;

}
