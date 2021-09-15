package com.ironhack.midtermproject.service.interfaces;

import com.ironhack.midtermproject.controller.dto.ThirdPartyDTOInput;
import com.ironhack.midtermproject.controller.dto.ThirdPartyDTOOutput;

public interface UserService {

    ThirdPartyDTOOutput addThirdParty(ThirdPartyDTOInput thirdPartyDTOInput);
    void deleteThirdParty(Long id);
}
