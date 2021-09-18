package com.ironhack.midtermproject.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

public class SavingDTO {

    private int id;
    private Currency currency;
    private BigDecimal amount;
    private BigDecimal penaltyFee;
    private String secretKey;
    private BigDecimal minimumBalance;
    private BigDecimal interestRate;

    private String name;
    private LocalDate dateOfBirth;

    private String direction;
    private String location;
    private String city;
    private String country;
    private String mailingAddress;

    private String nameTwo;
    private LocalDate dateOfBirthTwo;

    private String directionTwo;
    private String locationTwo;
    private String cityTwo;
    private String countryTwo;
    private String mailingAddressTwo;

    public SavingDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(BigDecimal penaltyFee) {
        this.penaltyFee = penaltyFee;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getNameTwo() {
        return nameTwo;
    }

    public void setNameTwo(String nameTwo) {
        this.nameTwo = nameTwo;
    }

    public LocalDate getDateOfBirthTwo() {
        return dateOfBirthTwo;
    }

    public void setDateOfBirthTwo(LocalDate dateOfBirthTwo) {
        this.dateOfBirthTwo = dateOfBirthTwo;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(String mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public String getDirectionTwo() {
        return directionTwo;
    }

    public void setDirectionTwo(String directionTwo) {
        this.directionTwo = directionTwo;
    }

    public String getLocationTwo() {
        return locationTwo;
    }

    public void setLocationTwo(String locationTwo) {
        this.locationTwo = locationTwo;
    }

    public String getCityTwo() {
        return cityTwo;
    }

    public void setCityTwo(String cityTwo) {
        this.cityTwo = cityTwo;
    }

    public String getCountryTwo() {
        return countryTwo;
    }

    public void setCountryTwo(String countryTwo) {
        this.countryTwo = countryTwo;
    }

    public String getMailingAddressTwo() {
        return mailingAddressTwo;
    }

    public void setMailingAddressTwo(String mailingAddressTwo) {
        this.mailingAddressTwo = mailingAddressTwo;
    }

}
