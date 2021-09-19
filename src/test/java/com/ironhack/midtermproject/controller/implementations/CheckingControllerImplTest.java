package com.ironhack.midtermproject.controller.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.midtermproject.classes.Money;
import com.ironhack.midtermproject.controller.dto.CheckingDTO;
import com.ironhack.midtermproject.controller.dto.SavingDTO;
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
import org.springframework.security.test.context.support.WithMockUser;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CheckingControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    CheckingRepository checkingRepository;

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

    CheckingDTO checkingDTO;
    Checking checking;
    Checking checkingTwo;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);


        checkingDTO = new CheckingDTO();
        checkingDTO.setAmount(BigDecimal.TEN);
        checkingDTO.setSecretKey("123456");
        checkingDTO.setMinimumBalance(BigDecimal.valueOf(100));
        checkingDTO.setInterestRate(BigDecimal.ZERO);
        checkingDTO.setName("Michael Douglas");
        checkingDTO.setDateOfBirth(LocalDate.of(1944, 9, 25));
        checkingDTO.setDirection("direction");
        checkingDTO.setLocation("location");
        checkingDTO.setCity("city");
        checkingDTO.setCountry("country");
        checkingDTO.setMailingAddress("mailingAddress@email.com");
        checkingDTO.setNameTwo("Catherine Z Jones");
        checkingDTO.setDateOfBirthTwo(LocalDate.of(1969, 9, 25));
        checkingDTO.setDirectionTwo("direction");
        checkingDTO.setLocationTwo("location");
        checkingDTO.setCityTwo("city");
        checkingDTO.setCountryTwo("country");
        checkingDTO.setMailingAddressTwo("mailingAddress2@email.com");

        User user = new User();
        user.setUsername("Michael Douglas");
        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);
        Role adminRole = new Role("HOLDER");
        adminRole.setUser(user);
        roleRepository.save(adminRole);

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

        checking = new Checking();
        checking.setPrimaryOwner(owner);
        checking.setBalance(new Money(BigDecimal.TEN));
        checking.setPenaltyFee(BigDecimal.ZERO);
        checking.setCreationDate(LocalDate.now());
        checkingRepository.save(checking);

        Movement movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setAccount(checking);
        movementRepository.save(movement);

        checkingTwo = new Checking();
        checkingTwo.setPrimaryOwner(owner);
        checkingTwo.setBalance(new Money(BigDecimal.TEN));
        checkingTwo.setPenaltyFee(BigDecimal.ZERO);
        checkingTwo.setCreationDate(LocalDate.now());
        checkingRepository.save(checkingTwo);

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setAccount(checkingTwo);
        movementRepository.save(movement);
    }

    @AfterEach
    void tearDown() {
        movementRepository.deleteAll();
        checkingRepository.deleteAll();
        studentRepository.deleteAll();
        ownerRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void store_UnprocessedEntity_InvalidBodyNotBirthDateInformed() throws Exception {

        checkingDTO.setDateOfBirth(null);
        String body = objectMapper.writeValueAsString(checkingDTO);
        mockMvc.perform(post("/accounts/checkings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void store_UnprocessedEntity_InvalidBodyWithSecondaryOwnerData_CheckingAccount() throws Exception {

        checkingDTO.setDirection("");
        String body = objectMapper.writeValueAsString(checkingDTO);
        mockMvc.perform(post("/accounts/checkings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());

        checkingDTO.setDirection("direction");
        checkingDTO.setDateOfBirthTwo(LocalDate.of(1969, 9, 25));
        checkingDTO.setDirectionTwo("");
        body = objectMapper.writeValueAsString(checkingDTO);
        mockMvc.perform(post("/accounts/checkings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());

        checkingDTO.setDateOfBirthTwo(null);
        body = objectMapper.writeValueAsString(checkingDTO);
        mockMvc.perform(post("/accounts/checkings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void store_UnprocessedEntity_InvalidBodyWithSecondaryOwnerData_StudentAccount() throws Exception {

        checkingDTO.setDateOfBirth(LocalDate.now());
        checkingDTO.setDirection("");
        String body = objectMapper.writeValueAsString(checkingDTO);
        mockMvc.perform(post("/accounts/checkings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());

        checkingDTO.setDirection("direction");
        checkingDTO.setDateOfBirthTwo(LocalDate.of(1969, 9, 25));
        checkingDTO.setDirectionTwo("");
        body = objectMapper.writeValueAsString(checkingDTO);
        mockMvc.perform(post("/accounts/checkings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());

        checkingDTO.setDateOfBirthTwo(null);
        body = objectMapper.writeValueAsString(checkingDTO);
        mockMvc.perform(post("/accounts/checkings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void store_Created_ValidBodyWithSecondaryOwnerData_CheckingAccount() throws Exception {

        userRepository.deleteAll();
        roleRepository.deleteAll();
        String body = objectMapper.writeValueAsString(checkingDTO);
        MvcResult mvcResult = mockMvc.perform(post("/accounts/checkings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("mailingAddress@email.com"));
        assertEquals(3, ownerRepository.findAll().size());

        body = objectMapper.writeValueAsString(checkingDTO);
        mvcResult = mockMvc.perform(post("/accounts/checkings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("mailingAddress@email.com"));
        assertEquals(3, ownerRepository.findAll().size());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void store_Created_ValidBodyWithSecondaryOwnerData_StudentAccount() throws Exception {

        userRepository.deleteAll();
        roleRepository.deleteAll();
        checkingDTO.setDateOfBirth(LocalDate.now());
        String body = objectMapper.writeValueAsString(checkingDTO);
        MvcResult mvcResult = mockMvc.perform(post("/accounts/checkings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("mailingAddress@email.com"));
        assertEquals(3, ownerRepository.findAll().size());

        body = objectMapper.writeValueAsString(checkingDTO);
        mvcResult = mockMvc.perform(post("/accounts/checkings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("mailingAddress@email.com"));
        assertEquals(3, ownerRepository.findAll().size());
    }

    @Test
    void getAll_ReturnEmptyList_NoAccounts() throws Exception {

        movementRepository.deleteAll();
        checkingRepository.deleteAll();
        ownerRepository.deleteAll();
        MvcResult mvcResult = mockMvc.perform(get("/accounts/checkings").with(httpBasic("Michael Douglas", "123456")))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getAll_ReturnCheckingList_AccountsInDatabase() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/accounts/checkings").with(httpBasic("Michael Douglas", "123456")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains(""+checking.getId()+""));
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains(""+checkingTwo.getId()+""));
    }
}