package com.ironhack.midtermproject.service.interfaces;

import com.ironhack.midtermproject.controller.dto.AccountDTO;

import java.util.List;

public interface AccountService {

    List<AccountDTO> getAllAccountsByAdmin();
    List<AccountDTO> getAllAccountsByHolder(String userName);
    void updateAmount(int id, AccountDTO accountDTO);
}
