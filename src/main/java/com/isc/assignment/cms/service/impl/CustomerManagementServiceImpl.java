package com.isc.assignment.cms.service.impl;

import com.isc.assignment.cms.model.dto.CardInfoDto;
import com.isc.assignment.cms.model.dto.CustomerInfoDto;
import com.isc.assignment.cms.model.dto.RegisterNewCustomerRequestDto;
import com.isc.assignment.cms.model.entity.Customer;
import com.isc.assignment.cms.service.api.CardService;
import com.isc.assignment.cms.service.api.CmsCacheService;
import com.isc.assignment.cms.service.api.CustomerManagementService;
import com.isc.assignment.cms.service.api.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerManagementServiceImpl implements CustomerManagementService {

    private final CustomerService customerService;
    private final CardService cardService;
    private final CmsCacheService cmsCacheService;

    private final Logger logger = LoggerFactory.getLogger(CustomerManagementServiceImpl.class);

    public CustomerManagementServiceImpl(CustomerService customerService,
                                         CardService cardService,
                                         CmsCacheService cmsCacheService) {

        this.customerService = customerService;
        this.cardService = cardService;
        this.cmsCacheService = cmsCacheService;
    }

    @Override
    public void registerNewCustomer(RegisterNewCustomerRequestDto registerNewCustomer) {

        Customer customer = new Customer.Builder()
                .fname(registerNewCustomer.getFname())
                .lname(registerNewCustomer.getLname())
                .address(registerNewCustomer.getAddress())
                .nationalCode(registerNewCustomer.getNationalCode())
                .phoneNumber(registerNewCustomer.getPhoneNumber())
                .build();
        customerService.getOrRegisterCustomer(customer);
    }

    @Override
    public Set<CardInfoDto> getCardsOfCustomer(String customerNationalCode) {

        Set<CardInfoDto> cardsOfCustomer = cmsCacheService.getCardsOfCustomer(customerNationalCode);
        if (cardsOfCustomer.isEmpty()) {
            logger.info("data not found in cache so reading from database started...");
            Set<CardInfoDto> cardInfos = cardService.getActiveCardsOfCustomer(customerNationalCode).stream()
                    .map(card ->
                            new CardInfoDto.Builder()
                                    .number(card.getNumber())
                                    .issuerCode(card.getIssuerCode())
                                    .issuerName(card.getIssuerName())
                                    .type(card.getType())
                                    .accountNumber(card.getAccountNumber())
                                    .active(card.getActive())
                                    .expireDate(card.getExpireDate())
                                    .build()).collect(Collectors.toSet());

            CustomerInfoDto customerInfo = new CustomerInfoDto.Builder()
                    .nationalCode(customerNationalCode)
                    .build();
            cardInfos.forEach(cardInfo ->
                cmsCacheService.updateCardInfo(customerInfo, cardInfo));
            return cardInfos;
        }
        return cardsOfCustomer;
    }
}
