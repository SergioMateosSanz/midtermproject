package com.ironhack.midtermproject.controller.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.midtermproject.classes.Money;
import com.ironhack.midtermproject.model.Account;
import com.ironhack.midtermproject.model.Owner;
import com.ironhack.midtermproject.model.Role;
import com.ironhack.midtermproject.model.User;
import com.ironhack.midtermproject.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    OwnerRepository ownerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    Owner holder;
    Owner owner;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        User user = new User();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user);
        Role adminRole = new Role("ADMIN");
        adminRole.setUser(user);
        roleRepository.save(adminRole);

        User user2 = new User();
        user2.setUsername("holder");
        user2.setPassword(passwordEncoder.encode("123456"));
        userRepository.save(user2);
        Role contributorRole = new Role("HOLDER");
        contributorRole.setUser(user2);
        roleRepository.save(contributorRole);

        holder = new Owner();
        holder.setName("holder");
        holder.setDateOfBirth(LocalDate.of(2010, 10, 3));
        holder.setCreationDate(LocalDate.now());
        holder.setAddress(null);
        ownerRepository.save(holder);

        Account account = new Account();
        account.setBalance(new Money(BigDecimal.TEN));
        account.setPenaltyFee(BigDecimal.ONE);
        account.setCreationDate(LocalDate.now());
        account.setModificationDate(LocalDate.of(1, 1, 1));
        account.setPrimaryOwner(holder);
        accountRepository.save(account);

        owner = new Owner();
        owner.setName("Owner");
        owner.setDateOfBirth(LocalDate.of(2010, 10, 3));
        owner.setCreationDate(LocalDate.now());
        owner.setAddress(null);
        ownerRepository.save(owner);

        account = new Account();
        account.setBalance(new Money(BigDecimal.TEN));
        account.setPenaltyFee(BigDecimal.ONE);
        account.setCreationDate(LocalDate.now());
        account.setModificationDate(LocalDate.of(1, 1, 1));
        account.setPrimaryOwner(owner);
        accountRepository.save(account);

    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        ownerRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void getAllAccounts_ReturnEmptyJSON_NoAccounts_AdminLogged() throws Exception {

        accountRepository.deleteAll();
        MvcResult mvcResult = mockMvc.perform(get("/accounts").with(httpBasic("admin", "123456")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("[]"));
    }

    @Test
    void getAllAccounts_ReturnEmptyJSON_NoAccounts_HolderLogged() throws Exception {

        accountRepository.deleteAll();
        MvcResult mvcResult = mockMvc.perform(get("/accounts").with(httpBasic("holder", "123456")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("[]"));
    }

    @Test
    @Disabled
    void getAllAccounts_ReturnAccountsList_AdminLogged() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/accounts").with(httpBasic("admin", "123456")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains(""+owner.getId()+","));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(""+holder.getId()+","));
    }

    @Test
    @Disabled
    void getAllAccounts_ReturnAccountsList_HolderLogged() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/accounts").with(httpBasic("holder", "123456")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains(""+holder.getId()+""));
    }
}