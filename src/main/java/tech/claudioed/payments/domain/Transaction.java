package tech.claudioed.payments.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author claudioed on 2019-03-02.
 * Project payments
 */
@Document(collection = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

  @Id
  private String id;

  private BigDecimal value;

  private String requesterId;

  private String customerId;

  private String type;

  private String paymentId;

  private String orderId;

}
