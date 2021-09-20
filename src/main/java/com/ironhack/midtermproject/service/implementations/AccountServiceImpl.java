package com.ironhack.midtermproject.service.implementations;

import com.ironhack.midtermproject.classes.Money;
import com.ironhack.midtermproject.controller.dto.AccountDTO;
import com.ironhack.midtermproject.model.Account;
import com.ironhack.midtermproject.model.Owner;
import com.ironhack.midtermproject.repository.AccountRepository;
import com.ironhack.midtermproject.repository.OwnerRepository;
import com.ironhack.midtermproject.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    OwnerRepository ownerRepository;


    @Override
    public List<AccountDTO> getAllAccountsByAdmin() {

        List<AccountDTO> accountAdminDTOList = new ArrayList<>();
        List<Account> accountList = accountRepository.findAll();
        for (Account account : accountList) {
            accountAdminDTOList.add(new AccountDTO(account.getId(), account.getBalance().getCurrency(), account.getBalance().getAmount()));
        }
        return accountAdminDTOList;
    }

    @Override
    public List<AccountDTO> getAllAccountsByHolder(String userName) {

        List<AccountDTO> accountDTOList = new ArrayList<>();

        List<Owner> ownerList = ownerRepository.findByName(userName);

        if (!ownerList.isEmpty()) {
            List<Account> accountList = accountRepository.findAllByOwner(ownerList.get(0));
            AccountDTO accountDTO;
            for (Account account : accountList) {
                accountDTO = new AccountDTO();
                accountDTO.setId(account.getId());
                accountDTO.setCurrency(account.getBalance().getCurrency());
                accountDTO.setAmount(account.getBalance().getAmount());
                accountDTOList.add(accountDTO);
            }
        }
        return accountDTOList;
    }

    @Override
    public void updateAmount(int id, AccountDTO accountDTO) {

        if (accountDTO.getAmount() == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not able to process");
        } else {
            Optional<Account> optionalAccount = accountRepository.findById(id);

            if (optionalAccount.isPresent()) {
                optionalAccount.get().setBalance(new Money(accountDTO.getAmount()));
                optionalAccount.get().setModificationDate(LocalDate.now());
                accountRepository.save(optionalAccount.get());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
            }
        }
    }

}
