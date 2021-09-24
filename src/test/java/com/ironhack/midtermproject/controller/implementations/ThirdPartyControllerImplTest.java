package com.ironhack.midtermproject.controller.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ironhack.midtermproject.classes.Money;
import com.ironhack.midtermproject.controller.dto.TransferMoneyDTO;
import com.ironhack.midtermproject.enums.AccountStatus;
import com.ironhack.midtermproject.model.*;
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
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ThirdPartyControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    SavingRepository savingRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CreditCardRepository creditCardRepository;

    @Autowired
    CheckingRepository checkingRepository;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    Account account;
    TransferMoneyDTO transferMoneyDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        account = new Account();
        account.setBalance(new Money(BigDecimal.TEN));
        account.setPenaltyFee(BigDecimal.ONE);
        account.setCreationDate(LocalDate.now());
        account.setModificationDate(LocalDate.of(1, 1, 1));
        accountRepository.save(account);

        transferMoneyDTO = new TransferMoneyDTO();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void sendMoney_Forbidden_NoThirdPartyUser() throws Exception {

        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        mockMvc.perform(post("/thirdparty/sendmoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "THIRD PARTY")
    void sendMoney_UnprocessedEntity_NullBody() throws Exception {

        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        mockMvc.perform(post("/thirdparty/sendmoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "THIRD PARTY")
    void sendMoney_NoFound_AccountNotExits() throws Exception {

        transferMoneyDTO.setAmount(BigDecimal.ONE);
        transferMoneyDTO.setAccountId(-1);
        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        mockMvc.perform(post("/thirdparty/sendmoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "THIRD PARTY")
    void sendMoney_Created_ValidBody() throws Exception {

        transferMoneyDTO.setAmount(BigDecimal.ONE);
        transferMoneyDTO.setAccountId(account.getId());
        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        MvcResult mvcResult = mockMvc.perform(post("/thirdparty/sendmoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("1"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(""+account.getId()+""));
    }

    @Test
    @WithMockUser(roles = "THIRD PARTY")
    void sendMoney_UnprocessedEntity_SavingAccountFrozen() throws Exception {

        Saving saving = new Saving();
        saving.setBalance(new Money(BigDecimal.valueOf(1000)));
        saving.setMinimumBalance(BigDecimal.ZERO);
        saving.setInterestRate(BigDecimal.valueOf(0.3));
        saving.setStatus(AccountStatus.FROZEN);
        saving.setCreationDate(LocalDate.now());
        savingRepository.save(saving);

        transferMoneyDTO.setAmount(BigDecimal.ONE);
        transferMoneyDTO.setAccountId(saving.getId());
        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        mockMvc.perform(post("/thirdparty/sendmoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "THIRD PARTY")
    void sendMoney_UnprocessedEntity_CheckingAccountFrozen() throws Exception {

        Checking checking = new Checking();
        checking.setBalance(new Money(BigDecimal.valueOf(250)));
        checking.setPenaltyFee(BigDecimal.valueOf(3));
        checking.setMinimumBalance(BigDecimal.valueOf(250));
        checking.setMonthlyMaintenanceFee(BigDecimal.valueOf(0.3));
        checking.setStatus(AccountStatus.FROZEN);
        checking.setCreationDate(LocalDate.now());
        checkingRepository.save(checking);

        transferMoneyDTO.setAmount(BigDecimal.ONE);
        transferMoneyDTO.setAccountId(checking.getId());
        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        mockMvc.perform(post("/thirdparty/sendmoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "THIRD PARTY")
    void sendMoney_UnprocessedEntity_StudentAccountFrozen() throws Exception {

        Student student = new Student();
        student.setBalance(new Money(BigDecimal.TEN));
        student.setPenaltyFee(BigDecimal.valueOf(3));
        student.setStatus(AccountStatus.ACTIVE);
        student.setStatus(AccountStatus.FROZEN);
        student.setCreationDate(LocalDate.now());
        studentRepository.save(student);

        transferMoneyDTO.setAmount(BigDecimal.ONE);
        transferMoneyDTO.setAccountId(student.getId());
        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        mockMvc.perform(post("/thirdparty/sendmoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void receiveMoney_Forbidden_NoThirdPartyUser() throws Exception {

        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        mockMvc.perform(post("/thirdparty/receivemoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "THIRD PARTY")
    void receiveMoney_UnprocessedEntity_NullBody() throws Exception {

        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        mockMvc.perform(post("/thirdparty/receivemoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "THIRD PARTY")
    void receiveMoney_NoFound_AccountNotExits() throws Exception {

        transferMoneyDTO.setAmount(BigDecimal.ONE);
        transferMoneyDTO.setAccountId(-1);
        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        mockMvc.perform(post("/thirdparty/receivemoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "THIRD PARTY")
    void receiveMoney_Created_ValidBody_SavingAccountWithoutPenalty() throws Exception {

        Saving saving = new Saving();
        saving.setBalance(new Money(BigDecimal.valueOf(1000)));
        saving.setMinimumBalance(BigDecimal.ZERO);
        saving.setInterestRate(BigDecimal.valueOf(0.3));
        saving.setStatus(AccountStatus.ACTIVE);
        saving.setCreationDate(LocalDate.now());
        savingRepository.save(saving);

        transferMoneyDTO.setAmount(BigDecimal.ONE);
        transferMoneyDTO.setAccountId(saving.getId());
        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        MvcResult mvcResult = mockMvc.perform(post("/thirdparty/receivemoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("1"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(""+saving.getId()+""));
        assertEquals(BigDecimal.valueOf(999).setScale(2), savingRepository.findById(saving.getId()).get().getBalance().getAmount());
    }

    @Test
    @WithMockUser(roles = "THIRD PARTY")
    void receiveMoney_Created_ValidBody_SavingAccountWithPenalty() throws Exception {

        Saving saving = new Saving();
        saving.setBalance(new Money(BigDecimal.valueOf(100)));
        saving.setPenaltyFee(BigDecimal.valueOf(3));
        saving.setMinimumBalance(BigDecimal.valueOf(100));
        saving.setInterestRate(BigDecimal.valueOf(0.3));
        saving.setStatus(AccountStatus.ACTIVE);
        saving.setCreationDate(LocalDate.now());
        savingRepository.save(saving);

        transferMoneyDTO.setAmount(BigDecimal.ONE);
        transferMoneyDTO.setAccountId(saving.getId());
        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        MvcResult mvcResult = mockMvc.perform(post("/thirdparty/receivemoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("1"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(""+saving.getId()+""));
        assertEquals(BigDecimal.valueOf(96).setScale(2), savingRepository.findById(saving.getId()).get().getBalance().getAmount());
    }

    @Test
    @WithMockUser(roles = "THIRD PARTY")
    void receiveMoney_UnprocessedEntity_SavingAccountFrozen() throws Exception {

        Saving saving = new Saving();
        saving.setBalance(new Money(BigDecimal.valueOf(1000)));
        saving.setMinimumBalance(BigDecimal.ZERO);
        saving.setInterestRate(BigDecimal.valueOf(0.3));
        saving.setStatus(AccountStatus.FROZEN);
        saving.setCreationDate(LocalDate.now());
        savingRepository.save(saving);

        transferMoneyDTO.setAmount(BigDecimal.ONE);
        transferMoneyDTO.setAccountId(saving.getId());
        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        mockMvc.perform(post("/thirdparty/receivemoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "THIRD PARTY")
    void receiveMoney_Created_ValidBody_CheckingAccountWithoutPenalty() throws Exception {

        Checking checking = new Checking();
        checking.setBalance(new Money(BigDecimal.valueOf(1000)));
        checking.setPenaltyFee(BigDecimal.valueOf(3));
        checking.setMinimumBalance(BigDecimal.valueOf(250));
        checking.setStatus(AccountStatus.ACTIVE);
        checking.setMonthlyMaintenanceFee(BigDecimal.valueOf(0.3));
        checking.setCreationDate(LocalDate.now());
        checkingRepository.save(checking);

        transferMoneyDTO.setAmount(BigDecimal.ONE);
        transferMoneyDTO.setAccountId(checking.getId());
        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        MvcResult mvcResult = mockMvc.perform(post("/thirdparty/receivemoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("1"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(""+checking.getId()+""));
        assertEquals(BigDecimal.valueOf(999).setScale(2), checkingRepository.findById(checking.getId()).get().getBalance().getAmount());
    }

    @Test
    @WithMockUser(roles = "THIRD PARTY")
    void receiveMoney_Created_ValidBody_CheckingAccountWithPenalty() throws Exception {

        Checking checking = new Checking();
        checking.setBalance(new Money(BigDecimal.valueOf(250)));
        checking.setPenaltyFee(BigDecimal.valueOf(3));
        checking.setMinimumBalance(BigDecimal.valueOf(250));
        checking.setMonthlyMaintenanceFee(BigDecimal.valueOf(0.3));
        checking.setStatus(AccountStatus.ACTIVE);
        checking.setCreationDate(LocalDate.now());
        checkingRepository.save(checking);

        transferMoneyDTO.setAmount(BigDecimal.ONE);
        transferMoneyDTO.setAccountId(checking.getId());
        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        MvcResult mvcResult = mockMvc.perform(post("/thirdparty/receivemoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("1"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(""+checking.getId()+""));
        assertEquals(BigDecimal.valueOf(246).setScale(2), checkingRepository.findById(checking.getId()).get().getBalance().getAmount());
    }

    @Test
    @WithMockUser(roles = "THIRD PARTY")
    void receiveMoney_UnprocessedEntity_CheckingAccountFrozen() throws Exception {

        Checking checking = new Checking();
        checking.setBalance(new Money(BigDecimal.valueOf(250)));
        checking.setPenaltyFee(BigDecimal.valueOf(3));
        checking.setMinimumBalance(BigDecimal.valueOf(250));
        checking.setMonthlyMaintenanceFee(BigDecimal.valueOf(0.3));
        checking.setStatus(AccountStatus.FROZEN);
        checking.setCreationDate(LocalDate.now());
        checkingRepository.save(checking);

        transferMoneyDTO.setAmount(BigDecimal.ONE);
        transferMoneyDTO.setAccountId(checking.getId());
        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        mockMvc.perform(post("/thirdparty/receivemoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "THIRD PARTY")
    void receiveMoney_Created_ValidBody_StudentAccount() throws Exception {

        Student student = new Student();
        student.setBalance(new Money(BigDecimal.TEN));
        student.setPenaltyFee(BigDecimal.valueOf(3));
        student.setStatus(AccountStatus.ACTIVE);
        student.setCreationDate(LocalDate.now());
        studentRepository.save(student);

        transferMoneyDTO.setAmount(BigDecimal.ONE);
        transferMoneyDTO.setAccountId(student.getId());
        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        MvcResult mvcResult = mockMvc.perform(post("/thirdparty/receivemoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("1"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(""+student.getId()+""));
        assertEquals(BigDecimal.valueOf(9).setScale(2), studentRepository.findById(student.getId()).get().getBalance().getAmount());
    }

    @Test
    @WithMockUser(roles = "THIRD PARTY")
    void receiveMoney_UnprocessedEntity_StudentAccountFrozen() throws Exception {

        Student student = new Student();
        student.setBalance(new Money(BigDecimal.TEN));
        student.setPenaltyFee(BigDecimal.valueOf(3));
        student.setStatus(AccountStatus.ACTIVE);
        student.setStatus(AccountStatus.FROZEN);
        student.setCreationDate(LocalDate.now());
        studentRepository.save(student);

        transferMoneyDTO.setAmount(BigDecimal.ONE);
        transferMoneyDTO.setAccountId(student.getId());
        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        mockMvc.perform(post("/thirdparty/receivemoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @WithMockUser(roles = "THIRD PARTY")
    void receiveMoney_Created_ValidBody_CreditCardAccount() throws Exception {

        CreditCard creditCard = new CreditCard();
        creditCard.setBalance(new Money(BigDecimal.TEN));
        creditCard.setPenaltyFee(BigDecimal.valueOf(3));
        creditCard.setCreditLimit(BigDecimal.ZERO);
        creditCard.setInterestRate(BigDecimal.valueOf(0.3));
        creditCard.setCreationDate(LocalDate.now());
        creditCardRepository.save(creditCard);

        transferMoneyDTO.setAmount(BigDecimal.ONE);
        transferMoneyDTO.setAccountId(creditCard.getId());
        String body = objectMapper.writeValueAsString(transferMoneyDTO);
        MvcResult mvcResult = mockMvc.perform(post("/thirdparty/receivemoney")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                )
                .andExpect(status().isCreated())
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("1"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains(""+creditCard.getId()+""));
        assertEquals(BigDecimal.valueOf(9).setScale(2), creditCardRepository.findById(creditCard.getId()).get().getBalance().getAmount());
    }
}