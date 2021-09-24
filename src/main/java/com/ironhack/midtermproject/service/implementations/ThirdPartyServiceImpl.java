package com.ironhack.midtermproject.service.implementations;

import com.ironhack.midtermproject.classes.DecreasedPenaltyFee;
import com.ironhack.midtermproject.classes.FraudDetection;
import com.ironhack.midtermproject.classes.Money;
import com.ironhack.midtermproject.controller.dto.TransferMoneyDTO;
import com.ironhack.midtermproject.enums.AccountStatus;
import com.ironhack.midtermproject.enums.MovementType;
import com.ironhack.midtermproject.model.*;
import com.ironhack.midtermproject.repository.AccountRepository;
import com.ironhack.midtermproject.repository.MovementRepository;
import com.ironhack.midtermproject.service.interfaces.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ThirdPartyServiceImpl implements ThirdPartyService {

    @Autowired
    MovementRepository movementRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    DecreasedPenaltyFee decreasedPenaltyFee;

    @Autowired
    FraudDetection fraudDetection;


    @Override
    public TransferMoneyDTO sendMoney(TransferMoneyDTO transferMoneyDTO) {

        if ((transferMoneyDTO.getAmount() == null) || (transferMoneyDTO.getAccountId() == 0)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable entity");
        }

        Optional<Account> optionalAccount = accountRepository.findById(transferMoneyDTO.getAccountId());

        if (!optionalAccount.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }
        if (optionalAccount.get().getClass().equals(Saving.class)) {
            Saving saving = (Saving) optionalAccount.get();
            if (saving.getStatus().equals(AccountStatus.FROZEN)) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account is frozen");
            }
        }
        if (optionalAccount.get().getClass().equals(Checking.class)) {
            Checking checking = (Checking) optionalAccount.get();
            if (checking.getStatus().equals(AccountStatus.FROZEN)) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account is frozen");
            }
        }
        if (optionalAccount.get().getClass().equals(Student.class)) {
            Student student = (Student) optionalAccount.get();
            if (student.getStatus().equals(AccountStatus.FROZEN)) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account is frozen");
            }
        }

        Movement movement = new Movement();
        movement.setTransferAmount(transferMoneyDTO.getAmount());
        movement.setBalanceBefore(optionalAccount.get().getBalance().getAmount());
        movement.setBalanceAfter(optionalAccount.get().getBalance().getAmount().add(transferMoneyDTO.getAmount()));
        movement.setMovementType(MovementType.ADDED);
        movement.setOrderDate(LocalDate.now());
        movement.setTimeExecution(LocalDateTime.now());
        movement.setModificationDate(LocalDate.of(1, 1, 1));
        movement.setAccount(optionalAccount.get());
        movementRepository.save(movement);

        optionalAccount.get().setBalance(new Money(optionalAccount.get().getBalance().getAmount().add(transferMoneyDTO.getAmount())));
        accountRepository.save(optionalAccount.get());

        transferMoneyDTO.setMovementId(movement.getId());

        if (optionalAccount.get().getClass().equals(Saving.class)) {
            Saving saving = (Saving) optionalAccount.get();

            if (fraudDetection.isFraudDetected(saving.getId())){
                movement = new Movement();
                movement.setTransferAmount(BigDecimal.ZERO);
                movement.setBalanceBefore(BigDecimal.ZERO);
                movement.setBalanceAfter(BigDecimal.ZERO);
                movement.setMovementType(MovementType.FROZEN);
                movement.setOrderDate(LocalDate.now());
                movement.setTimeExecution(LocalDateTime.now());
                movement.setModificationDate(LocalDate.of(1, 1, 1));
                movement.setAccount(saving);
                movementRepository.save(movement);

                saving.setStatus(AccountStatus.FROZEN);
                accountRepository.save(saving);
            }
        }
        if (optionalAccount.get().getClass().equals(Checking.class)) {
            Checking checking = (Checking) optionalAccount.get();

            if (fraudDetection.isFraudDetected(checking.getId())){
                movement = new Movement();
                movement.setTransferAmount(BigDecimal.ZERO);
                movement.setBalanceBefore(BigDecimal.ZERO);
                movement.setBalanceAfter(BigDecimal.ZERO);
                movement.setMovementType(MovementType.FROZEN);
                movement.setOrderDate(LocalDate.now());
                movement.setTimeExecution(LocalDateTime.now());
                movement.setModificationDate(LocalDate.of(1, 1, 1));
                movement.setAccount(checking);
                movementRepository.save(movement);

                checking.setStatus(AccountStatus.FROZEN);
                accountRepository.save(checking);
            }
        }
        if (optionalAccount.get().getClass().equals(Student.class)) {
            Student student = (Student) optionalAccount.get();

            if (fraudDetection.isFraudDetected(student.getId())){
                movement = new Movement();
                movement.setTransferAmount(BigDecimal.ZERO);
                movement.setBalanceBefore(BigDecimal.ZERO);
                movement.setBalanceAfter(BigDecimal.ZERO);
                movement.setMovementType(MovementType.FROZEN);
                movement.setOrderDate(LocalDate.now());
                movement.setTimeExecution(LocalDateTime.now());
                movement.setModificationDate(LocalDate.of(1, 1, 1));
                movement.setAccount(student);
                movementRepository.save(movement);

                student.setStatus(AccountStatus.FROZEN);
                accountRepository.save(student);
            }
        }

        return transferMoneyDTO;
    }

    @Override
    public TransferMoneyDTO receiveMoney(TransferMoneyDTO transferMoneyDTO) {

        if ((transferMoneyDTO.getAmount() == null) || (transferMoneyDTO.getAccountId() == 0)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable entity");
        }

        Optional<Account> optionalAccount = accountRepository.findById(transferMoneyDTO.getAccountId());

        if (!optionalAccount.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
        }

        Movement movement = new Movement();

        if (optionalAccount.get().getClass().equals(Saving.class)) {
            Saving saving = (Saving) optionalAccount.get();
            if (saving.getStatus().equals(AccountStatus.FROZEN)) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account is frozen");
            }

            if (decreasedPenaltyFee.isPenaltyFeeSavingAccounts(saving, transferMoneyDTO.getAmount())) {
                movement = new Movement();
                movement.setTransferAmount(transferMoneyDTO.getAmount().negate());
                movement.setBalanceBefore(saving.getBalance().getAmount());
                movement.setBalanceAfter(saving.getBalance().getAmount().subtract(transferMoneyDTO.getAmount()));
                movement.setMovementType(MovementType.DECREASED);
                movement.setOrderDate(LocalDate.now());
                movement.setTimeExecution(LocalDateTime.now());
                movement.setModificationDate(LocalDate.of(1, 1, 1));
                movement.setAccount(optionalAccount.get());
                movementRepository.save(movement);

                optionalAccount.get().setBalance(new Money(saving.getBalance().getAmount().subtract(transferMoneyDTO.getAmount())));
                accountRepository.save(saving);

                BigDecimal amountInAccount = saving.getBalance().getAmount();
                BigDecimal amountAfterMovement = amountInAccount.subtract(saving.getPenaltyFee());

                saving.setBalance(new Money(decreasedPenaltyFee.calculateBalanceAmountToSet
                        (amountInAccount, saving.getPenaltyFee())));
                Movement movementPenalty = new Movement();
                movementPenalty.setTransferAmount(saving.getPenaltyFee().negate());
                movementPenalty.setBalanceBefore(amountInAccount);
                movementPenalty.setBalanceAfter(amountAfterMovement);
                movementPenalty.setMovementType(MovementType.PENALTY_FEE);
                movementPenalty.setOrderDate(LocalDate.now());
                movementPenalty.setTimeExecution(LocalDateTime.now());
                movementPenalty.setModificationDate(LocalDate.of(1, 1, 1));
                movementPenalty.setAccount(saving);
                movementRepository.save(movementPenalty);

                optionalAccount.get().setBalance(new Money(amountAfterMovement));
                accountRepository.save(saving);
            } else {
                movement = new Movement();
                movement.setTransferAmount(transferMoneyDTO.getAmount().negate());
                movement.setBalanceBefore(saving.getBalance().getAmount());
                movement.setBalanceAfter(saving.getBalance().getAmount().subtract(transferMoneyDTO.getAmount()));
                movement.setMovementType(MovementType.DECREASED);
                movement.setOrderDate(LocalDate.now());
                movement.setTimeExecution(LocalDateTime.now());
                movement.setModificationDate(LocalDate.of(1, 1, 1));
                movement.setAccount(saving);
                movementRepository.save(movement);

                optionalAccount.get().setBalance(new Money(saving.getBalance().getAmount().subtract(transferMoneyDTO.getAmount())));
                accountRepository.save(optionalAccount.get());
            }
            if (fraudDetection.isFraudDetected(saving.getId())){
                movement = new Movement();
                movement.setTransferAmount(BigDecimal.ZERO);
                movement.setBalanceBefore(BigDecimal.ZERO);
                movement.setBalanceAfter(BigDecimal.ZERO);
                movement.setMovementType(MovementType.FROZEN);
                movement.setOrderDate(LocalDate.now());
                movement.setTimeExecution(LocalDateTime.now());
                movement.setModificationDate(LocalDate.of(1, 1, 1));
                movement.setAccount(saving);
                movementRepository.save(movement);

                saving.setStatus(AccountStatus.FROZEN);
                accountRepository.save(saving);
            }
        }

        if (optionalAccount.get().getClass().equals(Checking.class)) {
            Checking checking = (Checking) optionalAccount.get();
            if (checking.getStatus().equals(AccountStatus.FROZEN)) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account is frozen");
            }

            if (decreasedPenaltyFee.isPenaltyFeeCheckingAccounts(checking, transferMoneyDTO.getAmount())) {
                movement = new Movement();
                movement.setTransferAmount(transferMoneyDTO.getAmount().negate());
                movement.setBalanceBefore(checking.getBalance().getAmount());
                movement.setBalanceAfter(checking.getBalance().getAmount().subtract(transferMoneyDTO.getAmount()));
                movement.setMovementType(MovementType.DECREASED);
                movement.setOrderDate(LocalDate.now());
                movement.setTimeExecution(LocalDateTime.now());
                movement.setModificationDate(LocalDate.of(1, 1, 1));
                movement.setAccount(checking);
                movementRepository.save(movement);

                checking.setBalance(new Money(checking.getBalance().getAmount().subtract(transferMoneyDTO.getAmount())));
                accountRepository.save(checking);

                BigDecimal amountInAccount = checking.getBalance().getAmount();
                BigDecimal amountAfterMovement = amountInAccount.subtract(checking.getPenaltyFee());

                optionalAccount.get().setBalance(new Money(decreasedPenaltyFee.calculateBalanceAmountToSet
                        (amountInAccount, checking.getPenaltyFee())));
                Movement movementPenalty = new Movement();
                movementPenalty.setTransferAmount(checking.getPenaltyFee().negate());
                movementPenalty.setBalanceBefore(amountInAccount);
                movementPenalty.setBalanceAfter(amountAfterMovement);
                movementPenalty.setMovementType(MovementType.PENALTY_FEE);
                movementPenalty.setOrderDate(LocalDate.now());
                movementPenalty.setTimeExecution(LocalDateTime.now());
                movementPenalty.setModificationDate(LocalDate.of(1, 1, 1));
                movementPenalty.setAccount(checking);
                movementRepository.save(movementPenalty);

                checking.setBalance(new Money(amountAfterMovement));
                accountRepository.save(checking);
            } else {
                movement = new Movement();
                movement.setTransferAmount(transferMoneyDTO.getAmount().negate());
                movement.setBalanceBefore(checking.getBalance().getAmount());
                movement.setBalanceAfter(checking.getBalance().getAmount().subtract(transferMoneyDTO.getAmount()));
                movement.setMovementType(MovementType.DECREASED);
                movement.setOrderDate(LocalDate.now());
                movement.setTimeExecution(LocalDateTime.now());
                movement.setModificationDate(LocalDate.of(1, 1, 1));
                movement.setAccount(checking);
                movementRepository.save(movement);

                checking.setBalance(new Money(checking.getBalance().getAmount().subtract(transferMoneyDTO.getAmount())));
                accountRepository.save(checking);
            }

            if (fraudDetection.isFraudDetected(checking.getId())){
                movement = new Movement();
                movement.setTransferAmount(BigDecimal.ZERO);
                movement.setBalanceBefore(BigDecimal.ZERO);
                movement.setBalanceAfter(BigDecimal.ZERO);
                movement.setMovementType(MovementType.FROZEN);
                movement.setOrderDate(LocalDate.now());
                movement.setTimeExecution(LocalDateTime.now());
                movement.setModificationDate(LocalDate.of(1, 1, 1));
                movement.setAccount(checking);
                movementRepository.save(movement);

                checking.setStatus(AccountStatus.FROZEN);
                accountRepository.save(checking);
            }
        }

        if (optionalAccount.get().getClass().equals(Student.class)) {
            Student student = (Student) optionalAccount.get();
            if (student.getStatus().equals(AccountStatus.FROZEN)) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Account is frozen");
            }

            movement = new Movement();
            movement.setTransferAmount(transferMoneyDTO.getAmount().negate());
            movement.setBalanceBefore(student.getBalance().getAmount());
            movement.setBalanceAfter(student.getBalance().getAmount().subtract(transferMoneyDTO.getAmount()));
            movement.setMovementType(MovementType.DECREASED);
            movement.setOrderDate(LocalDate.now());
            movement.setTimeExecution(LocalDateTime.now());
            movement.setModificationDate(LocalDate.of(1, 1, 1));
            movement.setAccount(student);
            movementRepository.save(movement);

            student.setBalance(new Money(student.getBalance().getAmount().subtract(transferMoneyDTO.getAmount())));
            accountRepository.save(student);

            if (fraudDetection.isFraudDetected(student.getId())){
                movement = new Movement();
                movement.setTransferAmount(BigDecimal.ZERO);
                movement.setBalanceBefore(BigDecimal.ZERO);
                movement.setBalanceAfter(BigDecimal.ZERO);
                movement.setMovementType(MovementType.FROZEN);
                movement.setOrderDate(LocalDate.now());
                movement.setTimeExecution(LocalDateTime.now());
                movement.setModificationDate(LocalDate.of(1, 1, 1));
                movement.setAccount(student);
                movementRepository.save(movement);

                student.setStatus(AccountStatus.FROZEN);
                accountRepository.save(student);
            }
        }

        if (optionalAccount.get().getClass().equals(CreditCard.class)) {
            movement = new Movement();
            movement.setTransferAmount(transferMoneyDTO.getAmount().negate());
            movement.setBalanceBefore(optionalAccount.get().getBalance().getAmount());
            movement.setBalanceAfter(optionalAccount.get().getBalance().getAmount().subtract(transferMoneyDTO.getAmount()));
            movement.setMovementType(MovementType.DECREASED);
            movement.setOrderDate(LocalDate.now());
            movement.setTimeExecution(LocalDateTime.now());
            movement.setModificationDate(LocalDate.of(1, 1, 1));
            movement.setAccount(optionalAccount.get());
            movementRepository.save(movement);

            optionalAccount.get().setBalance(new Money(optionalAccount.get().getBalance().getAmount().subtract(transferMoneyDTO.getAmount())));
            accountRepository.save(optionalAccount.get());
        }

        transferMoneyDTO.setMovementId(movement.getId());

        return transferMoneyDTO;
    }
}
