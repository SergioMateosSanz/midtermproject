package com.ironhack.midtermproject.controller.implementations;

import com.ironhack.midtermproject.controller.dto.SavingDTO;
import com.ironhack.midtermproject.controller.interfaces.SavingController;
import com.ironhack.midtermproject.service.interfaces.SavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SavingControllerImpl implements SavingController {

    @Autowired
    SavingService savingService;

    @Override
    @PostMapping("/accounts/savings")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingDTO store(@RequestBody SavingDTO savingDTO) {

        return savingService.store(savingDTO);
    }
}
