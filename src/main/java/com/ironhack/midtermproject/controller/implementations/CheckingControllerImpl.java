package com.ironhack.midtermproject.controller.implementations;

import com.ironhack.midtermproject.controller.dto.MovementDTO;
import com.ironhack.midtermproject.controller.dto.CheckingDTO;
import com.ironhack.midtermproject.controller.interfaces.CheckingController;
import com.ironhack.midtermproject.security.CustomUserDetails;
import com.ironhack.midtermproject.service.interfaces.CheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Override
    @GetMapping("/accounts/checkings")
    @ResponseStatus(HttpStatus.OK)
    public List<CheckingDTO> getAll(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return chekingService.getAllByOwner(userDetails.getUsername());
    }

    @Override
    @GetMapping("/accounts/checkings/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CheckingDTO getChecking(@PathVariable(name = "id") int id, @AuthenticationPrincipal CustomUserDetails userDetails) {

        return chekingService.getChecking(id, userDetails.getUsername());
    }

    @Override
    @PostMapping("/accounts/checkings/{id}/movements")
    @ResponseStatus(HttpStatus.CREATED)
    public MovementDTO createMovement(@PathVariable(name = "id") int id, @RequestBody MovementDTO movementDTO,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {

        return chekingService.createMovement(id, movementDTO, userDetails.getUsername());
    }

    @Override
    @GetMapping("/accounts/checkings/{id}/movements")
    @ResponseStatus(HttpStatus.OK)
    public List<MovementDTO> getMovements(@PathVariable(name = "id") int id, @AuthenticationPrincipal CustomUserDetails userDetails) {

        return chekingService.getMovements(id, userDetails.getUsername());
    }
}
