package com.ironhack.midtermproject.service.interfaces;

import com.ironhack.midtermproject.controller.dto.MovementDTO;
import com.ironhack.midtermproject.controller.dto.CheckingDTO;

import java.util.List;

public interface StudentService {

    CheckingDTO store(CheckingDTO checkingDTO);
    List<CheckingDTO> getAllByOwner(String name);
    CheckingDTO getStudent(int id, String name);
    MovementDTO createMovement(int id, MovementDTO movementDTO, String name);
    List<MovementDTO> getMovements(int id, String name);
}
