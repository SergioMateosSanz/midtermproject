package com.ironhack.midtermproject.service.implementations;

import com.ironhack.midtermproject.classes.Money;
import com.ironhack.midtermproject.controller.dto.SavingDTO;
import com.ironhack.midtermproject.enums.AccountStatus;
import com.ironhack.midtermproject.model.Account;
import com.ironhack.midtermproject.model.Address;
import com.ironhack.midtermproject.model.Owner;
import com.ironhack.midtermproject.model.Saving;
import com.ironhack.midtermproject.repository.AccountRepository;
import com.ironhack.midtermproject.repository.AddressRepository;
import com.ironhack.midtermproject.repository.OwnerRepository;
import com.ironhack.midtermproject.repository.SavingRepository;
import com.ironhack.midtermproject.service.interfaces.SavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class SavingServiceImpl implements SavingService {

    @Autowired
    SavingRepository savingRepository;

    @Autowired
    OwnerRepository ownerRepository;

    @Autowired
    AddressRepository addressRepository;

    private final BigDecimal PENALTY_FEE = BigDecimal.valueOf(40);

    @Override
    public SavingDTO store(SavingDTO savingDTO) {

        if (!validInputDTO(savingDTO)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Resource not processable");
        }

        Owner primaryOwner = new Owner();
        Owner secondaryOwner = new Owner();

/*        if (!savingDTO.getNameTwo().equals("")) {
            if (savingDTO.getDateOfBirthTwo() != null){
                if (!validOwnerTwo(savingDTO)) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not processable");
                }
                System.out.println("step 0");
                Address address = createAddress(savingDTO.getDirectionTwo(), savingDTO.getLocationTwo(),
                        savingDTO.getCityTwo(), savingDTO.getCountryTwo(), savingDTO.getMailingAddressTwo());
                addressRepository.save(address);

                secondaryOwner.setAddress(address);
                secondaryOwner.setName(savingDTO.getNameTwo());
                secondaryOwner.setDateOfBirth(savingDTO.getDateOfBirthTwo());
                secondaryOwner.setCreationDate(LocalDate.now());
                ownerRepository.save(secondaryOwner);
            } else {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not processable");
            }
        }*/

        Address address = createAddress(savingDTO.getDirection(), savingDTO.getLocation(),
                savingDTO.getCity(), savingDTO.getCountry(), savingDTO.getMailingAddress());
        addressRepository.save(address);

        primaryOwner.setAddress(address);
        primaryOwner.setName(savingDTO.getName());
        primaryOwner.setDateOfBirth(savingDTO.getDateOfBirth());
        primaryOwner.setCreationDate(LocalDate.now());
        ownerRepository.save(primaryOwner);

        Saving saving = new Saving();
        Money money = new Money(savingDTO.getAmount());
        saving.setBalance(money);
        saving.setPenaltyFee(PENALTY_FEE);
        saving.setSecretKey(savingDTO.getSecretKey());
        saving.setMinimumBalance(savingDTO.getMinimumBalance());
        saving.setInterestRate(savingDTO.getInterestRate());
        saving.setStatus(AccountStatus.ACTIVE);
        savingRepository.save(saving);

        SavingDTO returnDTO = new SavingDTO();

        returnDTO.setId(saving.getId());
        return returnDTO;
    }

    private boolean validInputDTO(SavingDTO savingDTO) {

        if ((savingDTO.getName().equals("")) || (savingDTO.getDateOfBirth() == null) || (savingDTO.getDirection().equals(""))
                || (savingDTO.getLocation().equals("")) || (savingDTO.getCity().equals("")) || (savingDTO.getCountry().equals(""))
                || (savingDTO.getMailingAddress().equals("")) || (savingDTO.getSecretKey().equals(""))) {
            return false;
        }
        return true;
    }

    private boolean validOwnerTwo(SavingDTO savingDTO) {

        if ((savingDTO.getNameTwo() == null) || (savingDTO.getDateOfBirthTwo() == null) || (savingDTO.getDirectionTwo() == null)
                || (savingDTO.getLocationTwo() == null) || (savingDTO.getCityTwo() == null) || (savingDTO.getCountryTwo() == null)
                || (savingDTO.getMailingAddressTwo() == null)) {
            return false;
        }
        return true;
    }

    private Address createAddress(String direction, String location, String city, String country, String mailingAddress) {

        Address address = new Address();
        address.setDirection(direction);
        address.setLocation(location);
        address.setCity(city);
        address.setCountry(country);
        address.setMailingAddress(mailingAddress);
        address.setCreationDate(LocalDate.now());

        return address;
    }

}
