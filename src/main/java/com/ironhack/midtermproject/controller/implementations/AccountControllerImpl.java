package com.ironhack.midtermproject.controller.implementations;

import com.ironhack.midtermproject.controller.dto.AccountDTO;
import com.ironhack.midtermproject.controller.dto.CheckingDTO;
import com.ironhack.midtermproject.controller.interfaces.AccountController;
import com.ironhack.midtermproject.security.CustomUserDetails;
import com.ironhack.midtermproject.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class AccountControllerImpl implements AccountController {

    @Autowired
    AccountService accountService;

    @Override
    @GetMapping("/accounts")
    @ResponseStatus(HttpStatus.OK)
    public List<AccountDTO> getAllAccounts(@AuthenticationPrincipal CustomUserDetails userDetails) {

        List<Object> list = Collections.singletonList(userDetails.getAuthorities());
        String role = list.toString();

        if (role.equals("[[ROLE_ADMIN]]")) {
            return accountService.getAllAccountsByAdmin();
        } else {
            return accountService.getAllAccountsByHolder(userDetails.getUsername());
        }
    }

    @Override
    @PatchMapping("/accounts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAmount(@PathVariable(name = "id") int id, @RequestBody AccountDTO accountDTO) {

        accountService.updateAmount(id, accountDTO);
    }
}
