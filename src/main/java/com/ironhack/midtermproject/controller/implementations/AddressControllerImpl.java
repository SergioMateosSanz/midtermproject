package com.ironhack.midtermproject.controller.implementations;

import com.ironhack.midtermproject.controller.dto.AddressDTO;
import com.ironhack.midtermproject.controller.interfaces.AddressController;
import com.ironhack.midtermproject.service.interfaces.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class AddressControllerImpl implements AddressController {

    @Autowired
    AddressService addressService;

    @Override
    @PatchMapping("/addresses/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable(name = "id") int id, @RequestBody AddressDTO addressDTO) {

        addressService.update(id, addressDTO);
    }
}
