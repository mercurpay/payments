package tech.claudioed.payments.domain.repository;

import org.springframework.data.repository.CrudRepository;
import tech.claudioed.payments.domain.Transaction;

/** @author claudioed on 2019-03-02. Project payments */
public interface TransactionRepository extends CrudRepository<Transaction, String> {}
