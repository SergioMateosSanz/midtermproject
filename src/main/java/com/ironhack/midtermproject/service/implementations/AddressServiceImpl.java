package com.ironhack.midtermproject.service.implementations;

import com.ironhack.midtermproject.controller.dto.AddressDTO;
import com.ironhack.midtermproject.model.Address;
import com.ironhack.midtermproject.repository.AddressRepository;
import com.ironhack.midtermproject.service.interfaces.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    AddressRepository addressRepository;

    @Override
    public void update(int id, AddressDTO addressDTO) {

        Address databaseAddress = addressRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Resource with Id " + id + " not found"));

        if (addressDTO.getDirection() != null) {
            databaseAddress.setDirection(addressDTO.getDirection());
        }
        if (addressDTO.getLocation() != null) {
            databaseAddress.setLocation(addressDTO.getLocation());
        }
        if (addressDTO.getCity() != null) {
            databaseAddress.setCity(addressDTO.getCity());
        }
        if (addressDTO.getCountry() != null) {
            databaseAddress.setCountry(addressDTO.getCountry());
        }
        if (addressDTO.getMailingAddress() != null) {
            databaseAddress.setMailingAddress(addressDTO.getMailingAddress());
        }
        databaseAddress.setModificationDate(LocalDate.now());
        addressRepository.save(databaseAddress);
    }
}
