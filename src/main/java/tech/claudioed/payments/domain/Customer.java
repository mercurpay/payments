package tech.claudioed.payments.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @author claudioed on 2019-04-11. Project payments */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

  private String id;

  private Boolean twoFactorEnabled;

  public Boolean twoFactorEnabled(){
    return this.twoFactorEnabled;
  }

}
