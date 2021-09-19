package com.ironhack.midtermproject.controller.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.midtermproject.controller.dto.AddressDTO;
import com.ironhack.midtermproject.model.Address;
import com.ironhack.midtermproject.repository.AddressRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AddressControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    AddressRepository addressRepository;

    AddressDTO addressDTO;
    Address address;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        addressDTO = new AddressDTO();
        address = new Address();
        address.setDirection("direction");
        address.setLocation("location");
        address.setCity("city");
        address.setCountry("country");
        address.setMailingAddress("email");
        address.setCreationDate(LocalDate.now());
        address.setModificationDate(LocalDate.of(1,1,1));
        addressRepository.save(address);
    }

    @AfterEach
    void tearDown() {
        addressRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_NotFound_AddressNotExits() throws Exception {

        String body = objectMapper.writeValueAsString(addressDTO);
        mockMvc.perform(patch("/addresses/0")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_NoContent_NewAddress() throws Exception {

        addressDTO.setDirection("new direction");
        addressDTO.setLocation("new location");
        addressDTO.setCity("new city");
        addressDTO.setCountry("Spain");
        addressDTO.setMailingAddress("email");
        String body = objectMapper.writeValueAsString(addressDTO);
        mockMvc.perform(patch("/addresses/" + address.getId())
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        assertEquals("Spain", addressRepository.findById(address.getId()).get().getCountry());
        assertEquals(LocalDate.now(), addressRepository.findById(address.getId()).get().getModificationDate());
    }

}