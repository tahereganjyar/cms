package com.isc.assignment.cms.service.api;

import com.isc.assignment.cms.model.dto.RegisterNewCardRequestDto;

public interface CardManagementService {

    void registerNewCard(RegisterNewCardRequestDto registerNewCardRequest);
}
