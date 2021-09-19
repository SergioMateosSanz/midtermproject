package com.ironhack.midtermproject.controller.implementations;

import com.ironhack.midtermproject.controller.dto.CheckingDTO;
import com.ironhack.midtermproject.controller.interfaces.StudentController;
import com.ironhack.midtermproject.security.CustomUserDetails;
import com.ironhack.midtermproject.service.interfaces.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StudentControllerImpl implements StudentController {

    @Autowired
    StudentService studentService;

    @Override
    @GetMapping("/accounts/students")
    @ResponseStatus(HttpStatus.OK)
    public List<CheckingDTO> getAll(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return studentService.getAllByOwner(userDetails.getUsername());
    }

    @Override
    @GetMapping("/accounts/students/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CheckingDTO getStudent(@PathVariable(name = "id") int id, @AuthenticationPrincipal CustomUserDetails userDetails) {

        return studentService.getStudent(id, userDetails.getUsername());
    }
}
