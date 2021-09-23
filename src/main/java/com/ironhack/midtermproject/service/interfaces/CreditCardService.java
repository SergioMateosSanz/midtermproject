package com.ironhack.midtermproject.service.interfaces;

import com.ironhack.midtermproject.controller.dto.MovementDTO;
import com.ironhack.midtermproject.controller.dto.CreditCardDTO;

import java.util.List;

public interface CreditCardService {

    CreditCardDTO store(CreditCardDTO creditCardDTO);
    List<CreditCardDTO> getAllByOwner(String name);
    CreditCardDTO getCreditCard(int id, String name);
    MovementDTO createMovement(int id, MovementDTO movementDTO, String name);
    List<MovementDTO> getMovements(int id, String name);
}
