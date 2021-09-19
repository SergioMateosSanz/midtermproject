package com.ironhack.midtermproject.service.interfaces;

import com.ironhack.midtermproject.controller.dto.CheckingDTO;

import java.util.List;

public interface CheckingService {

    CheckingDTO store(CheckingDTO checkingDTO);
    List<CheckingDTO> getAllByOwner(String name);
    CheckingDTO getChecking(int id);
}
