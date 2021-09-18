package com.ironhack.midtermproject.service.implementations;

import com.ironhack.midtermproject.classes.Money;
import com.ironhack.midtermproject.controller.dto.SavingDTO;
import com.ironhack.midtermproject.enums.AccountStatus;
import com.ironhack.midtermproject.enums.MovementType;
import com.ironhack.midtermproject.model.*;
import com.ironhack.midtermproject.repository.*;
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

    @Autowired
    MovementRepository movementRepository;

    private final BigDecimal PENALTY_FEE = BigDecimal.valueOf(40);

    @Override
    public SavingDTO store(SavingDTO savingDTO) {

        if (!validInputDTO(savingDTO)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Resource not processable");
        }

        Saving saving = new Saving();

        if (savingDTO.getNameTwo() != null) {
            if (savingDTO.getDateOfBirthTwo() != null){
                if (!validOwnerTwo(savingDTO)) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Resource not processable");
                }

                Address address = createAddress(savingDTO.getDirectionTwo(), savingDTO.getLocationTwo(),
                        savingDTO.getCityTwo(), savingDTO.getCountryTwo(), savingDTO.getMailingAddressTwo());
                addressRepository.save(address);

                Owner secondaryOwner = fillSecondaryOwnerData(savingDTO, address);
                ownerRepository.save(secondaryOwner);

                saving.setOtherOwner(secondaryOwner);
            } else {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Resource not processable");
            }
        }

        Address address = createAddress(savingDTO.getDirection(), savingDTO.getLocation(),
                savingDTO.getCity(), savingDTO.getCountry(), savingDTO.getMailingAddress());
        addressRepository.save(address);

        Owner primaryOwner = fillOwnerData(savingDTO, address);
        ownerRepository.save(primaryOwner);

        Money money = new Money(savingDTO.getAmount());
        saving.setBalance(money);
        saving.setPenaltyFee(PENALTY_FEE);
        saving.setSecretKey(savingDTO.getSecretKey());
        saving.setMinimumBalance(savingDTO.getMinimumBalance());
        saving.setInterestRate(savingDTO.getInterestRate());
        saving.setStatus(AccountStatus.ACTIVE);
        saving.setPrimaryOwner(primaryOwner);
        saving.setCreationDate(LocalDate.now());
        saving.setModificationDate(LocalDate.of(0001,01,01));
        savingRepository.save(saving);

        Movement movement = fillMovementData(savingDTO);
        movement.setAccount(saving);
        movementRepository.save(movement);

        return fillOutputInformation(saving);
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
        address.setModificationDate(LocalDate.of(0001,01,01));

        return address;
    }

    private Owner fillSecondaryOwnerData(SavingDTO savingDTO, Address address) {

        Owner secondaryOwner = new Owner();
        secondaryOwner.setAddress(address);
        secondaryOwner.setName(savingDTO.getNameTwo());
        secondaryOwner.setDateOfBirth(savingDTO.getDateOfBirthTwo());
        secondaryOwner.setCreationDate(LocalDate.now());
        secondaryOwner.setModificationDate(LocalDate.of(0001,01,01));

        return secondaryOwner;
    }

    private Owner fillOwnerData(SavingDTO savingDTO, Address address) {

        Owner primaryOwner = new Owner();
        primaryOwner.setAddress(address);
        primaryOwner.setName(savingDTO.getName());
        primaryOwner.setDateOfBirth(savingDTO.getDateOfBirth());
        primaryOwner.setCreationDate(LocalDate.now());
        primaryOwner.setModificationDate(LocalDate.of(0001,01,01));

        return primaryOwner;
    }

    private Movement fillMovementData(SavingDTO savingDTO) {

        Movement movement = new Movement();
        movement.setTransferAmount(savingDTO.getAmount());
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(savingDTO.getAmount());
        movement.setMovementType(MovementType.CREATED);
        movement.setOrderDate(LocalDate.now());
        movement.setModificationDate(LocalDate.of(0001,01,01));

        return movement;
    }

    private SavingDTO fillOutputInformation(Saving saving) {

        SavingDTO returnDTO = new SavingDTO();

        returnDTO.setId(saving.getId());
        returnDTO.setCurrency(saving.getBalance().getCurrency());
        returnDTO.setAmount(saving.getBalance().getAmount());
        returnDTO.setPenaltyFee(saving.getPenaltyFee());
        returnDTO.setSecretKey("******");
        returnDTO.setMinimumBalance(saving.getMinimumBalance());
        returnDTO.setInterestRate(saving.getInterestRate());
        returnDTO.setName(saving.getPrimaryOwner().getName());
        returnDTO.setDateOfBirth(saving.getPrimaryOwner().getDateOfBirth());
        returnDTO.setDirection(saving.getPrimaryOwner().getAddress().getDirection());
        returnDTO.setLocation(saving.getPrimaryOwner().getAddress().getLocation());
        returnDTO.setCity(saving.getPrimaryOwner().getAddress().getCity());
        returnDTO.setCountry(saving.getPrimaryOwner().getAddress().getCountry());
        returnDTO.setMailingAddress(saving.getPrimaryOwner().getAddress().getMailingAddress());

        if (saving.getOtherOwner() != null) {
            returnDTO.setNameTwo(saving.getOtherOwner().getName());
            returnDTO.setDateOfBirthTwo(saving.getOtherOwner().getDateOfBirth());
            returnDTO.setDirectionTwo(saving.getOtherOwner().getAddress().getDirection());
            returnDTO.setLocationTwo(saving.getOtherOwner().getAddress().getLocation());
            returnDTO.setCityTwo(saving.getOtherOwner().getAddress().getCity());
            returnDTO.setCountryTwo(saving.getOtherOwner().getAddress().getCountry());
            returnDTO.setMailingAddressTwo(saving.getOtherOwner().getAddress().getMailingAddress());
        }

        return returnDTO;
    }
}
