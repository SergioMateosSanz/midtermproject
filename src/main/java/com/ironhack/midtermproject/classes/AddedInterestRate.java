package com.ironhack.midtermproject.classes;

import com.ironhack.midtermproject.enums.MovementType;
import com.ironhack.midtermproject.model.Account;
import com.ironhack.midtermproject.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class AddedInterestRate {

    @Autowired
    AccountRepository accountRepository;

    public boolean isInterestRateToAddSavings(int id) {

        LocalDate now = LocalDate.now();

        List<Account> accountList = accountRepository.findByIdAndMovementType(id, MovementType.INTEREST_RATE);

        if (accountList.isEmpty()) {
            accountList = accountRepository.findByIdAndMovementType(id, MovementType.CREATED);
        }

        return Period.between(accountList.get(0).getMovementList().get(0).getOrderDate(), now).getYears() != 0;
    }

    public boolean isInterestRateToAddCreditCards(int id) {

        LocalDate now = LocalDate.now();

        List<Account> accountList = accountRepository.findByIdAndMovementType(id, MovementType.INTEREST_RATE);

        if (accountList.isEmpty()) {
            accountList = accountRepository.findByIdAndMovementType(id, MovementType.CREATED);
        }

        return Period.between(accountList.get(0).getMovementList().get(0).getOrderDate(), now).getMonths() != 0;
    }

    public BigDecimal calculateInterestRateToSet(BigDecimal actualAmount, BigDecimal interestRate) {

        BigDecimal amountToReturn = BigDecimal.ZERO;
        switch (actualAmount.compareTo(BigDecimal.ZERO)){
            case -1:
            case 0:
                amountToReturn = actualAmount;
                break;
            case 1:
                BigDecimal amountToAdd = interestRate.multiply(actualAmount);
                amountToReturn = actualAmount.add(amountToAdd);
                break;
        }
        return amountToReturn;
    }
}
