package com.ironhack.midtermproject.service.implementations;

import com.ironhack.midtermproject.controller.dto.ThirdPartyDTOInput;
import com.ironhack.midtermproject.controller.dto.ThirdPartyDTOOutput;
import com.ironhack.midtermproject.model.Role;
import com.ironhack.midtermproject.model.User;
import com.ironhack.midtermproject.repository.RoleRepository;
import com.ironhack.midtermproject.repository.UserRepository;
import com.ironhack.midtermproject.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public ThirdPartyDTOOutput addThirdParty(ThirdPartyDTOInput thirdPartyDTOInput) {

        if ((thirdPartyDTOInput.getName().equals("")) || (thirdPartyDTOInput.getSharedKey().equals("")) ) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Body not able to process");
        }

        Optional<User> userDatabase = userRepository.findByUsername(thirdPartyDTOInput.getName());
        if (userDatabase.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Resource already exits");
        }

        User userToAdd = new User();
        userToAdd.setUsername(thirdPartyDTOInput.getName());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        userToAdd.setPassword(passwordEncoder.encode(thirdPartyDTOInput.getSharedKey()));
        userRepository.save(userToAdd);

        Role role = new Role("THIRD PARTY");
        role.setUser(userToAdd);
        roleRepository.save(role);

        ThirdPartyDTOOutput thirdPartyDTOOutput = new ThirdPartyDTOOutput();
        thirdPartyDTOOutput.setId(userToAdd.getId());
        thirdPartyDTOOutput.setName(userToAdd.getUsername());

        return thirdPartyDTOOutput;
    }

    @Override
    public void deleteThirdParty(Long id) {

        User databaseUser = userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Resource with Id " + id + " not found"));
        Set<Role> roleList = databaseUser.getRoles();
        for (Role role : roleList) {
            roleRepository.delete(role);
        }
        userRepository.delete(databaseUser);
    }
}
