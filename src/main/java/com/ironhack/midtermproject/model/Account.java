package com.ironhack.midtermproject.model;

import com.ironhack.midtermproject.classes.Money;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DynamicUpdate
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Embedded
    private Money balance;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner primaryOwner;
    @ManyToOne
    @JoinColumn(name = "other_owner_id")
    private Owner otherOwner;

    private BigDecimal penaltyFee;
    private LocalDate creationDate;
    private LocalDate modificationDate;

    @OneToMany(mappedBy = "account")
    private List<Movement> movementList;

    public Account() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }

    public Owner getPrimaryOwner() {
        return primaryOwner;
    }

    public void setPrimaryOwner(Owner primaryOwner) {
        this.primaryOwner = primaryOwner;
    }

    public Owner getOtherOwner() {
        return otherOwner;
    }

    public void setOtherOwner(Owner otherOwner) {
        this.otherOwner = otherOwner;
    }

    public BigDecimal getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(BigDecimal penaltyFee) {
        this.penaltyFee = penaltyFee;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDate modificationDate) {
        this.modificationDate = modificationDate;
    }

    public List<Movement> getMovementList() {
        return movementList;
    }

    public void setMovementList(List<Movement> movementList) {
        this.movementList = movementList;
    }
}
