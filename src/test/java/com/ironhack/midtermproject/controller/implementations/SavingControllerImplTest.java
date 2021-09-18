package com.ironhack.midtermproject.controller.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.midtermproject.controller.dto.SavingDTO;
import com.ironhack.midtermproject.model.Address;
import com.ironhack.midtermproject.model.Owner;
import com.ironhack.midtermproject.repository.AddressRepository;
import com.ironhack.midtermproject.repository.MovementRepository;
import com.ironhack.midtermproject.repository.OwnerRepository;
import com.ironhack.midtermproject.repository.SavingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
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

    SavingDTO savingDTO;
    Owner primaryOwner;
    Owner secondaryOwner;
    Address address;
    Address addressTwo;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        address = new Address();
        address.setDirection("direction");
        address.setLocation("location");
        address.setCity("city");
        address.setCountry("country");
        address.setMailingAddress("mailingAddress@email.com");
        address.setCreationDate(LocalDate.now());
        address.setModificationDate(LocalDate.of(1, 1, 1));
        addressTwo = new Address();
        addressTwo.setDirection("direction");
        addressTwo.setLocation("location");
        addressTwo.setCity("city");
        addressTwo.setCountry("country");
        addressTwo.setMailingAddress("mailingAddress2@email.com");
        addressTwo.setCreationDate(LocalDate.now());
        addressTwo.setModificationDate(LocalDate.of(1, 1, 1));
        addressRepository.saveAll(List.of(address, addressTwo));

        primaryOwner = new Owner();
        primaryOwner.setAddress(address);
        primaryOwner.setName("Michael Douglas");
        primaryOwner.setDateOfBirth(LocalDate.of(1988, 9, 21));
        primaryOwner.setCreationDate(LocalDate.now());
        primaryOwner.setModificationDate(LocalDate.of(1,1,1));
        secondaryOwner = new Owner();
        secondaryOwner.setAddress(address);
        secondaryOwner.setName("Catherine Z Jones");
        secondaryOwner.setDateOfBirth(LocalDate.of(1988, 9, 21));
        secondaryOwner.setCreationDate(LocalDate.now());
        secondaryOwner.setModificationDate(LocalDate.of(1,1,1));
        ownerRepository.saveAll(List.of(primaryOwner, secondaryOwner));

        savingDTO = new SavingDTO();
        savingDTO.setAmount(BigDecimal.TEN);
        savingDTO.setSecretKey("123456");
        savingDTO.setMinimumBalance(BigDecimal.valueOf(100));
        savingDTO.setName("Michael Douglas");
        //savingDTO.setDateOfBirth(LocalDate.of(1944, 9, 25));
        savingDTO.setDirection("direction");
        savingDTO.setLocation("location");
        savingDTO.setCity("city");
        savingDTO.setCountry("country");
        savingDTO.setMailingAddress("mailingAddress@email.com");
        savingDTO.setNameTwo("Catherine Z Jones");
        //savingDTO.setDateOfBirthTwo(LocalDate.of(1969, 9, 25));
        savingDTO.setDirectionTwo("direction");
        savingDTO.setLocationTwo("location");
        savingDTO.setCityTwo("city");
        savingDTO.setCountryTwo("country");
        savingDTO.setMailingAddressTwo("mailingAddress2@email.com");
    }

    @AfterEach
    void tearDown() {
        savingRepository.deleteAll();
        ownerRepository.deleteAll();
        addressRepository.deleteAll();
        movementRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void store_UnprocessedEntity_InvalidBodyAtValidInputDTO() throws Exception {

        String body = objectMapper.writeValueAsString(savingDTO);
        mockMvc.perform(post("/accounts/savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

/*    @Test
    @WithMockUser(roles = "ADMIN")
    void store_UnprocessedEntity_InvalidBodyWithSecondaryOwnerData() throws Exception {

        savingDTO.setDateOfBirth(LocalDate.of(2000,1, 1));
*//*        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        objectMapper.setDateFormat(df);*//*
        String body = objectMapper.writeValueAsString(savingDTO);
        mockMvc.perform(post("/accounts/savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }*/
}