package com.ironhack.midtermproject.service.implementations;

import com.ironhack.midtermproject.classes.Money;
import com.ironhack.midtermproject.controller.dto.CreditCardDTO;
import com.ironhack.midtermproject.controller.dto.SavingDTO;
import com.ironhack.midtermproject.enums.MovementType;
import com.ironhack.midtermproject.model.*;
import com.ironhack.midtermproject.repository.*;
import com.ironhack.midtermproject.service.interfaces.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreditCardServiceImpl implements CreditCardService {

    @Autowired
    CreditCardRepository creditCardRepository;

    @Autowired
    OwnerRepository ownerRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    MovementRepository movementRepository;

    private final BigDecimal PENALTY_FEE = BigDecimal.valueOf(40);

    @Override
    public CreditCardDTO store(CreditCardDTO creditCardDTO) {

        if (!validInputDTO(creditCardDTO)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Resource not processable");
        }

        CreditCard creditCard = new CreditCard();

        if (creditCardDTO.getNameTwo() != null) {
            if (creditCardDTO.getDateOfBirthTwo() != null){
                if (!validOwnerTwo(creditCardDTO)) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Resource not processable");
                }

                Address address;
                List<Address> addressList = addressRepository.findByMailingAddress(creditCardDTO.getMailingAddressTwo());
                if (!addressList.isEmpty()) {
                    address = addressList.get(0);
                } else {
                    address = createAddress(creditCardDTO.getDirectionTwo(), creditCardDTO.getLocationTwo(),
                            creditCardDTO.getCityTwo(), creditCardDTO.getCountryTwo(), creditCardDTO.getMailingAddressTwo());
                    addressRepository.save(address);
                }

                Owner ownerDatabase = lookOwnerPreviouslyRegister(creditCardDTO.getNameTwo(), creditCardDTO.getDateOfBirthTwo());
                Owner secondaryOwner;
                if (ownerDatabase != null) {
                    secondaryOwner = ownerDatabase;
                } else {
                    secondaryOwner = fillSecondaryOwnerData(creditCardDTO, address);
                    ownerRepository.save(secondaryOwner);
                }

                creditCard.setOtherOwner(secondaryOwner);
            } else {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Resource not processable");
            }
        }

        Address address;
        List<Address> addressList = addressRepository.findByMailingAddress(creditCardDTO.getMailingAddress());
        if (!addressList.isEmpty()) {
            address = addressList.get(0);
        } else {
            address = createAddress(creditCardDTO.getDirection(), creditCardDTO.getLocation(),
                    creditCardDTO.getCity(), creditCardDTO.getCountry(), creditCardDTO.getMailingAddress());
            addressRepository.save(address);
        }

        Owner ownerDatabase = lookOwnerPreviouslyRegister(creditCardDTO.getName(), creditCardDTO.getDateOfBirth());
        Owner primaryOwner;
        if (ownerDatabase != null) {
            primaryOwner = ownerDatabase;
        } else {
            primaryOwner = fillOwnerData(creditCardDTO, address);
            ownerRepository.save(primaryOwner);
        }

        Money money = new Money(creditCardDTO.getAmount());
        creditCard.setBalance(money);
        creditCard.setPenaltyFee(PENALTY_FEE);
        creditCard.setCreditLimit(creditCardDTO.getCreditLimit());
        creditCard.setInterestRate(creditCardDTO.getInterestRate());
        creditCard.setPrimaryOwner(primaryOwner);
        creditCard.setCreationDate(LocalDate.now());
        creditCard.setModificationDate(LocalDate.of(1,1,1));
        creditCardRepository.save(creditCard);

        Movement movement = fillMovementData(creditCardDTO);
        movement.setAccount(creditCard);
        movementRepository.save(movement);

        return fillOutputInformation(creditCard);
    }

    @Override
    public List<CreditCardDTO> getAllByOwner(String name) {

        List<Owner> ownerList = ownerRepository.findByName(name);

        if (!ownerList.isEmpty()) {
            List<CreditCard> creditCardList = creditCardRepository.getAllByOwner(ownerList.get(0));
            List<CreditCardDTO> returnList = new ArrayList<>();

            for (CreditCard creditCard : creditCardList) {
                returnList.add(fillOutputInformation(creditCard));
            }

            return returnList;
        } else {
            return null;
        }
    }

    private boolean validInputDTO(CreditCardDTO creditCardDTO) {

        if ((creditCardDTO.getName().equals("")) || (creditCardDTO.getDateOfBirth() == null) || (creditCardDTO.getDirection().equals(""))
                || (creditCardDTO.getLocation().equals("")) || (creditCardDTO.getCity().equals("")) || (creditCardDTO.getCountry().equals(""))
                || (creditCardDTO.getMailingAddress().equals("")) || (creditCardDTO.getCreditLimit() == null)) {
            return false;
        }
        return true;
    }

    private boolean validOwnerTwo(CreditCardDTO creditCardDTO) {

        if ((creditCardDTO.getNameTwo().equals("")) || (creditCardDTO.getDateOfBirthTwo() == null) || (creditCardDTO.getDirectionTwo().equals(""))
                || (creditCardDTO.getLocationTwo().equals("")) || (creditCardDTO.getCityTwo().equals("")) || (creditCardDTO.getCountryTwo().equals(""))
                || (creditCardDTO.getMailingAddressTwo().equals(""))) {
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
        address.setModificationDate(LocalDate.of(1,1,1));

        return address;
    }

    private Owner lookOwnerPreviouslyRegister(String name, LocalDate dateOfBirth) {

        Owner owner = new Owner();
        boolean found = false;
        List<Owner> ownerList = ownerRepository.findByName(name);
        for (Owner owner1 : ownerList) {
            if (dateOfBirth.equals(owner1.getDateOfBirth())) {
                owner = owner1;
                found = true;
            }
        }
        if (found) {
            return owner;
        } else {
            return null;
        }
    }

    private Owner fillSecondaryOwnerData(CreditCardDTO creditCardDTO, Address address) {

        Owner secondaryOwner = new Owner();
        secondaryOwner.setAddress(address);
        secondaryOwner.setName(creditCardDTO.getNameTwo());
        secondaryOwner.setDateOfBirth(creditCardDTO.getDateOfBirthTwo());
        secondaryOwner.setCreationDate(LocalDate.now());
        secondaryOwner.setModificationDate(LocalDate.of(1,1,1));

        return secondaryOwner;
    }

    private Owner fillOwnerData(CreditCardDTO creditCardDTO, Address address) {

        Owner primaryOwner = new Owner();
        primaryOwner.setAddress(address);
        primaryOwner.setName(creditCardDTO.getName());
        primaryOwner.setDateOfBirth(creditCardDTO.getDateOfBirth());
        primaryOwner.setCreationDate(LocalDate.now());
        primaryOwner.setModificationDate(LocalDate.of(1,1,1));

        return primaryOwner;
    }

    private Movement fillMovementData(CreditCardDTO creditCardDTO) {

        Movement movement = new Movement();
        movement.setTransferAmount(creditCardDTO.getAmount());
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(creditCardDTO.getAmount());
        movement.setMovementType(MovementType.CREATED);
        movement.setOrderDate(LocalDate.now());
        movement.setTimeExecution(LocalDateTime.now());
        movement.setModificationDate(LocalDate.of(1,1,1));

        return movement;
    }

    private CreditCardDTO fillOutputInformation(CreditCard creditCard) {

        CreditCardDTO returnDTO = new CreditCardDTO();

        returnDTO.setId(creditCard.getId());
        returnDTO.setCurrency(creditCard.getBalance().getCurrency());
        returnDTO.setAmount(creditCard.getBalance().getAmount());
        returnDTO.setPenaltyFee(creditCard.getPenaltyFee());
        returnDTO.setCreditLimit(creditCard.getCreditLimit());
        returnDTO.setInterestRate(creditCard.getInterestRate());
        returnDTO.setName(creditCard.getPrimaryOwner().getName());
        returnDTO.setDateOfBirth(creditCard.getPrimaryOwner().getDateOfBirth());
        returnDTO.setDirection(creditCard.getPrimaryOwner().getAddress().getDirection());
        returnDTO.setLocation(creditCard.getPrimaryOwner().getAddress().getLocation());
        returnDTO.setCity(creditCard.getPrimaryOwner().getAddress().getCity());
        returnDTO.setCountry(creditCard.getPrimaryOwner().getAddress().getCountry());
        returnDTO.setMailingAddress(creditCard.getPrimaryOwner().getAddress().getMailingAddress());

        if (creditCard.getOtherOwner() != null) {
            returnDTO.setNameTwo(creditCard.getOtherOwner().getName());
            returnDTO.setDateOfBirthTwo(creditCard.getOtherOwner().getDateOfBirth());
            returnDTO.setDirectionTwo(creditCard.getOtherOwner().getAddress().getDirection());
            returnDTO.setLocationTwo(creditCard.getOtherOwner().getAddress().getLocation());
            returnDTO.setCityTwo(creditCard.getOtherOwner().getAddress().getCity());
            returnDTO.setCountryTwo(creditCard.getOtherOwner().getAddress().getCountry());
            returnDTO.setMailingAddressTwo(creditCard.getOtherOwner().getAddress().getMailingAddress());
        }

        return returnDTO;
    }

}
