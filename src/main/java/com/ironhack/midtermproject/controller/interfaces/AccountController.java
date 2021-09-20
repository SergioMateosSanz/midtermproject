package com.ironhack.midtermproject.controller.interfaces;

import com.ironhack.midtermproject.controller.dto.AccountDTO;
import com.ironhack.midtermproject.security.CustomUserDetails;

import java.util.List;

public interface AccountController {

    List<AccountDTO> getAllAccounts(CustomUserDetails userDetails);
    void updateAmount(int id, AccountDTO accountDTO);
}
