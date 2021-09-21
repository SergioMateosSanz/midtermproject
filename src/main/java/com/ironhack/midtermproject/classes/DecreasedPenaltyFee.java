package com.ironhack.midtermproject.classes;

import com.ironhack.midtermproject.model.Checking;
import com.ironhack.midtermproject.model.Saving;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DecreasedPenaltyFee {

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

    public BigDecimal calculateBalanceAmountToSet(BigDecimal actualAmount, BigDecimal penaltyFee) {

        return actualAmount.subtract(penaltyFee);
    }
}
