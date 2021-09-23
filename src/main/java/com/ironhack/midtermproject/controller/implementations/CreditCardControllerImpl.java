package com.ironhack.midtermproject.controller.implementations;

import com.ironhack.midtermproject.controller.dto.MovementDTO;
import com.ironhack.midtermproject.controller.dto.CreditCardDTO;
import com.ironhack.midtermproject.controller.interfaces.CreditCardController;
import com.ironhack.midtermproject.security.CustomUserDetails;
import com.ironhack.midtermproject.service.interfaces.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Override
    @GetMapping("/accounts/credits")
    @ResponseStatus(HttpStatus.OK)
    public List<CreditCardDTO> getAll(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return creditCardService.getAllByOwner(userDetails.getUsername());
    }

    @Override
    @GetMapping("/accounts/credits/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CreditCardDTO getCreditCard(@PathVariable(name = "id") int id, @AuthenticationPrincipal CustomUserDetails userDetails) {

        return creditCardService.getCreditCard(id, userDetails.getUsername());
    }

    @Override
    @PostMapping("/accounts/credits/{id}/movements")
    @ResponseStatus(HttpStatus.CREATED)
    public MovementDTO createMovement(@PathVariable(name = "id") int id, @RequestBody MovementDTO movementDTO,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {

        return creditCardService.createMovement(id, movementDTO, userDetails.getUsername());
    }

    @Override
    @GetMapping("/accounts/credits/{id}/movements")
    @ResponseStatus(HttpStatus.OK)
    public List<MovementDTO> getMovements(@PathVariable(name = "id") int id, @AuthenticationPrincipal CustomUserDetails userDetails) {

        return creditCardService.getMovements(id, userDetails.getUsername());
    }
}
