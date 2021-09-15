package com.ironhack.midtermproject.controller.interfaces;


import com.ironhack.midtermproject.controller.dto.ThirdPartyDTOInput;
import com.ironhack.midtermproject.controller.dto.ThirdPartyDTOOutput;

public interface UserController {

    ThirdPartyDTOOutput addThirdParty(ThirdPartyDTOInput thirdPartyDTOInput);
    void deleteThirdParty(Long id);
}
