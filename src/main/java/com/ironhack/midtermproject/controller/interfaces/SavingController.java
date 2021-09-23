package com.ironhack.midtermproject.controller.interfaces;

import com.ironhack.midtermproject.controller.dto.MovementDTO;
import com.ironhack.midtermproject.controller.dto.SavingDTO;
import com.ironhack.midtermproject.security.CustomUserDetails;

import java.util.List;

public interface SavingController {

    SavingDTO store(SavingDTO savingDTO);
    List<SavingDTO> getAll(CustomUserDetails userDetails);
    SavingDTO getSaving(int id, CustomUserDetails userDetails);
    MovementDTO createMovement(int id, MovementDTO movementDTO, CustomUserDetails userDetails);
    List<MovementDTO> getMovements(int id, CustomUserDetails userDetails);
}
