package com.ironhack.midtermproject.controller.interfaces;

import com.ironhack.midtermproject.controller.dto.MovementDTO;
import com.ironhack.midtermproject.controller.dto.CreditCardDTO;
import com.ironhack.midtermproject.security.CustomUserDetails;

import java.util.List;

public interface CreditCardController {

    CreditCardDTO store(CreditCardDTO creditCardDTO);
    List<CreditCardDTO> getAll(CustomUserDetails userDetails);
    CreditCardDTO getCreditCard(int id, CustomUserDetails userDetails);
    MovementDTO createMovement(int id, MovementDTO movementDTO, CustomUserDetails userDetails);
    List<MovementDTO> getMovements(int id, CustomUserDetails userDetails);
}
