package com.ironhack.midtermproject.controller.implementations;

import com.ironhack.midtermproject.classes.Money;
import com.ironhack.midtermproject.enums.MovementType;
import com.ironhack.midtermproject.model.*;
import com.ironhack.midtermproject.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class StudentControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

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

    @Autowired
    private PasswordEncoder passwordEncoder;

    Student student;
    Student studentTwo;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        User user = new User();
        user.setUsername("Michael Douglas");
        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);
        Role holderRole = new Role("HOLDER");
        holderRole.setUser(user);
        roleRepository.save(holderRole);

        Address address = new Address();
        address.setDirection("direction");
        address.setLocation("location");
        address.setCity("city");
        address.setCountry("country");
        address.setMailingAddress("email");
        address.setCreationDate(LocalDate.now());
        addressRepository.save(address);

        Owner owner = new Owner();
        owner.setName("Michael Douglas");
        owner.setDateOfBirth(LocalDate.of(1980, 10, 3));
        owner.setCreationDate(LocalDate.now());
        owner.setAddress(address);
        ownerRepository.save(owner);

        student = new Student();
        student.setPrimaryOwner(owner);
        student.setBalance(new Money(BigDecimal.TEN));
        student.setPenaltyFee(BigDecimal.ZERO);
        student.setCreationDate(LocalDate.now());
        studentRepository.save(student);

        Movement movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setAccount(student);
        movementRepository.save(movement);

        studentTwo = new Student();
        studentTwo.setPrimaryOwner(owner);
        studentTwo.setBalance(new Money(BigDecimal.TEN));
        studentTwo.setPenaltyFee(BigDecimal.ZERO);
        studentTwo.setCreationDate(LocalDate.now());
        studentRepository.save(studentTwo);

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setAccount(studentTwo);
        movementRepository.save(movement);

        User userTwo = new User();
        userTwo.setUsername("Andres Iniesta");
        userTwo.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(userTwo);
        holderRole = new Role("HOLDER");
        holderRole.setUser(userTwo);
        roleRepository.save(holderRole);
    }

    @AfterEach
    void tearDown() {
        movementRepository.deleteAll();
        studentRepository.deleteAll();
        ownerRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void getAll_ReturnEmptyList_NoAccounts() throws Exception {

        movementRepository.deleteAll();
        studentRepository.deleteAll();
        ownerRepository.deleteAll();
        MvcResult mvcResult = mockMvc.perform(get("/accounts/students").with(httpBasic("Michael Douglas", "123456")))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getAll_ReturnStudentList_AccountsInDatabase() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/accounts/students").with(httpBasic("Michael Douglas", "123456")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains(""+student.getId()+""));
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains(""+studentTwo.getId()+""));
    }

    @Test
    void getStudent_NoFound_AccountNotExits() throws Exception {

        mockMvc.perform(get("/accounts/students/0").with(httpBasic("Andres Iniesta", "123456")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getStudent_Forbidden_AccountExits() throws Exception {

        mockMvc.perform(get("/accounts/students/"+student.getId()).with(httpBasic("Andres Iniesta", "123456")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getStudent_ReturnChecking_AccountExits() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/accounts/students/"+student.getId()).with(httpBasic("Michael Douglas", "123456")))
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains(""+student.getId()+""));
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("Michael Douglas"));
    }
}