package com.github.h2m.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


@Component
public class Transactor {

    @Autowired
    private PlatformTransactionManager transactionManager;

    public boolean perform(UnitOfWork unitOfWork) {
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        TransactionStatus transaction = transactionManager.getTransaction(definition);

        try {
            unitOfWork.work();
            transactionManager.commit(transaction);
            return true;
        } catch (Exception e) {
            transactionManager.rollback(transaction);
            e.printStackTrace();
            return false;
        }
    }
}
