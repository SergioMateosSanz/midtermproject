package com.ironhack.midtermproject.service.implementations;

import com.ironhack.midtermproject.classes.Money;
import com.ironhack.midtermproject.controller.dto.CheckingDTO;
import com.ironhack.midtermproject.controller.dto.SavingDTO;
import com.ironhack.midtermproject.enums.AccountStatus;
import com.ironhack.midtermproject.enums.MovementType;
import com.ironhack.midtermproject.model.*;
import com.ironhack.midtermproject.repository.*;
import com.ironhack.midtermproject.service.interfaces.CheckingService;
import com.ironhack.midtermproject.service.interfaces.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CheckingServiceImpl implements CheckingService {

    @Autowired
    CheckingRepository checkingRepository;

    @Autowired
    StudentService studentService;

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
    public CheckingDTO store(CheckingDTO checkingDTO) {

        if (checkingDTO.getDateOfBirth() == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Resource not processable");
        }

        CheckingDTO returnDTO;
        LocalDate now = LocalDate.now();
        if (Period.between(checkingDTO.getDateOfBirth(), now).getYears() < 24) {
            returnDTO = studentService.store(checkingDTO);
        } else {
            returnDTO = storeChecking(checkingDTO);
        }
        return returnDTO;
    }

    @Override
    public List<CheckingDTO> getAllByOwner(String name) {

        List<Owner> ownerList = ownerRepository.findByName(name);

        if (!ownerList.isEmpty()) {
            List<Checking> checkingList = checkingRepository.getAllByOwner(ownerList.get(0));
            List<CheckingDTO> returnList = new ArrayList<>();

            for (Checking checking : checkingList) {
                returnList.add(fillOutputInformation(checking));
            }

            return returnList;
        } else {
            return null;
        }
    }

    @Override
    public CheckingDTO getChecking(int id) {

        Optional<Checking> optionalChecking = checkingRepository.findById(id);

        if (optionalChecking.isPresent()) {
            return fillOutputInformation(optionalChecking.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
    }

    private CheckingDTO storeChecking(CheckingDTO checkingDTO) {

        if (!validInputDTO(checkingDTO)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Resource not processable");
        }

        Checking checking = new Checking();

        if (checkingDTO.getNameTwo() != null) {
            if (checkingDTO.getDateOfBirthTwo() != null) {
                if (!validOwnerTwo(checkingDTO)) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Resource not processable");
                }

                Address address;
                List<Address> addressList = addressRepository.findByMailingAddress(checkingDTO.getMailingAddressTwo());
                if (!addressList.isEmpty()) {
                    address = addressList.get(0);
                } else {
                    address = createAddress(checkingDTO.getDirectionTwo(), checkingDTO.getLocationTwo(),
                            checkingDTO.getCityTwo(), checkingDTO.getCountryTwo(), checkingDTO.getMailingAddressTwo());
                    addressRepository.save(address);
                }

                Owner ownerDatabase = lookOwnerPreviouslyRegister(checkingDTO.getNameTwo(), checkingDTO.getDateOfBirthTwo());
                Owner secondaryOwner;
                if (ownerDatabase != null) {
                    secondaryOwner = ownerDatabase;
                } else {
                    secondaryOwner = fillSecondaryOwnerData(checkingDTO, address);
                    ownerRepository.save(secondaryOwner);
                }

                checking.setOtherOwner(secondaryOwner);
            } else {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Resource not processable");
            }
        }

        Address address;
        List<Address> addressList = addressRepository.findByMailingAddress(checkingDTO.getMailingAddress());
        if (!addressList.isEmpty()) {
            address = addressList.get(0);
        } else {
            address = createAddress(checkingDTO.getDirection(), checkingDTO.getLocation(),
                    checkingDTO.getCity(), checkingDTO.getCountry(), checkingDTO.getMailingAddress());
            addressRepository.save(address);
        }

        Owner ownerDatabase = lookOwnerPreviouslyRegister(checkingDTO.getName(), checkingDTO.getDateOfBirth());
        Owner primaryOwner;
        if (ownerDatabase != null) {
            primaryOwner = ownerDatabase;
        } else {
            primaryOwner = fillOwnerData(checkingDTO, address);
            ownerRepository.save(primaryOwner);
        }

        Money money = new Money(checkingDTO.getAmount());
        checking.setBalance(money);
        checking.setPenaltyFee(PENALTY_FEE);
        checking.setSecretKey(checkingDTO.getSecretKey());
        checking.setMinimumBalance(checkingDTO.getMinimumBalance());
        checking.setMonthlyMaintenanceFee(checkingDTO.getMonthlyMaintenanceFee());
        checking.setStatus(AccountStatus.ACTIVE);
        checking.setPrimaryOwner(primaryOwner);
        checking.setCreationDate(LocalDate.now());
        checking.setModificationDate(LocalDate.of(1, 1, 1));
        checkingRepository.save(checking);

        Movement movement = fillMovementData(checkingDTO);
        movement.setAccount(checking);
        movementRepository.save(movement);

        saveUserCredentials(checkingDTO.getSecretKey(), checkingDTO.getName());

        return fillOutputInformation(checking);
    }

    private boolean validInputDTO(CheckingDTO checkingDTO) {

        if ((checkingDTO.getName().equals("")) || (checkingDTO.getDateOfBirth() == null) || (checkingDTO.getDirection().equals(""))
                || (checkingDTO.getLocation().equals("")) || (checkingDTO.getCity().equals("")) || (checkingDTO.getCountry().equals(""))
                || (checkingDTO.getMailingAddress().equals("")) || (checkingDTO.getSecretKey().equals(""))) {
            return false;
        }
        return true;
    }

    private boolean validOwnerTwo(CheckingDTO checkingDTO) {

        if ((checkingDTO.getNameTwo().equals("")) || (checkingDTO.getDateOfBirthTwo() == null) || (checkingDTO.getDirectionTwo().equals(""))
                || (checkingDTO.getLocationTwo().equals("")) || (checkingDTO.getCityTwo().equals("")) || (checkingDTO.getCountryTwo().equals(""))
                || (checkingDTO.getMailingAddressTwo().equals(""))) {
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

    private Owner fillSecondaryOwnerData(CheckingDTO checkingDTO, Address address) {

        Owner secondaryOwner = new Owner();
        secondaryOwner.setAddress(address);
        secondaryOwner.setName(checkingDTO.getNameTwo());
        secondaryOwner.setDateOfBirth(checkingDTO.getDateOfBirthTwo());
        secondaryOwner.setCreationDate(LocalDate.now());
        secondaryOwner.setModificationDate(LocalDate.of(1, 1, 1));

        return secondaryOwner;
    }

    private Owner fillOwnerData(CheckingDTO checkingDTO, Address address) {

        Owner primaryOwner = new Owner();
        primaryOwner.setAddress(address);
        primaryOwner.setName(checkingDTO.getName());
        primaryOwner.setDateOfBirth(checkingDTO.getDateOfBirth());
        primaryOwner.setCreationDate(LocalDate.now());
        primaryOwner.setModificationDate(LocalDate.of(1, 1, 1));

        return primaryOwner;
    }

    private Movement fillMovementData(CheckingDTO checkingDTO) {

        Movement movement = new Movement();
        movement.setTransferAmount(checkingDTO.getAmount());
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(checkingDTO.getAmount());
        movement.setMovementType(MovementType.CREATED);
        movement.setOrderDate(LocalDate.now());
        movement.setTimeExecution(LocalDateTime.now());
        movement.setModificationDate(LocalDate.of(1, 1, 1));

        return movement;
    }

    private CheckingDTO fillOutputInformation(Checking checking) {

        CheckingDTO returnDTO = new CheckingDTO();

        returnDTO.setId(checking.getId());
        returnDTO.setCurrency(checking.getBalance().getCurrency());
        returnDTO.setAmount(checking.getBalance().getAmount());
        returnDTO.setPenaltyFee(checking.getPenaltyFee());
        returnDTO.setSecretKey("******");
        returnDTO.setMinimumBalance(checking.getMinimumBalance());
        returnDTO.setMonthlyMaintenanceFee(checking.getMonthlyMaintenanceFee());
        returnDTO.setName(checking.getPrimaryOwner().getName());
        returnDTO.setDateOfBirth(checking.getPrimaryOwner().getDateOfBirth());
        returnDTO.setDirection(checking.getPrimaryOwner().getAddress().getDirection());
        returnDTO.setLocation(checking.getPrimaryOwner().getAddress().getLocation());
        returnDTO.setCity(checking.getPrimaryOwner().getAddress().getCity());
        returnDTO.setCountry(checking.getPrimaryOwner().getAddress().getCountry());
        returnDTO.setMailingAddress(checking.getPrimaryOwner().getAddress().getMailingAddress());

        if (checking.getOtherOwner() != null) {
            returnDTO.setNameTwo(checking.getOtherOwner().getName());
            returnDTO.setDateOfBirthTwo(checking.getOtherOwner().getDateOfBirth());
            returnDTO.setDirectionTwo(checking.getOtherOwner().getAddress().getDirection());
            returnDTO.setLocationTwo(checking.getOtherOwner().getAddress().getLocation());
            returnDTO.setCityTwo(checking.getOtherOwner().getAddress().getCity());
            returnDTO.setCountryTwo(checking.getOtherOwner().getAddress().getCountry());
            returnDTO.setMailingAddressTwo(checking.getOtherOwner().getAddress().getMailingAddress());
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
