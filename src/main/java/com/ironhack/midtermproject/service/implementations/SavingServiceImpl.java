package com.ironhack.midtermproject.service.implementations;

import com.ironhack.midtermproject.classes.AddedInterestRate;
import com.ironhack.midtermproject.classes.DecreasedPenaltyFee;
import com.ironhack.midtermproject.classes.Money;
import com.ironhack.midtermproject.controller.dto.MovementDTO;
import com.ironhack.midtermproject.controller.dto.SavingDTO;
import com.ironhack.midtermproject.enums.AccountStatus;
import com.ironhack.midtermproject.enums.MovementType;
import com.ironhack.midtermproject.model.*;
import com.ironhack.midtermproject.repository.*;
import com.ironhack.midtermproject.service.interfaces.SavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SavingServiceImpl implements SavingService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    SavingRepository savingRepository;

    @Autowired
    OwnerRepository ownerRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    MovementRepository movementRepository;

    @Autowired
    DecreasedPenaltyFee decreasedPenaltyFee;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AddedInterestRate addedInterestRate;

    private final BigDecimal PENALTY_FEE = BigDecimal.valueOf(40);

    @Override
    public SavingDTO store(SavingDTO savingDTO) {

        if (!validInputDTO(savingDTO)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Resource not processable");
        }

        Saving saving = new Saving();

        if (savingDTO.getNameTwo() != null) {
            if (savingDTO.getDateOfBirthTwo() != null) {
                if (!validOwnerTwo(savingDTO)) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Resource not processable");
                }

                Address address;
                List<Address> addressList = addressRepository.findByMailingAddress(savingDTO.getMailingAddressTwo());
                if (!addressList.isEmpty()) {
                    address = addressList.get(0);
                } else {
                    address = createAddress(savingDTO.getDirectionTwo(), savingDTO.getLocationTwo(),
                            savingDTO.getCityTwo(), savingDTO.getCountryTwo(), savingDTO.getMailingAddressTwo());
                    addressRepository.save(address);
                }

                Owner ownerDatabase = lookOwnerPreviouslyRegister(savingDTO.getNameTwo(), savingDTO.getDateOfBirthTwo());
                Owner secondaryOwner;
                if (ownerDatabase != null) {
                    secondaryOwner = ownerDatabase;
                } else {
                    secondaryOwner = fillSecondaryOwnerData(savingDTO, address);
                    ownerRepository.save(secondaryOwner);
                }

                saving.setOtherOwner(secondaryOwner);
            } else {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Resource not processable");
            }
        }

        Address address;
        List<Address> addressList = addressRepository.findByMailingAddress(savingDTO.getMailingAddress());
        if (!addressList.isEmpty()) {
            address = addressList.get(0);
        } else {
            address = createAddress(savingDTO.getDirection(), savingDTO.getLocation(),
                    savingDTO.getCity(), savingDTO.getCountry(), savingDTO.getMailingAddress());
            addressRepository.save(address);
        }

        Owner ownerDatabase = lookOwnerPreviouslyRegister(savingDTO.getName(), savingDTO.getDateOfBirth());
        Owner primaryOwner;
        if (ownerDatabase != null) {
            primaryOwner = ownerDatabase;
        } else {
            primaryOwner = fillOwnerData(savingDTO, address);
            ownerRepository.save(primaryOwner);
        }

        Money money = new Money(savingDTO.getAmount());
        saving.setBalance(money);
        saving.setPenaltyFee(PENALTY_FEE);
        saving.setSecretKey(savingDTO.getSecretKey());
        saving.setMinimumBalance(savingDTO.getMinimumBalance());
        saving.setInterestRate(savingDTO.getInterestRate());
        saving.setStatus(AccountStatus.ACTIVE);
        saving.setPrimaryOwner(primaryOwner);
        saving.setCreationDate(LocalDate.now());
        saving.setModificationDate(LocalDate.of(1, 1, 1));
        savingRepository.save(saving);

        Movement movement = fillMovementData(savingDTO);
        movement.setAccount(saving);
        movementRepository.save(movement);

        saveUserCredentials(savingDTO.getSecretKey(), savingDTO.getName());

        return fillOutputInformation(saving);
    }

    @Override
    public List<SavingDTO> getAllByOwner(String name) {

        List<Owner> ownerList = ownerRepository.findByName(name);

        if (!ownerList.isEmpty()) {
            List<Saving> savingList = savingRepository.getAllByOwner(ownerList.get(0));
            List<SavingDTO> returnList = new ArrayList<>();

            for (Saving saving : savingList) {
                if (addedInterestRate.isInterestRateToAddSavings(saving.getId())) {
                    BigDecimal amountAfter = addedInterestRate.calculateInterestRateToSet(saving.getBalance().getAmount(),
                            saving.getInterestRate());
                    BigDecimal amountDifference = amountAfter.subtract(saving.getBalance().getAmount());
                    Movement movementInterestRate = new Movement();
                    movementInterestRate.setTransferAmount(amountDifference);
                    movementInterestRate.setBalanceBefore(saving.getBalance().getAmount());
                    movementInterestRate.setBalanceAfter(amountAfter);
                    movementInterestRate.setMovementType(MovementType.INTEREST_RATE);
                    movementInterestRate.setOrderDate(LocalDate.now());
                    movementInterestRate.setTimeExecution(LocalDateTime.now());
                    movementInterestRate.setModificationDate(LocalDate.of(1,1,1));
                    movementInterestRate.setAccount(saving);
                    movementRepository.save(movementInterestRate);

                    saving.setBalance(new Money(amountAfter));
                    savingRepository.save(saving);
                }

                returnList.add(fillOutputInformation(saving));
            }

            return returnList;
        } else {
            return null;
        }
    }

    @Override
    public SavingDTO getSaving(int id, String name) {

        Optional<Saving> optionalSaving = savingRepository.findById(id);

        if (optionalSaving.isPresent()) {
            if (optionalSaving.get().getPrimaryOwner().getName().equals(name)) {
                if (addedInterestRate.isInterestRateToAddSavings(optionalSaving.get().getId())) {
                    BigDecimal amountAfter = addedInterestRate.calculateInterestRateToSet(optionalSaving.get().getBalance().getAmount(),
                            optionalSaving.get().getInterestRate());
                    BigDecimal amountDifference = amountAfter.subtract(optionalSaving.get().getBalance().getAmount());
                    Movement movementInterestRate = new Movement();
                    movementInterestRate.setTransferAmount(amountDifference);
                    movementInterestRate.setBalanceBefore(optionalSaving.get().getBalance().getAmount());
                    movementInterestRate.setBalanceAfter(amountAfter);
                    movementInterestRate.setMovementType(MovementType.INTEREST_RATE);
                    movementInterestRate.setOrderDate(LocalDate.now());
                    movementInterestRate.setTimeExecution(LocalDateTime.now());
                    movementInterestRate.setModificationDate(LocalDate.of(1,1,1));
                    movementInterestRate.setAccount(optionalSaving.get());
                    movementRepository.save(movementInterestRate);

                    optionalSaving.get().setBalance(new Money(amountAfter));
                    savingRepository.save(optionalSaving.get());
                }
                return fillOutputInformation(optionalSaving.get());
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

        Optional<Saving> optionalSaving = savingRepository.findById(id);

        if (optionalSaving.isPresent()) {
            if (optionalSaving.get().getPrimaryOwner().getName().equals(name)) {
                BigDecimal amountInAccount = optionalSaving.get().getBalance().getAmount();
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
                movement.setAccount(optionalSaving.get());
                movementRepository.save(movement);

                optionalSaving.get().setBalance(new Money(amountAfterMovement));
                savingRepository.save(optionalSaving.get());

                if (decreasedPenaltyFee.isPenaltyFeeSavingAccounts(optionalSaving.get(), movementDTO.getTransferAmount())) {
                    optionalSaving.get().setBalance(new Money(decreasedPenaltyFee.calculateBalanceAmountToSet
                            (amountInAccount, optionalSaving.get().getPenaltyFee())));
                    Movement movementPenalty = new Movement();
                    movementPenalty.setTransferAmount(optionalSaving.get().getPenaltyFee().negate());
                    movementPenalty.setBalanceBefore(amountAfterMovement);
                    movementPenalty.setBalanceAfter(amountAfterMovement.subtract(optionalSaving.get().getPenaltyFee()));
                    movementPenalty.setMovementType(MovementType.PENALTY_FEE);
                    movementPenalty.setOrderDate(LocalDate.now());
                    movementPenalty.setTimeExecution(LocalDateTime.now());
                    movementPenalty.setModificationDate(LocalDate.of(1, 1, 1));
                    movementPenalty.setAccount(optionalSaving.get());
                    movementRepository.save(movementPenalty);

                    optionalSaving.get().setBalance(new Money(amountAfterMovement.subtract(optionalSaving.get().getPenaltyFee())));
                } else {
                    optionalSaving.get().setBalance(new Money(amountAfterMovement));
                }

                savingRepository.save(optionalSaving.get());

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

        Optional<Saving> optionalSaving = savingRepository.findById(id);

        if (optionalSaving.isPresent()) {
            if (optionalSaving.get().getPrimaryOwner().getName().equals(name)) {
                List<Account> movementList = accountRepository.getByIdWithMovements(id);
                return fillOutputAllMovements(movementList);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access not permitted");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
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

        if ((savingDTO.getNameTwo().equals("")) || (savingDTO.getDateOfBirthTwo() == null) || (savingDTO.getDirectionTwo().equals(""))
                || (savingDTO.getLocationTwo().equals("")) || (savingDTO.getCityTwo().equals("")) || (savingDTO.getCountryTwo().equals(""))
                || (savingDTO.getMailingAddressTwo().equals(""))) {
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
        address.setModificationDate(LocalDate.of(1, 1, 1));

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

    private Owner fillSecondaryOwnerData(SavingDTO savingDTO, Address address) {

        Owner secondaryOwner = new Owner();
        secondaryOwner.setAddress(address);
        secondaryOwner.setName(savingDTO.getNameTwo());
        secondaryOwner.setDateOfBirth(savingDTO.getDateOfBirthTwo());
        secondaryOwner.setCreationDate(LocalDate.now());
        secondaryOwner.setModificationDate(LocalDate.of(1, 1, 1));

        return secondaryOwner;
    }

    private Owner fillOwnerData(SavingDTO savingDTO, Address address) {

        Owner primaryOwner = new Owner();
        primaryOwner.setAddress(address);
        primaryOwner.setName(savingDTO.getName());
        primaryOwner.setDateOfBirth(savingDTO.getDateOfBirth());
        primaryOwner.setCreationDate(LocalDate.now());
        primaryOwner.setModificationDate(LocalDate.of(1, 1, 1));

        return primaryOwner;
    }

    private Movement fillMovementData(SavingDTO savingDTO) {

        Movement movement = new Movement();
        movement.setTransferAmount(savingDTO.getAmount());
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(savingDTO.getAmount());
        movement.setMovementType(MovementType.CREATED);
        movement.setOrderDate(LocalDate.now());
        movement.setTimeExecution(LocalDateTime.now());
        movement.setModificationDate(LocalDate.of(1, 1, 1));

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

    private void saveUserCredentials(String secretKey, String name) {

        Optional<User> optionalUser = userRepository.findByUsername(name);
        if (!optionalUser.isPresent()) {
            User user = new User();
            user.setUsername(name);
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(secretKey));
            userRepository.save(user);
            Role holderRole = new Role("HOLDER");
            holderRole.setUser(user);
            roleRepository.save(holderRole);
        }
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
