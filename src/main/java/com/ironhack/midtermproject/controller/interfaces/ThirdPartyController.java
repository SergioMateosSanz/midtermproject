package com.ironhack.midtermproject.controller.interfaces;

import com.ironhack.midtermproject.controller.dto.TransferMoneyDTO;

public interface ThirdPartyController {

    TransferMoneyDTO sendMoney(TransferMoneyDTO transferMoneyDTO);
    TransferMoneyDTO receiveMoney(TransferMoneyDTO transferMoneyDTO);
}
