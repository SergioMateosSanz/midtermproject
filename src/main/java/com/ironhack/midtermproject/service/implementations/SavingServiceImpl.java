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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

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

        Address address = createAddress(savingDTO.getDirection(), savingDTO.getLocation(),
                savingDTO.getCity(), savingDTO.getCountry(), savingDTO.getMailingAddress());
        addressRepository.save(address);

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
        saving.setModificationDate(LocalDate.of(1,1,1));
        savingRepository.save(saving);

        Movement movement = fillMovementData(savingDTO);
        movement.setAccount(saving);
        movementRepository.save(movement);

        saveUserCredentials(savingDTO.getSecretKey(), savingDTO.getName());

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

    private Owner fillSecondaryOwnerData(SavingDTO savingDTO, Address address) {

        Owner secondaryOwner = new Owner();
        secondaryOwner.setAddress(address);
        secondaryOwner.setName(savingDTO.getNameTwo());
        secondaryOwner.setDateOfBirth(savingDTO.getDateOfBirthTwo());
        secondaryOwner.setCreationDate(LocalDate.now());
        secondaryOwner.setModificationDate(LocalDate.of(1,1,1));

        return secondaryOwner;
    }

    private Owner fillOwnerData(SavingDTO savingDTO, Address address) {

        Owner primaryOwner = new Owner();
        primaryOwner.setAddress(address);
        primaryOwner.setName(savingDTO.getName());
        primaryOwner.setDateOfBirth(savingDTO.getDateOfBirth());
        primaryOwner.setCreationDate(LocalDate.now());
        primaryOwner.setModificationDate(LocalDate.of(1,1,1));

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
        movement.setModificationDate(LocalDate.of(1,1,1));

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
}
