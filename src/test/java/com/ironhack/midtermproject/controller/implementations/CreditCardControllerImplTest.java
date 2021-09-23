package com.ironhack.midtermproject.controller.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.midtermproject.classes.Money;
import com.ironhack.midtermproject.controller.dto.MovementDTO;
import com.ironhack.midtermproject.controller.dto.CreditCardDTO;
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
    MovementDTO movementDTO;

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
        owner.setName("holder");
        owner.setDateOfBirth(LocalDate.of(1980, 10, 3));
        owner.setCreationDate(LocalDate.now());
        owner.setAddress(address);
        ownerRepository.save(owner);

        creditCard = new CreditCard();
        creditCard.setPrimaryOwner(owner);
        creditCard.setBalance(new Money(BigDecimal.TEN));
        creditCard.setPenaltyFee(BigDecimal.ZERO);
        creditCard.setInterestRate(BigDecimal.valueOf(0.3));
        creditCard.setCreationDate(LocalDate.now());
        creditCardRepository.save(creditCard);

        Movement movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setOrderDate(LocalDate.of(2000,1, 1));
        movement.setAccount(creditCard);
        movementRepository.save(movement);

        creditCardTwo = new CreditCard();
        creditCardTwo.setPrimaryOwner(owner);
        creditCardTwo.setBalance(new Money(BigDecimal.TEN));
        creditCardTwo.setPenaltyFee(BigDecimal.ZERO);
        creditCardTwo.setInterestRate(BigDecimal.valueOf(0.3));
        creditCardTwo.setCreationDate(LocalDate.now());
        creditCardRepository.save(creditCardTwo);

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setOrderDate(LocalDate.now());
        movement.setAccount(creditCardTwo);
        movementRepository.save(movement);

        User userTwo = new User();
        userTwo.setUsername("Andres Iniesta");
        userTwo.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(userTwo);
        holderRole = new Role("HOLDER");
        holderRole.setUser(userTwo);
        roleRepository.save(holderRole);

        movementDTO = new MovementDTO();
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
        MvcResult mvcResult = mockMvc.perform(get("/accounts/credits")
                        .with(httpBasic("holder", "123456")))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getAll_ReturnCreditCardList_AccountsInDatabase() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/accounts/credits")
                        .with(httpBasic("holder", "123456")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains(""+creditCard.getId()+""));
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains(""+creditCardTwo.getId()+""));
    }

    @Test
    void getCreditCard_NoFound_AccountNotExits() throws Exception {

        mockMvc.perform(get("/accounts/credits/0").with(httpBasic("Andres Iniesta", "123456")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCreditCard_Forbidden_AccountExits() throws Exception {

        mockMvc.perform(get("/accounts/credits/"+creditCard.getId())
                        .with(httpBasic("Andres Iniesta", "123456")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getCreditCard_ReturnChecking_AccountExits() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/accounts/credits/"+creditCard.getId())
                        .with(httpBasic("holder", "123456")))
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains(""+creditCard.getId()+""));
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("holder"));
    }

    @Test
    void createMovement_UnprocessedEntity_NullTransferAmount() throws Exception {

        movementDTO.setTransferAmount(null);
        String body = objectMapper.writeValueAsString(movementDTO);
        mockMvc.perform(post("/accounts/credits/"+creditCard.getId()+"/movements")
                        .with(httpBasic("Andres Iniesta", "123456"))
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createMovement_UnprocessedEntity_TransferAmountZero() throws Exception {

        movementDTO.setTransferAmount(BigDecimal.ZERO);
        String body = objectMapper.writeValueAsString(movementDTO);
        mockMvc.perform(post("/accounts/credits/"+creditCard.getId()+"/movements")
                        .with(httpBasic("Andres Iniesta", "123456"))
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createMovement_NotFound_AccountNotExits() throws Exception {

        movementDTO.setTransferAmount(BigDecimal.TEN);
        String body = objectMapper.writeValueAsString(movementDTO);
        mockMvc.perform(post("/accounts/credits/0/movements")
                        .with(httpBasic("Andres Iniesta", "123456"))
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void createMovement_Forbidden_AccountOtherOwner() throws Exception {

        movementDTO.setTransferAmount(BigDecimal.TEN);
        String body = objectMapper.writeValueAsString(movementDTO);
        mockMvc.perform(post("/accounts/credits/"+creditCard.getId()+"/movements")
                        .with(httpBasic("Andrés Iniesta", "123456"))
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void createMovement_UnprocessedEntity_NotEnoughFounds() throws Exception {

        movementDTO.setTransferAmount(BigDecimal.valueOf(100000000));
        String body = objectMapper.writeValueAsString(movementDTO);
        mockMvc.perform(post("/accounts/credits/"+creditCard.getId()+"/movements")
                        .with(httpBasic("holder", "123456"))
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createMovement_Created_ValidationOk() throws Exception {

        movementDTO.setTransferAmount(BigDecimal.valueOf(1));
        String body = objectMapper.writeValueAsString(movementDTO);
        MvcResult mvcResult = mockMvc.perform(post("/accounts/credits/"+creditCard.getId()+"/movements")
                        .with(httpBasic("holder", "123456"))
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("1"));
        assertEquals(creditCard.getBalance().getAmount().subtract(BigDecimal.valueOf(1)),
                creditCardRepository.findById(creditCard.getId()).get().getBalance().getAmount());
    }

    @Test
    void getMovements_isForbidden_AccountExistsWithMovements() throws Exception {

        mockMvc.perform(get("/accounts/credits/"+creditCard.getId()+"/movements")
                        .with(httpBasic("Andrés Iniesta", "123456"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void getMovements_isNotFound_AccountExistsWithMovements() throws Exception {

        mockMvc.perform(get("/accounts/credits/0/movements")
                        .with(httpBasic("holder", "123456"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void getMovements_isOk_AccountExistsWithMovements() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/accounts/credits/"+creditCard.getId()+"/movements")
                        .with(httpBasic("holder", "123456"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("CREATED"));
    }
}