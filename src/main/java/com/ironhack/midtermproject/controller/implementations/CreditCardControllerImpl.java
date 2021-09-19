package com.ironhack.midtermproject.controller.implementations;

import com.ironhack.midtermproject.controller.dto.CreditCardDTO;
import com.ironhack.midtermproject.controller.interfaces.CreditCardController;
import com.ironhack.midtermproject.service.interfaces.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CreditCardControllerImpl implements CreditCardController {

    @Autowired
    CreditCardService creditCardService;

    @Override
    @PostMapping("/accounts/credits")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCardDTO store(@RequestBody CreditCardDTO creditCardDTO) {

        return creditCardService.store(creditCardDTO);
    }
}
