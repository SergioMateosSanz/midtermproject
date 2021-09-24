package com.ironhack.midtermproject.classes;

import com.ironhack.midtermproject.model.Account;
import com.ironhack.midtermproject.model.Movement;
import com.ironhack.midtermproject.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class FraudDetection {

    @Autowired
    AccountRepository accountRepository;

    public boolean isFraudDetected(int id) {

        LocalDateTime now = LocalDateTime.now();

        List<Account> accountList = accountRepository.getByIdWithMovements(id);

        if (accountList.isEmpty()) {
            return false;
        }
        byte transactionNumber = 0;

        for (Movement movement : accountList.get(0).getMovementList()) {
            if (movement.getTimeExecution().toEpochSecond(ZoneOffset.UTC) == now.toEpochSecond(ZoneOffset.UTC)) {
                transactionNumber++;
            }
        }

        if (transactionNumber > 2) {
            return true;
        } else {
            return false;
        }
    }
}
