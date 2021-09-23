package com.ironhack.midtermproject.service.implementations;

import com.ironhack.midtermproject.classes.DecreasedPenaltyFee;
import com.ironhack.midtermproject.classes.Money;
import com.ironhack.midtermproject.controller.dto.TransferMoneyDTO;
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


    @Override
    public TransferMoneyDTO sendMoney(TransferMoneyDTO transferMoneyDTO) {

        if ((transferMoneyDTO.getAmount() == null) || (transferMoneyDTO.getAccountId() == 0)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable entity");
        }

        Optional<Account> optionalAccount = accountRepository.findById(transferMoneyDTO.getAccountId());

        if (!optionalAccount.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found");
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
            if (decreasedPenaltyFee.isPenaltyFeeSavingAccounts((Saving) optionalAccount.get(), transferMoneyDTO.getAmount())) {
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

                BigDecimal amountInAccount = optionalAccount.get().getBalance().getAmount();
                BigDecimal amountAfterMovement = amountInAccount.subtract(optionalAccount.get().getPenaltyFee());

                optionalAccount.get().setBalance(new Money(decreasedPenaltyFee.calculateBalanceAmountToSet
                        (amountInAccount, optionalAccount.get().getPenaltyFee())));
                Movement movementPenalty = new Movement();
                movementPenalty.setTransferAmount(optionalAccount.get().getPenaltyFee().negate());
                movementPenalty.setBalanceBefore(amountInAccount);
                movementPenalty.setBalanceAfter(amountAfterMovement);
                movementPenalty.setMovementType(MovementType.PENALTY_FEE);
                movementPenalty.setOrderDate(LocalDate.now());
                movementPenalty.setTimeExecution(LocalDateTime.now());
                movementPenalty.setModificationDate(LocalDate.of(1, 1, 1));
                movementPenalty.setAccount(optionalAccount.get());
                movementRepository.save(movementPenalty);

                optionalAccount.get().setBalance(new Money(amountAfterMovement));
                accountRepository.save(optionalAccount.get());
            } else {
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
        }

        if (optionalAccount.get().getClass().equals(Checking.class)) {
            if (decreasedPenaltyFee.isPenaltyFeeCheckingAccounts((Checking) optionalAccount.get(), transferMoneyDTO.getAmount())) {
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

                BigDecimal amountInAccount = optionalAccount.get().getBalance().getAmount();
                BigDecimal amountAfterMovement = amountInAccount.subtract(optionalAccount.get().getPenaltyFee());

                optionalAccount.get().setBalance(new Money(decreasedPenaltyFee.calculateBalanceAmountToSet
                        (amountInAccount, optionalAccount.get().getPenaltyFee())));
                Movement movementPenalty = new Movement();
                movementPenalty.setTransferAmount(optionalAccount.get().getPenaltyFee().negate());
                movementPenalty.setBalanceBefore(amountInAccount);
                movementPenalty.setBalanceAfter(amountAfterMovement);
                movementPenalty.setMovementType(MovementType.PENALTY_FEE);
                movementPenalty.setOrderDate(LocalDate.now());
                movementPenalty.setTimeExecution(LocalDateTime.now());
                movementPenalty.setModificationDate(LocalDate.of(1, 1, 1));
                movementPenalty.setAccount(optionalAccount.get());
                movementRepository.save(movementPenalty);

                optionalAccount.get().setBalance(new Money(amountAfterMovement));
                accountRepository.save(optionalAccount.get());
            } else {
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
        }

        if (optionalAccount.get().getClass().equals(Student.class)) {
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
