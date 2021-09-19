package com.ironhack.midtermproject.controller.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.midtermproject.classes.Money;
import com.ironhack.midtermproject.controller.dto.CreditCardDTO;
import com.ironhack.midtermproject.controller.dto.SavingDTO;
import com.ironhack.midtermproject.enums.MovementType;
import com.ironhack.midtermproject.model.*;
import com.ironhack.midtermproject.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
class CreditCardControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    CreditCardRepository creditCardRepository;

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

    CreditCardDTO creditCardDTO;
    CreditCard creditCard;
    CreditCard creditCardTwo;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);


        creditCardDTO = new CreditCardDTO();
        creditCardDTO.setAmount(BigDecimal.TEN);
        creditCardDTO.setCreditLimit(BigDecimal.valueOf(100));
        creditCardDTO.setInterestRate(BigDecimal.ZERO);
        creditCardDTO.setName("Michael Douglas");
        creditCardDTO.setDateOfBirth(LocalDate.of(1944, 9, 25));
        creditCardDTO.setDirection("direction");
        creditCardDTO.setLocation("location");
        creditCardDTO.setCity("city");
        creditCardDTO.setCountry("country");
        creditCardDTO.setMailingAddress("mailingAddress@email.com");
        creditCardDTO.setNameTwo("Catherine Z Jones");
        creditCardDTO.setDateOfBirthTwo(LocalDate.of(1969, 9, 25));
        creditCardDTO.setDirectionTwo("direction");
        creditCardDTO.setLocationTwo("location");
        creditCardDTO.setCityTwo("city");
        creditCardDTO.setCountryTwo("country");
        creditCardDTO.setMailingAddressTwo("mailingAddress2@email.com");

        User user = new User();
        user.setUsername("holder");
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
        owner.setName("holder");
        owner.setDateOfBirth(LocalDate.of(1980, 10, 3));
        owner.setCreationDate(LocalDate.now());
        owner.setAddress(address);
        ownerRepository.save(owner);

        creditCard = new CreditCard();
        creditCard.setPrimaryOwner(owner);
        creditCard.setBalance(new Money(BigDecimal.TEN));
        creditCard.setPenaltyFee(BigDecimal.ZERO);
        creditCard.setCreationDate(LocalDate.now());
        creditCardRepository.save(creditCard);

        Movement movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setAccount(creditCard);
        movementRepository.save(movement);

        creditCardTwo = new CreditCard();
        creditCardTwo.setPrimaryOwner(owner);
        creditCardTwo.setBalance(new Money(BigDecimal.TEN));
        creditCardTwo.setPenaltyFee(BigDecimal.ZERO);
        creditCardTwo.setCreationDate(LocalDate.now());
        creditCardRepository.save(creditCardTwo);

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setAccount(creditCardTwo);
        movementRepository.save(movement);
    }

    @AfterEach
    void tearDown() {
        movementRepository.deleteAll();
        creditCardRepository.deleteAll();
        ownerRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void store_UnprocessedEntity_InvalidBodyAtValidInputDTO() throws Exception {

        creditCardDTO.setDateOfBirth(null);
        String body = objectMapper.writeValueAsString(creditCardDTO);
        mockMvc.perform(post("/accounts/credits")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void store_UnprocessedEntity_InvalidBodyWithSecondaryOwnerData() throws Exception {

        creditCardDTO.setDateOfBirthTwo(null);
        String body = objectMapper.writeValueAsString(creditCardDTO);
        mockMvc.perform(post("/accounts/credits")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());

        creditCardDTO.setDateOfBirthTwo(LocalDate.of(1969, 9, 25));
        creditCardDTO.setDirectionTwo("");
        body = objectMapper.writeValueAsString(creditCardDTO);
        mockMvc.perform(post("/accounts/credits")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void store_Created_ValidBodyWithSecondaryOwnerData() throws Exception {

        String body = objectMapper.writeValueAsString(creditCardDTO);
        MvcResult mvcResult = mockMvc.perform(post("/accounts/credits")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("mailingAddress@email.com"));
        assertEquals(3, ownerRepository.findAll().size());

        body = objectMapper.writeValueAsString(creditCardDTO);
        mvcResult = mockMvc.perform(post("/accounts/credits")
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
        creditCardRepository.deleteAll();
        ownerRepository.deleteAll();
        MvcResult mvcResult = mockMvc.perform(get("/accounts/savings").with(httpBasic("holder", "123456")))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @Disabled
    void getAll_ReturnCreditCardList_AccountsInDatabase() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/accounts/savings").with(httpBasic("holder", "123456")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains(""+creditCard.getId()+""));
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains(""+creditCardTwo.getId()+""));
    }
}