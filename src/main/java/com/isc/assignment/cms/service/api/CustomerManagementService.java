package com.isc.assignment.cms.service.api;

import com.isc.assignment.cms.model.dto.CardInfoDto;
import com.isc.assignment.cms.model.dto.RegisterNewCustomerRequestDto;

import java.util.Set;

public interface CustomerManagementService {

    void registerNewCustomer(RegisterNewCustomerRequestDto registerNewCustomer);

    Set<CardInfoDto> getCardsOfCustomer(String userNationalCode);
}
