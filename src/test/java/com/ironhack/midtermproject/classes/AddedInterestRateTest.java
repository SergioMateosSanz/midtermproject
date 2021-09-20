package com.ironhack.midtermproject.classes;

import com.ironhack.midtermproject.enums.MovementType;
import com.ironhack.midtermproject.model.Account;
import com.ironhack.midtermproject.model.Movement;
import com.ironhack.midtermproject.repository.AccountRepository;
import com.ironhack.midtermproject.repository.MovementRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AddedInterestRateTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    MovementRepository movementRepository;

    @Autowired
    AddedInterestRate addedInterestRate;

    Account account2000;
    Account accountToday;
    Account account364days;
    Account account30days;
    Movement movement;

    @BeforeEach
    void setUp() {

        account2000 = new Account();
        account2000.setPenaltyFee(BigDecimal.ZERO);
        account2000.setCreationDate(LocalDate.of(2000, 01, 01));
        accountRepository.save(account2000);

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setOrderDate(LocalDate.of(2000, 01, 01));
        movement.setAccount(account2000);
        movementRepository.save(movement);

        accountToday = new Account();
        accountToday.setPenaltyFee(BigDecimal.ZERO);
        accountToday.setCreationDate(LocalDate.now());
        accountRepository.save(accountToday);

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setOrderDate(LocalDate.now());
        movement.setAccount(accountToday);
        movementRepository.save(movement);

        account364days = new Account();
        account364days.setPenaltyFee(BigDecimal.ZERO);
        account364days.setCreationDate(LocalDate.now().minusDays(364));
        accountRepository.save(account364days);

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setOrderDate(LocalDate.now().minusDays(364));
        movement.setAccount(account364days);
        movementRepository.save(movement);

        account30days = new Account();
        account30days.setPenaltyFee(BigDecimal.ZERO);
        account30days.setCreationDate(LocalDate.now().minusDays(30));
        accountRepository.save(account30days);

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setOrderDate(LocalDate.now().minusDays(30));
        movement.setAccount(account30days);
        movementRepository.save(movement);
    }

    @AfterEach
    void tearDown() {
        movementRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void isInterestRateToAddSavings_ReturnTrue_CreateMoreThanAYear() {

        assertTrue(addedInterestRate.isInterestRateToAddSavings(account2000.getId()));
    }

    @Test
    void isInterestRateToAddSavings_ReturnFalse_CreateToday() {

        assertFalse(addedInterestRate.isInterestRateToAddSavings(accountToday.getId()));
    }

    @Test
    void isInterestRateToAddSavings_ReturnFalse_Create364DaysBeforeToday() {

        assertFalse(addedInterestRate.isInterestRateToAddSavings(account364days.getId()));
    }

    @Test
    void isInterestRateToAddSavings_ReturnTrue_LastInterestRateMoreThanAYear() {

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.INTEREST_RATE);
        movement.setOrderDate(LocalDate.now().minusDays(365));
        movement.setAccount(account2000);
        movementRepository.save(movement);
        assertTrue(addedInterestRate.isInterestRateToAddSavings(account2000.getId()));
    }

    @Test
    void isInterestRateToAddSavings_ReturnFalse_LastInterestRateLessThanAYear() {

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.INTEREST_RATE);
        movement.setOrderDate(LocalDate.now().minusDays(364));
        movement.setAccount(account2000);
        movementRepository.save(movement);
        assertFalse(addedInterestRate.isInterestRateToAddSavings(account2000.getId()));
    }

    @Test
    void isInterestRateToAddCreditCards_ReturnTrue_CreateMoreThanAYear() {

        assertTrue(addedInterestRate.isInterestRateToAddCreditCards(account2000.getId()));
    }

    @Test
    void isInterestRateToAddCreditCards_ReturnFalse_CreateToday() {

        assertFalse(addedInterestRate.isInterestRateToAddCreditCards(accountToday.getId()));
    }

    @Test
    void isInterestRateToAddCreditCards_ReturnFalse_Create30DaysBeforeToday() {

        assertFalse(addedInterestRate.isInterestRateToAddCreditCards(account30days.getId()));
    }

    @Test
    void isInterestRateToAddCreditCards_ReturnTrue_LastInterestRateMoreThanAMonth() {

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.INTEREST_RATE);
        movement.setOrderDate(LocalDate.now().minusDays(31));
        movement.setAccount(account2000);
        movementRepository.save(movement);
        assertTrue(addedInterestRate.isInterestRateToAddCreditCards(account2000.getId()));
    }

    @Test
    void isInterestRateToAddCreditCards_ReturnFalse_LastInterestRateLessThanAMonth() {

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.INTEREST_RATE);
        movement.setOrderDate(LocalDate.now().minusDays(28));
        movement.setAccount(account2000);
        movementRepository.save(movement);
        assertFalse(addedInterestRate.isInterestRateToAddCreditCards(account2000.getId()));
    }

    @Test
    void calculateInterestRateToSet_CorrectCalculation_AmountPositive() {

        BigDecimal result = BigDecimal.valueOf(110).setScale(1);
        assertEquals(result, addedInterestRate.calculateInterestRateToSet(BigDecimal.valueOf(100), BigDecimal.valueOf(0.1)));

        result = BigDecimal.valueOf(105).setScale(2);
        assertEquals(result, addedInterestRate.calculateInterestRateToSet(BigDecimal.valueOf(100), BigDecimal.valueOf(0.05)));
    }

    @Test
    void calculateInterestRateToSet_ReturnActualAmount_AmountZero() {

        assertEquals(BigDecimal.ZERO, addedInterestRate.calculateInterestRateToSet(BigDecimal.ZERO, BigDecimal.valueOf(0.1)));
        assertEquals(BigDecimal.ZERO, addedInterestRate.calculateInterestRateToSet(BigDecimal.ZERO, BigDecimal.valueOf(0.03)));
        assertEquals(BigDecimal.ZERO, addedInterestRate.calculateInterestRateToSet(BigDecimal.ZERO, BigDecimal.valueOf(0.0025)));
    }

    @Test
    void calculateInterestRateToSet_ReturnActualAmount_AmountNegative() {

        assertEquals(BigDecimal.valueOf(-1), addedInterestRate.calculateInterestRateToSet(BigDecimal.valueOf(-1), BigDecimal.valueOf(0.1)));
        assertEquals(BigDecimal.valueOf(-10), addedInterestRate.calculateInterestRateToSet(BigDecimal.valueOf(-10), BigDecimal.valueOf(0.03)));
        assertEquals(BigDecimal.valueOf(-333), addedInterestRate.calculateInterestRateToSet(BigDecimal.valueOf(-333), BigDecimal.valueOf(0.0025)));
    }
}