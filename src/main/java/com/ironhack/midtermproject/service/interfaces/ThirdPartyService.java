package com.ironhack.midtermproject.service.interfaces;

import com.ironhack.midtermproject.controller.dto.TransferMoneyDTO;

public interface ThirdPartyService {

    TransferMoneyDTO sendMoney(TransferMoneyDTO transferMoneyDTO);
    TransferMoneyDTO receiveMoney(TransferMoneyDTO transferMoneyDTO);
}
