package com.ironhack.midtermproject.classes;

import com.ironhack.midtermproject.enums.MovementType;
import com.ironhack.midtermproject.model.Account;
import com.ironhack.midtermproject.model.Checking;
import com.ironhack.midtermproject.model.Movement;
import com.ironhack.midtermproject.repository.CheckingRepository;
import com.ironhack.midtermproject.repository.MovementRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class DecreasedMonthlyMaintenanceFeeTest {

    @Autowired
    CheckingRepository checkingRepository;

    @Autowired
    MovementRepository movementRepository;

    @Autowired
    DecreasedMonthlyMaintenanceFee decreasedMonthlyMaintenanceFee;

    Checking checkingToday;
    Checking checking1month;
    Movement movement;

    @BeforeEach
    void setUp() {
        checkingToday = new Checking();
        checkingToday.setPenaltyFee(BigDecimal.ZERO);
        checkingToday.setMonthlyMaintenanceFee(BigDecimal.valueOf(12));
        checkingToday.setCreationDate(LocalDate.now());
        checkingRepository.save(checkingToday);

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setOrderDate(LocalDate.now());
        movement.setAccount(checkingToday);
        movementRepository.save(movement);

        checking1month = new Checking();
        checking1month.setPenaltyFee(BigDecimal.ZERO);
        checking1month.setCreationDate(LocalDate.now().minusMonths(1));
        checkingRepository.save(checking1month);

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setOrderDate(LocalDate.now().minusMonths(1));
        movement.setAccount(checking1month);
        movementRepository.save(movement);
    }

    @AfterEach
    void tearDown() {
        movementRepository.deleteAll();
        checkingRepository.deleteAll();
    }

    @Test
    void isMonthlyMaintenanceFeeToAdd_ReturnFalse_CreateToday() {

        assertFalse(decreasedMonthlyMaintenanceFee.isMonthlyMaintenanceFeeToAdd(checkingToday.getId()));
    }

    @Test
    void isMonthlyMaintenanceFeeToAdd_ReturnTrue_CreateMoreThanAMonth() {

        assertTrue(decreasedMonthlyMaintenanceFee.isMonthlyMaintenanceFeeToAdd(checking1month.getId()));
    }

    @Test
    void isMonthlyMaintenanceFeeToAdd_ReturnTrue_LastMonthlyMaintenanceFeeMoreThanAMonth() {

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.MONTHLY_MAINTENANCE);
        movement.setOrderDate(LocalDate.now().minusMonths(1));
        movement.setAccount(checking1month);
        movementRepository.save(movement);
        assertTrue(decreasedMonthlyMaintenanceFee.isMonthlyMaintenanceFeeToAdd(checking1month.getId()));
    }

    @Test
    void calculateMonthlyMaintenanceFeeToSet_CorrectCalculation_ActualAmountPositive() {

        BigDecimal result = BigDecimal.valueOf(-11);
        assertEquals(result, decreasedMonthlyMaintenanceFee.calculateMonthlyMaintenanceFeeToSet(BigDecimal.valueOf(1), BigDecimal.valueOf(12)));

        result = BigDecimal.valueOf(100);
        assertEquals(result, decreasedMonthlyMaintenanceFee.calculateMonthlyMaintenanceFeeToSet(BigDecimal.valueOf(112), BigDecimal.valueOf(12)));

        result = BigDecimal.valueOf(443.87);
        assertEquals(result, decreasedMonthlyMaintenanceFee.calculateMonthlyMaintenanceFeeToSet(BigDecimal.valueOf(455.87), BigDecimal.valueOf(12)));
    }

    @Test
    void calculateMonthlyMaintenanceFeeToSet_CorrectCalculation_ActualAmountZero() {

        BigDecimal result = BigDecimal.valueOf(-12);
        assertEquals(result, decreasedMonthlyMaintenanceFee.calculateMonthlyMaintenanceFeeToSet(BigDecimal.ZERO, BigDecimal.valueOf(12)));

        result = BigDecimal.valueOf(-100);
        assertEquals(result, decreasedMonthlyMaintenanceFee.calculateMonthlyMaintenanceFeeToSet(BigDecimal.ZERO, BigDecimal.valueOf(100)));
    }

    @Test
    void calculateMonthlyMaintenanceFeeToSet_CorrectCalculation_ActualAmountNegative() {

        BigDecimal result = BigDecimal.valueOf(-13);
        assertEquals(result, decreasedMonthlyMaintenanceFee.calculateMonthlyMaintenanceFeeToSet(BigDecimal.valueOf(-1), BigDecimal.valueOf(12)));

        result = BigDecimal.valueOf(-45.03);
        assertEquals(result, decreasedMonthlyMaintenanceFee.calculateMonthlyMaintenanceFeeToSet(BigDecimal.valueOf(-33.03), BigDecimal.valueOf(12)));
    }
}