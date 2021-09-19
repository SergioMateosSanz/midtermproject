package com.ironhack.midtermproject.controller.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ironhack.midtermproject.controller.dto.SavingDTO;
import com.ironhack.midtermproject.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    SavingDTO savingDTO;

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

        String body = objectMapper.writeValueAsString(savingDTO);
        MvcResult mvcResult = mockMvc.perform(post("/accounts/savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("mailingAddress@email.com"));
        assertEquals(2, ownerRepository.findAll().size());

        body = objectMapper.writeValueAsString(savingDTO);
        mvcResult = mockMvc.perform(post("/accounts/savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("mailingAddress@email.com"));
        assertEquals(2, ownerRepository.findAll().size());
    }
}