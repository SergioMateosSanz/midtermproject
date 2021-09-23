package com.ironhack.midtermproject.service.interfaces;

import com.ironhack.midtermproject.controller.dto.MovementDTO;
import com.ironhack.midtermproject.controller.dto.SavingDTO;

import java.util.List;

public interface SavingService {

    SavingDTO store(SavingDTO savingDTO);
    List<SavingDTO> getAllByOwner(String name);
    SavingDTO getSaving(int id, String name);
    MovementDTO createMovement(int id, MovementDTO movementDTO, String name);
    List<MovementDTO> getMovements(int id, String name);
}
