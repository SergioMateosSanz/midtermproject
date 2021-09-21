package com.ironhack.midtermproject.classes;

import com.ironhack.midtermproject.model.Checking;
import com.ironhack.midtermproject.model.Saving;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DecreasedPenaltyFee {


    public void updateAmountSaving(Saving saving, BigDecimal penaltyFee) {

        BigDecimal result = new BigDecimal(String.valueOf(saving.getBalance().getAmount()));
        result = result.subtract(penaltyFee);

        Money money = new Money(result);
        saving.setBalance(money);
    }

    public boolean isPenaltyFeeSavingAccounts(Saving saving, BigDecimal amount) {

        boolean penalty = false;

        BigDecimal result = new BigDecimal(String.valueOf(saving.getBalance().getAmount()));
        BigDecimal minimumBalance = saving.getMinimumBalance();
        result = result.subtract(amount);

        switch (result.compareTo(minimumBalance)) {
            case -1:
                penalty = true;
                break;
            case 0:
            case 1:
                break;
        }
        return penalty;
    }

    public void updateAmountChecking(Checking checking, BigDecimal penaltyFee) {

        BigDecimal result = new BigDecimal(String.valueOf(checking.getBalance().getAmount()));
        result = result.subtract(penaltyFee);

        Money money = new Money(result);
        checking.setBalance(money);
    }

    public boolean isPenaltyFeeCheckingAccounts(Checking checking, BigDecimal amount) {

        boolean penalty = false;

        BigDecimal result = new BigDecimal(String.valueOf(checking.getBalance().getAmount()));
        BigDecimal minimumBalance = checking.getMinimumBalance();
        result = result.subtract(amount);

        switch (result.compareTo(minimumBalance)) {
            case -1:
                penalty = true;
                break;
            case 0:
            case 1:
                break;
        }
        return penalty;
    }
}
