package com.ironhack.midtermproject.controller.implementations;

import com.ironhack.midtermproject.controller.dto.ThirdPartyDTOInput;
import com.ironhack.midtermproject.controller.dto.ThirdPartyDTOOutput;
import com.ironhack.midtermproject.controller.interfaces.UserController;
import com.ironhack.midtermproject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserControllerImpl implements UserController {

    @Autowired
    UserService userService;

    @Override
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public ThirdPartyDTOOutput addThirdParty(@RequestBody ThirdPartyDTOInput thirdPartyDTOInput) {

        return userService.addThirdParty(thirdPartyDTOInput);
    }

    @Override
    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteThirdParty(@PathVariable(name = "id") Long id) {

        userService.deleteThirdParty(id);
    }
}
