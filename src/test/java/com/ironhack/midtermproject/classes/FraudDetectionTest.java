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
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FraudDetectionTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    MovementRepository movementRepository;

    @Autowired
    FraudDetection fraudDetection;

    Account account;
    Movement movement;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setBalance(new Money(BigDecimal.valueOf(5000)));
        account.setPenaltyFee(BigDecimal.ZERO);
        account.setCreationDate(LocalDate.now());
        account.setModificationDate(LocalDate.of(1, 1, 1));
        accountRepository.save(account);

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setOrderDate(LocalDate.now());
        movement.setTimeExecution(LocalDateTime.now());
        movement.setAccount(account);
        movementRepository.save(movement);
    }

    @AfterEach
    void tearDown() {
        movementRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void isFraudDetected_ReturnFalse_AccountNotExist() {

        assertFalse(fraudDetection.isFraudDetected(0));
    }

    @Test
    void isFraudDetected_ReturnFalse_AccountWithoutMovements() {

        movementRepository.deleteAll();
        assertFalse(fraudDetection.isFraudDetected(account.getId()));
    }

    @Test
    void isFraudDetected_ReturnFalse_AccountWithCreatedMovementOnly() {

        assertFalse(fraudDetection.isFraudDetected(account.getId()));
    }

    @Test
    void isFraudDetected_ReturnFalse_AccountWithTwoMovementsAtSameSecond() {

        Movement movement2 = new Movement();
        movement2.setTransferAmount(BigDecimal.TEN);
        movement2.setBalanceBefore(BigDecimal.TEN);
        movement2.setBalanceAfter(BigDecimal.valueOf(20));
        movement2.setMovementType(MovementType.ADDED);
        movement2.setOrderDate(LocalDate.now());
        movement2.setTimeExecution(movement.getTimeExecution());
        movement2.setAccount(account);
        movementRepository.save(movement2);

        assertFalse(fraudDetection.isFraudDetected(account.getId()));
    }

    @Test
    void isFraudDetected_ReturnTrue_AccountWithMoreThanTwoMovementsAtSameSecond() {

        Movement movement2 = new Movement();
        movement2.setTransferAmount(BigDecimal.TEN);
        movement2.setBalanceBefore(BigDecimal.TEN);
        movement2.setBalanceAfter(BigDecimal.valueOf(20));
        movement2.setMovementType(MovementType.ADDED);
        movement2.setOrderDate(LocalDate.now());
        movement2.setTimeExecution(movement.getTimeExecution());
        movement2.setAccount(account);
        movementRepository.save(movement2);

        Movement movement3 = new Movement();
        movement3.setTransferAmount(BigDecimal.TEN);
        movement3.setBalanceBefore(BigDecimal.valueOf(20));
        movement3.setBalanceAfter(BigDecimal.valueOf(30));
        movement3.setMovementType(MovementType.ADDED);
        movement3.setOrderDate(LocalDate.now());
        movement3.setTimeExecution(movement.getTimeExecution());
        movement3.setAccount(account);
        movementRepository.save(movement3);

        assertTrue(fraudDetection.isFraudDetected(account.getId()));
    }
}