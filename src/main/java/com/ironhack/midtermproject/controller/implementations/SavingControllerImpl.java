package com.ironhack.midtermproject.controller.implementations;

import com.ironhack.midtermproject.controller.dto.MovementDTO;
import com.ironhack.midtermproject.controller.dto.SavingDTO;
import com.ironhack.midtermproject.controller.interfaces.SavingController;
import com.ironhack.midtermproject.security.CustomUserDetails;
import com.ironhack.midtermproject.service.interfaces.SavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Override
    @GetMapping("/accounts/savings")
    @ResponseStatus(HttpStatus.OK)
    public List<SavingDTO> getAll(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return savingService.getAllByOwner(userDetails.getUsername());
    }

    @Override
    @GetMapping("/accounts/savings/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SavingDTO getSaving(@PathVariable(name = "id") int id, @AuthenticationPrincipal CustomUserDetails userDetails) {

        return savingService.getSaving(id, userDetails.getUsername());
    }

    @Override
    @PostMapping("/accounts/savings/{id}/movements")
    @ResponseStatus(HttpStatus.CREATED)
    public MovementDTO createMovement(@PathVariable(name = "id") int id, @RequestBody MovementDTO movementDTO,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {

        return savingService.createMovement(id, movementDTO, userDetails.getUsername());
    }

    @Override
    @GetMapping("/accounts/savings/{id}/movements")
    @ResponseStatus(HttpStatus.OK)
    public List<MovementDTO> getMovements(@PathVariable(name = "id") int id, @AuthenticationPrincipal CustomUserDetails userDetails) {

        return savingService.getMovements(id, userDetails.getUsername());
    }
}
