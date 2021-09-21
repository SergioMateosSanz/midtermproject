package com.ironhack.midtermproject.model;

import com.ironhack.midtermproject.enums.AccountStatus;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@DynamicUpdate
public class Student extends Account {

    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private String secretKey;

    public Student() {
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
