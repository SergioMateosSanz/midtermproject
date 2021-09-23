package com.ironhack.midtermproject.service.implementations;

import com.ironhack.midtermproject.classes.AddedInterestRate;
import com.ironhack.midtermproject.classes.Money;
import com.ironhack.midtermproject.controller.dto.MovementDTO;
import com.ironhack.midtermproject.controller.dto.CreditCardDTO;
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
import java.util.Optional;

@Service
public class CreditCardServiceImpl implements CreditCardService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CreditCardRepository creditCardRepository;

    @Autowired
    OwnerRepository ownerRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    MovementRepository movementRepository;

    @Autowired
    AddedInterestRate addedInterestRate;

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
                if (addedInterestRate.isInterestRateToAddCreditCards(creditCard.getId())) {
                    BigDecimal amountAfter = addedInterestRate.calculateInterestRateToSet(creditCard.getBalance().getAmount(),
                            creditCard.getInterestRate());
                    BigDecimal amountDifference = amountAfter.subtract(creditCard.getBalance().getAmount());
                    Movement movementInterestRate = new Movement();
                    movementInterestRate.setTransferAmount(amountDifference);
                    movementInterestRate.setBalanceBefore(creditCard.getBalance().getAmount());
                    movementInterestRate.setBalanceAfter(amountAfter);
                    movementInterestRate.setMovementType(MovementType.INTEREST_RATE);
                    movementInterestRate.setOrderDate(LocalDate.now());
                    movementInterestRate.setTimeExecution(LocalDateTime.now());
                    movementInterestRate.setModificationDate(LocalDate.of(1,1,1));
                    movementInterestRate.setAccount(creditCard);
                    movementRepository.save(movementInterestRate);

                    creditCard.setBalance(new Money(amountAfter));
                    creditCardRepository.save(creditCard);
                }

                returnList.add(fillOutputInformation(creditCard));
            }

            return returnList;
        } else {
            return null;
        }
    }

    @Override
    public CreditCardDTO getCreditCard(int id, String name) {

        Optional<CreditCard> optionalCreditCard = creditCardRepository.findById(id);

        if (optionalCreditCard.isPresent()) {
            if (optionalCreditCard.get().getPrimaryOwner().getName().equals(name)) {
                if (addedInterestRate.isInterestRateToAddCreditCards(optionalCreditCard.get().getId())) {
                    BigDecimal amountAfter = addedInterestRate.calculateInterestRateToSet(optionalCreditCard.get().getBalance().getAmount(),
                            optionalCreditCard.get().getInterestRate());
                    BigDecimal amountDifference = amountAfter.subtract(optionalCreditCard.get().getBalance().getAmount());
                    Movement movementInterestRate = new Movement();
                    movementInterestRate.setTransferAmount(amountDifference);
                    movementInterestRate.setBalanceBefore(optionalCreditCard.get().getBalance().getAmount());
                    movementInterestRate.setBalanceAfter(amountAfter);
                    movementInterestRate.setMovementType(MovementType.INTEREST_RATE);
                    movementInterestRate.setOrderDate(LocalDate.now());
                    movementInterestRate.setTimeExecution(LocalDateTime.now());
                    movementInterestRate.setModificationDate(LocalDate.of(1,1,1));
                    movementInterestRate.setAccount(optionalCreditCard.get());
                    movementRepository.save(movementInterestRate);

                    optionalCreditCard.get().setBalance(new Money(amountAfter));
                    creditCardRepository.save(optionalCreditCard.get());
                }

                return fillOutputInformation(optionalCreditCard.get());
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access not permitted");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
    }

    @Override
    public MovementDTO createMovement(int id, MovementDTO movementDTO, String name) {

        if (movementDTO.getTransferAmount() == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Resource not processable");
        }

        switch (movementDTO.getTransferAmount().compareTo(BigDecimal.ZERO)) {
            case -1:
            case 0:
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Movement not processable");
            case 1:
                break;
        }

        Optional<CreditCard> optionalCreditCard = creditCardRepository.findById(id);

        if (optionalCreditCard.isPresent()) {
            if (optionalCreditCard.get().getPrimaryOwner().getName().equals(name)) {
                BigDecimal amountInAccount = optionalCreditCard.get().getBalance().getAmount();
                BigDecimal amountAfterMovement = amountInAccount.subtract(movementDTO.getTransferAmount());

                switch (amountAfterMovement.compareTo(BigDecimal.ZERO)) {
                    case -1:
                        throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "You do not have enough founds");
                    case 0:
                    case 1:
                        break;
                }

                Movement movement = new Movement();
                movement.setTransferAmount(movementDTO.getTransferAmount().negate());
                movement.setBalanceBefore(amountInAccount);
                movement.setBalanceAfter(amountAfterMovement);
                movement.setMovementType(MovementType.DECREASED);
                movement.setOrderDate(LocalDate.now());
                movement.setTimeExecution(LocalDateTime.now());
                movement.setModificationDate(LocalDate.of(1, 1, 1));
                movement.setAccount(optionalCreditCard.get());
                movementRepository.save(movement);

                optionalCreditCard.get().setBalance(new Money(amountAfterMovement));
                creditCardRepository.save(optionalCreditCard.get());

                return fillOutputMovementInformation(movement);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access not permitted");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
    }

    @Override
    public List<MovementDTO> getMovements(int id, String name) {

        Optional<CreditCard> optionalCreditCard = creditCardRepository.findById(id);

        if (optionalCreditCard.isPresent()) {
            if (optionalCreditCard.get().getPrimaryOwner().getName().equals(name)) {
                List<Account> movementList = accountRepository.getByIdWithMovements(id);
                return fillOutputAllMovements(movementList);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access not permitted");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
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

    private MovementDTO fillOutputMovementInformation(Movement movement) {

        MovementDTO returnDTO = new MovementDTO();

        returnDTO.setId(movement.getId());
        returnDTO.setTransferAmount(movement.getTransferAmount().negate());
        returnDTO.setBalanceBefore(movement.getBalanceBefore());
        returnDTO.setBalanceAfter(movement.getBalanceAfter());
        returnDTO.setMovementType(movement.getMovementType());
        returnDTO.setOrderDate(movement.getOrderDate());
        returnDTO.setTimeExecution(movement.getTimeExecution());
        returnDTO.setModificationDate(movement.getModificationDate());

        return returnDTO;
    }

    private List<MovementDTO> fillOutputAllMovements(List<Account> studentList){

        List<MovementDTO> returnList = new ArrayList<>();
        Movement movement;

        for (int i = 0; i< studentList.size(); i++) {
            MovementDTO movementDTO = new MovementDTO();
            movement = studentList.get(i).getMovementList().get(i);

            movementDTO.setId(movement.getId());
            movementDTO.setTransferAmount(movement.getTransferAmount());
            movementDTO.setBalanceBefore(movement.getBalanceBefore());
            movementDTO.setBalanceAfter(movement.getBalanceAfter());
            movementDTO.setMovementType(movement.getMovementType());
            movementDTO.setOrderDate(movement.getOrderDate());
            movementDTO.setTimeExecution(movement.getTimeExecution());
            movementDTO.setModificationDate(movement.getModificationDate());

            returnList.add(movementDTO);
        }

        return returnList;
    }

}
