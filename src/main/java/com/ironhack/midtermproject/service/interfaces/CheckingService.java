package com.ironhack.midtermproject.service.interfaces;

import com.ironhack.midtermproject.classes.MovementDTO;
import com.ironhack.midtermproject.controller.dto.CheckingDTO;

import java.util.List;

public interface CheckingService {

    CheckingDTO store(CheckingDTO checkingDTO);
    List<CheckingDTO> getAllByOwner(String name);
    CheckingDTO getChecking(int id, String name);
    MovementDTO createMovement(int id, MovementDTO movementDTO, String name);
    List<MovementDTO> getMovements(int id, String name);
}
