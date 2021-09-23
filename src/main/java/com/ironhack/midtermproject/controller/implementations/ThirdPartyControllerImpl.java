package com.ironhack.midtermproject.controller.implementations;

import com.ironhack.midtermproject.controller.dto.TransferMoneyDTO;
import com.ironhack.midtermproject.controller.interfaces.ThirdPartyController;
import com.ironhack.midtermproject.service.interfaces.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ThirdPartyControllerImpl implements ThirdPartyController {

    @Autowired
    ThirdPartyService thirdPartyService;

    @Override
    @PostMapping("/thirdparty/sendmoney")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferMoneyDTO sendMoney(@RequestBody TransferMoneyDTO transferMoneyDTO) {

        return thirdPartyService.sendMoney(transferMoneyDTO);
    }

    @Override
    @PostMapping("/thirdparty/receivemoney")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferMoneyDTO receiveMoney(@RequestBody TransferMoneyDTO transferMoneyDTO) {

        return thirdPartyService.receiveMoney(transferMoneyDTO);
    }
}
