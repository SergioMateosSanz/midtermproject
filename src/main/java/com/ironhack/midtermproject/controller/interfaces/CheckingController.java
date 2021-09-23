package com.ironhack.midtermproject.controller.interfaces;

import com.ironhack.midtermproject.controller.dto.MovementDTO;
import com.ironhack.midtermproject.controller.dto.CheckingDTO;
import com.ironhack.midtermproject.security.CustomUserDetails;

import java.util.List;

public interface CheckingController {

    CheckingDTO store(CheckingDTO checkingDTO);
    List<CheckingDTO> getAll(CustomUserDetails userDetails);
    CheckingDTO getChecking(int id, CustomUserDetails userDetails);
    MovementDTO createMovement(int id, MovementDTO movementDTO, CustomUserDetails userDetails);
    List<MovementDTO> getMovements(int id, CustomUserDetails userDetails);
}
