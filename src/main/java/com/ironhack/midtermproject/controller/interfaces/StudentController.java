package com.ironhack.midtermproject.controller.interfaces;

import com.ironhack.midtermproject.controller.dto.CheckingDTO;
import com.ironhack.midtermproject.security.CustomUserDetails;

import java.util.List;

public interface StudentController {

    List<CheckingDTO> getAll(CustomUserDetails userDetails);
    CheckingDTO getStudent(int id, CustomUserDetails userDetails);
}
