package com.ironhack.midtermproject.repository;

import com.ironhack.midtermproject.enums.MovementType;
import com.ironhack.midtermproject.model.Account;
import com.ironhack.midtermproject.model.Address;
import com.ironhack.midtermproject.model.Movement;
import com.ironhack.midtermproject.model.Owner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    OwnerRepository ownerRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    MovementRepository movementRepository;

    Owner owner;
    Account accountOne;
    Account accountThree;
    Movement movement;

    @BeforeEach
    void setUp() {
        Address address = new Address();
        address.setDirection("direction");
        address.setLocation("location");
        address.setCity("city");
        address.setCountry("country");
        address.setMailingAddress("email");
        address.setCreationDate(LocalDate.now());
        addressRepository.save(address);

        owner = new Owner();
        owner.setName("Owner");
        owner.setDateOfBirth(LocalDate.of(1980, 10, 3));
        owner.setCreationDate(LocalDate.now());
        owner.setAddress(address);
        ownerRepository.save(owner);

        Owner ownerTwo = new Owner();
        ownerTwo.setName("Owner");
        ownerTwo.setDateOfBirth(LocalDate.of(1980, 10, 3));
        ownerTwo.setCreationDate(LocalDate.now());
        ownerTwo.setAddress(address);
        ownerRepository.save(ownerTwo);

        accountOne = new Account();
        accountOne.setPrimaryOwner(owner);
        accountOne.setPenaltyFee(BigDecimal.ZERO);
        accountOne.setCreationDate(LocalDate.now());
        Account accountTwo = new Account();
        accountTwo.setPrimaryOwner(ownerTwo);
        accountTwo.setPenaltyFee(BigDecimal.ZERO);
        accountTwo.setCreationDate(LocalDate.now());
        accountThree = new Account();
        accountThree.setPrimaryOwner(owner);
        accountThree.setPenaltyFee(BigDecimal.ZERO);
        accountThree.setCreationDate(LocalDate.now());
        accountRepository.saveAll(List.of(accountOne, accountTwo, accountThree));

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setAccount(accountThree);
        movementRepository.save(movement);

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.valueOf(100));
        movement.setBalanceAfter(BigDecimal.valueOf(90));
        movement.setMovementType(MovementType.INTEREST_RATE);
        movement.setAccount(accountThree);
        movementRepository.save(movement);

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.valueOf(200));
        movement.setBalanceAfter(BigDecimal.valueOf(190));
        movement.setMovementType(MovementType.INTEREST_RATE);
        movement.setAccount(accountThree);
        movementRepository.save(movement);

    }

    @AfterEach
    void tearDown() {
        movementRepository.deleteAll();
        accountRepository.deleteAll();
        ownerRepository.deleteAll();
        addressRepository.deleteAll();
    }

    @Test
    void findAllByOwner_NoAccounts_OwnerWithoutAccounts() {

        movementRepository.deleteAll();
        accountRepository.deleteAll();
        List<Account> accountList = accountRepository.findAllByOwner(owner);

        assertEquals(0, accountList.size());
    }

    @Test
    void findAllByOwner_ReturnOnlyOwnerAccounts_OwnerWithAccounts() {

        List<Account> accountList = accountRepository.findAllByOwner(owner);

        assertEquals(2, accountList.size());
        assertEquals(owner.getId(), accountList.get(0).getPrimaryOwner().getId());
        assertEquals(accountOne.getId(), accountList.get(0).getId());
        assertEquals(owner.getId(), accountList.get(1).getPrimaryOwner().getId());
        assertEquals(accountThree.getId(), accountList.get(1).getId());
    }

    @Test
    void findByIdAndMovementType_ReturnEmptyMovementList_AccountWithoutMovements() {

        List<Account> accountList = accountRepository.findByIdAndMovementType(accountOne.getId(), MovementType.CREATED);
        assertEquals(0, accountList.size());
    }

    @Test
    void findByIdAndMovementType_ReturnMovementList_AccountWithMovements() {

        List<Account> accountList = accountRepository.findByIdAndMovementType(accountThree.getId(), MovementType.CREATED);
        assertEquals(1, accountList.size());

        accountList = accountRepository.findByIdAndMovementType(accountThree.getId(), MovementType.INTEREST_RATE);
        assertEquals(2, accountList.size());
        assertEquals(movement.getId(), accountList.get(0).getMovementList().get(0).getId());
    }
}