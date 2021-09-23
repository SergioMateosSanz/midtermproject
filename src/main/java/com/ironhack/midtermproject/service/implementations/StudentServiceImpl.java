package com.ironhack.midtermproject.service.implementations;

import com.ironhack.midtermproject.classes.Money;
import com.ironhack.midtermproject.controller.dto.MovementDTO;
import com.ironhack.midtermproject.controller.dto.CheckingDTO;
import com.ironhack.midtermproject.enums.AccountStatus;
import com.ironhack.midtermproject.enums.MovementType;
import com.ironhack.midtermproject.model.*;
import com.ironhack.midtermproject.repository.*;
import com.ironhack.midtermproject.service.interfaces.StudentService;
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
public class StudentServiceImpl implements StudentService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    StudentRepository studentRepository;

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

        if (!validInputDTO(checkingDTO)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Resource not processable");
        }

        Student student = new Student();

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

                student.setOtherOwner(secondaryOwner);
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
        student.setBalance(money);
        student.setPenaltyFee(PENALTY_FEE);
        student.setSecretKey(checkingDTO.getSecretKey());
        student.setStatus(AccountStatus.ACTIVE);
        student.setPrimaryOwner(primaryOwner);
        student.setCreationDate(LocalDate.now());
        student.setModificationDate(LocalDate.of(1, 1, 1));
        studentRepository.save(student);

        Movement movement = fillMovementData(checkingDTO);
        movement.setAccount(student);
        movementRepository.save(movement);

        saveUserCredentials(checkingDTO.getSecretKey(), checkingDTO.getName());

        return fillOutputInformation(student);
    }

    @Override
    public List<CheckingDTO> getAllByOwner(String name) {

        List<Owner> ownerList = ownerRepository.findByName(name);

        if (!ownerList.isEmpty()) {
            List<Student> studentList = studentRepository.getAllByOwner(ownerList.get(0));
            List<CheckingDTO> returnList = new ArrayList<>();

            for (Student student : studentList) {
                returnList.add(fillOutputInformation(student));
            }

            return returnList;
        } else {
            return null;
        }
    }

    @Override
    public CheckingDTO getStudent(int id, String name) {

        Optional<Student> optionalStudent = studentRepository.findById(id);

        if (optionalStudent.isPresent()) {
            if (optionalStudent.get().getPrimaryOwner().getName().equals(name)) {
                return fillOutputInformation(optionalStudent.get());
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

        Optional<Student> optionalStudent = studentRepository.findById(id);

        if (optionalStudent.isPresent()) {
            if (optionalStudent.get().getPrimaryOwner().getName().equals(name)) {
                BigDecimal amountInAccount = optionalStudent.get().getBalance().getAmount();
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
                movement.setAccount(optionalStudent.get());
                movementRepository.save(movement);

                optionalStudent.get().setBalance(new Money(amountAfterMovement));
                studentRepository.save(optionalStudent.get());

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

        Optional<Student> optionalStudent = studentRepository.findById(id);

        if (optionalStudent.isPresent()) {
            if (optionalStudent.get().getPrimaryOwner().getName().equals(name)) {
                List<Account> movementList = accountRepository.getByIdWithMovements(id);
                return fillOutputAllMovements(movementList);
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access not permitted");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        }
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

    private CheckingDTO fillOutputInformation(Student student) {

        CheckingDTO returnDTO = new CheckingDTO();

        returnDTO.setId(student.getId());
        returnDTO.setCurrency(student.getBalance().getCurrency());
        returnDTO.setAmount(student.getBalance().getAmount());
        returnDTO.setPenaltyFee(student.getPenaltyFee());
        returnDTO.setSecretKey("******");
        returnDTO.setMinimumBalance(BigDecimal.ZERO);
        returnDTO.setMonthlyMaintenanceFee(BigDecimal.ZERO);
        returnDTO.setName(student.getPrimaryOwner().getName());
        returnDTO.setDateOfBirth(student.getPrimaryOwner().getDateOfBirth());
        returnDTO.setDirection(student.getPrimaryOwner().getAddress().getDirection());
        returnDTO.setLocation(student.getPrimaryOwner().getAddress().getLocation());
        returnDTO.setCity(student.getPrimaryOwner().getAddress().getCity());
        returnDTO.setCountry(student.getPrimaryOwner().getAddress().getCountry());
        returnDTO.setMailingAddress(student.getPrimaryOwner().getAddress().getMailingAddress());

        if (student.getOtherOwner() != null) {
            returnDTO.setNameTwo(student.getOtherOwner().getName());
            returnDTO.setDateOfBirthTwo(student.getOtherOwner().getDateOfBirth());
            returnDTO.setDirectionTwo(student.getOtherOwner().getAddress().getDirection());
            returnDTO.setLocationTwo(student.getOtherOwner().getAddress().getLocation());
            returnDTO.setCityTwo(student.getOtherOwner().getAddress().getCity());
            returnDTO.setCountryTwo(student.getOtherOwner().getAddress().getCountry());
            returnDTO.setMailingAddressTwo(student.getOtherOwner().getAddress().getMailingAddress());
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
