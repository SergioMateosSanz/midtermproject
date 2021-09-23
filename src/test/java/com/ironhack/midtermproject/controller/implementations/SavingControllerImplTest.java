package com.ironhack.midtermproject.controller.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.midtermproject.classes.Money;
import com.ironhack.midtermproject.controller.dto.MovementDTO;
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
class SavingControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

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

    @Autowired
    private PasswordEncoder passwordEncoder;

    SavingDTO savingDTO;
    Saving saving;
    Saving savingTwo;
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


        savingDTO = new SavingDTO();
        savingDTO.setAmount(BigDecimal.TEN);
        savingDTO.setSecretKey("123456");
        savingDTO.setMinimumBalance(BigDecimal.valueOf(100));
        savingDTO.setInterestRate(BigDecimal.ZERO);
        savingDTO.setName("Michael Douglas");
        savingDTO.setDateOfBirth(LocalDate.of(1944, 9, 25));
        savingDTO.setDirection("direction");
        savingDTO.setLocation("location");
        savingDTO.setCity("city");
        savingDTO.setCountry("country");
        savingDTO.setMailingAddress("mailingAddress@email.com");
        savingDTO.setNameTwo("Catherine Z Jones");
        savingDTO.setDateOfBirthTwo(LocalDate.of(1969, 9, 25));
        savingDTO.setDirectionTwo("direction");
        savingDTO.setLocationTwo("location");
        savingDTO.setCityTwo("city");
        savingDTO.setCountryTwo("country");
        savingDTO.setMailingAddressTwo("mailingAddress2@email.com");

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

        saving = new Saving();
        saving.setPrimaryOwner(owner);
        saving.setBalance(new Money(BigDecimal.TEN));
        saving.setPenaltyFee(BigDecimal.valueOf(3));
        saving.setMinimumBalance(BigDecimal.ZERO);
        saving.setInterestRate(BigDecimal.valueOf(0.3));
        saving.setCreationDate(LocalDate.now());
        savingRepository.save(saving);

        Movement movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setOrderDate(LocalDate.of(2000,1, 1));
        movement.setAccount(saving);
        movementRepository.save(movement);

        savingTwo = new Saving();
        savingTwo.setPrimaryOwner(owner);
        savingTwo.setBalance(new Money(BigDecimal.valueOf(1000)));
        savingTwo.setPenaltyFee(BigDecimal.ZERO);
        savingTwo.setMinimumBalance(BigDecimal.ZERO);
        savingTwo.setInterestRate(BigDecimal.valueOf(0.3));
        savingTwo.setCreationDate(LocalDate.now());
        savingRepository.save(savingTwo);

        movement = new Movement();
        movement.setTransferAmount(BigDecimal.TEN);
        movement.setBalanceBefore(BigDecimal.ZERO);
        movement.setBalanceAfter(BigDecimal.valueOf(10));
        movement.setMovementType(MovementType.CREATED);
        movement.setOrderDate(LocalDate.now());
        movement.setAccount(savingTwo);
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
        savingRepository.deleteAll();
        ownerRepository.deleteAll();
        addressRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void store_UnprocessedEntity_InvalidBodyAtValidInputDTO() throws Exception {

        savingDTO.setDateOfBirth(null);
        String body = objectMapper.writeValueAsString(savingDTO);
        mockMvc.perform(post("/accounts/savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void store_UnprocessedEntity_InvalidBodyWithSecondaryOwnerData() throws Exception {

        savingDTO.setDateOfBirthTwo(null);
        String body = objectMapper.writeValueAsString(savingDTO);
        mockMvc.perform(post("/accounts/savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());

        savingDTO.setDateOfBirthTwo(LocalDate.of(1969, 9, 25));
        savingDTO.setDirectionTwo("");
        body = objectMapper.writeValueAsString(savingDTO);
        mockMvc.perform(post("/accounts/savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void store_Created_ValidBodyWithSecondaryOwnerData() throws Exception {

        userRepository.deleteAll();
        roleRepository.deleteAll();

        String body = objectMapper.writeValueAsString(savingDTO);
        MvcResult mvcResult = mockMvc.perform(post("/accounts/savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("mailingAddress@email.com"));
        assertEquals(3, ownerRepository.findAll().size());

        body = objectMapper.writeValueAsString(savingDTO);
        mvcResult = mockMvc.perform(post("/accounts/savings")
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
        savingRepository.deleteAll();
        ownerRepository.deleteAll();
        MvcResult mvcResult = mockMvc.perform(get("/accounts/savings").with(httpBasic("Michael Douglas", "123456")))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getAll_ReturnSavingList_AccountsInDatabase() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/accounts/savings").with(httpBasic("Michael Douglas", "123456")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains(""+saving.getId()+""));
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains(""+savingTwo.getId()+""));
    }

    @Test
    void getSaving_NoFound_AccountNotExits() throws Exception {

        mockMvc.perform(get("/accounts/savings/0").with(httpBasic("Andres Iniesta", "123456")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSaving_Forbidden_AccountExits() throws Exception {

        mockMvc.perform(get("/accounts/savings/"+saving.getId()).with(httpBasic("Andres Iniesta", "123456")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getSaving_ReturnSaving_AccountExits() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/accounts/savings/"+saving.getId()).with(httpBasic("Michael Douglas", "123456")))
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains(""+saving.getId()+""));
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("Michael Douglas"));
    }

    @Test
    void createMovement_UnprocessedEntity_NullTransferAmount() throws Exception {

        movementDTO.setTransferAmount(null);
        String body = objectMapper.writeValueAsString(movementDTO);
        mockMvc.perform(post("/accounts/savings/"+saving.getId()+"/movements")
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
        mockMvc.perform(post("/accounts/savings/"+saving.getId()+"/movements")
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
        mockMvc.perform(post("/accounts/savings/0/movements")
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
        mockMvc.perform(post("/accounts/savings/"+saving.getId()+"/movements")
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
        mockMvc.perform(post("/accounts/savings/"+saving.getId()+"/movements")
                        .with(httpBasic("Michael Douglas", "123456"))
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createMovement_Created_ValidationOk_WithPenaltyFee() throws Exception {

        movementDTO.setTransferAmount(BigDecimal.valueOf(1));
        String body = objectMapper.writeValueAsString(movementDTO);
        MvcResult mvcResult = mockMvc.perform(post("/accounts/savings/"+saving.getId()+"/movements")
                        .with(httpBasic("Michael Douglas", "123456"))
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("1"));
        assertEquals(saving.getBalance().getAmount().subtract(BigDecimal.valueOf(1).add(BigDecimal.valueOf(3))),
                savingRepository.findById(saving.getId()).get().getBalance().getAmount());
    }

    @Test
    void createMovement_Created_ValidationOk_WithoutPenaltyFee() throws Exception {

        movementDTO.setTransferAmount(BigDecimal.valueOf(1));
        String body = objectMapper.writeValueAsString(movementDTO);
        MvcResult mvcResult = mockMvc.perform(post("/accounts/savings/"+savingTwo.getId()+"/movements")
                        .with(httpBasic("Michael Douglas", "123456"))
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("1"));
        assertEquals(savingTwo.getBalance().getAmount().subtract(BigDecimal.valueOf(1)),
                savingRepository.findById(savingTwo.getId()).get().getBalance().getAmount());
    }

    @Test
    void getMovements_isForbidden_AccountExistsWithMovements() throws Exception {

        mockMvc.perform(get("/accounts/savings/"+saving.getId()+"/movements")
                        .with(httpBasic("Andrés Iniesta", "123456"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void getMovements_isNotFound_AccountExistsWithMovements() throws Exception {

        mockMvc.perform(get("/accounts/savings/0/movements")
                        .with(httpBasic("Michael Douglas", "123456"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void getMovements_isOk_AccountExistsWithMovements() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/accounts/savings/"+saving.getId()+"/movements")
                        .with(httpBasic("Michael Douglas", "123456"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("CREATED"));
    }
}