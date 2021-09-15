package com.ironhack.midtermproject.controller.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.midtermproject.controller.dto.ThirdPartyDTOInput;
import com.ironhack.midtermproject.model.Role;
import com.ironhack.midtermproject.model.User;
import com.ironhack.midtermproject.repository.RoleRepository;
import com.ironhack.midtermproject.repository.UserRepository;
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

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@SpringBootTest
class UserControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    ThirdPartyDTOInput thirdPartyDTOInput;
    User user;
    Role role;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        thirdPartyDTOInput = new ThirdPartyDTOInput();
        user = new User();
        role = new Role();
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addThirdParty_UnprocessedEntity_BodyNotAbleToProcess() throws Exception {

        thirdPartyDTOInput.setName("");
        thirdPartyDTOInput.setSharedKey("");
        String body = objectMapper.writeValueAsString(thirdPartyDTOInput);
        mockMvc.perform(post("/users")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addThirdParty_UnprocessedEntity_ResourceExists() throws Exception {

        user.setUsername("James");
        user.setPassword("123456");
        userRepository.save(user);
        role.setName("THIRD PARTY");
        role.setUser(user);
        roleRepository.save(role);

        thirdPartyDTOInput.setName("James");
        thirdPartyDTOInput.setSharedKey("123456");
        String body = objectMapper.writeValueAsString(thirdPartyDTOInput);
        mockMvc.perform(post("/users")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addThirdParty_Created_StoreUserToDatabase() throws Exception {

        thirdPartyDTOInput.setName("James");
        thirdPartyDTOInput.setSharedKey("123456");
        String body = objectMapper.writeValueAsString(thirdPartyDTOInput);
        MvcResult mvcResult = mockMvc.perform(post("/users")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8).contains("James"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteThirdParty_NotFound_UserNotExitsInDatabase() throws Exception {

        mockMvc.perform(delete("/users/0")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteThirdParty_NoContent_UserExits() throws Exception {

        user.setUsername("James");
        user.setPassword("123456");
        userRepository.save(user);
        role.setName("THIRD PARTY");
        role.setUser(user);
        roleRepository.save(role);

        mockMvc.perform(delete("/users/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());
        assertEquals(Optional.empty(), userRepository.findById(user.getId()));
    }
}