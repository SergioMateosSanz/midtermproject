package com.ironhack.midtermproject.controller.implementations;

import com.ironhack.midtermproject.controller.dto.CheckingDTO;
import com.ironhack.midtermproject.controller.interfaces.CheckingController;
import com.ironhack.midtermproject.service.interfaces.CheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckingControllerImpl implements CheckingController {

    @Autowired
    CheckingService chekingService;

    @Override
    @PostMapping("/accounts/checkings")
    @ResponseStatus(HttpStatus.CREATED)
    public CheckingDTO store(@RequestBody CheckingDTO checkingDTO) {

        return chekingService.store(checkingDTO);
    }
}
