package com.isc.assignment.cms.service.api;

import com.isc.assignment.cms.model.dto.CardInfoDto;
import com.isc.assignment.cms.model.dto.CustomerInfoDto;

import java.util.Set;

public interface CmsCacheService {

    Set<CardInfoDto> getCardsOfCustomer(String customerNationalCode);

    void updateCardInfo(CustomerInfoDto customerInfo, CardInfoDto cardInfo);
}
