package com.ironhack.midtermproject.controller.dto;

public class ThirdPartyDTOInput {

    private String name;
    private String sharedKey;

    public ThirdPartyDTOInput() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSharedKey() {
        return sharedKey;
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
    }
}
