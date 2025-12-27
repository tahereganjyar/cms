package com.isc.assignment.cms.service.impl;

import com.isc.assignment.cms.common.BusinessException;
import com.isc.assignment.cms.model.dto.CardInfoDto;
import com.isc.assignment.cms.model.dto.CustomerInfoDto;
import com.isc.assignment.cms.model.dto.RegisterNewCardRequestDto;
import com.isc.assignment.cms.model.entity.Card;
import com.isc.assignment.cms.model.entity.Customer;
import com.isc.assignment.cms.repository.CustomerRepository;
import com.isc.assignment.cms.service.api.CardManagementService;
import com.isc.assignment.cms.service.api.CardService;
import com.isc.assignment.cms.service.api.CmsCacheService;
import org.springframework.stereotype.Service;

@Service
public class CardManagementServiceImpl implements CardManagementService {

    private final CmsCacheService cmsCacheService;
    private final CardService cardService;
    private final CustomerRepository customerRepository;

    public CardManagementServiceImpl(CmsCacheService cmsCacheService,
                                     CardService cardService,
                                     CustomerRepository customerRepository) {

        this.cmsCacheService = cmsCacheService;
        this.cardService = cardService;
        this.customerRepository = customerRepository;
    }

    @Override
    public void registerNewCard(RegisterNewCardRequestDto registerNewCardRequest) {

        Customer customer = customerRepository.findByNationalCode(registerNewCardRequest.getNationalCode())
                .orElseThrow(() -> new BusinessException("برای کدملی ارسالی ، هیج مشتری ایی یافت نشد"));
        Card card = new Card.Builder()
                .accountNumber(registerNewCardRequest.getAccountNumber())
                .expireDate(registerNewCardRequest.getExpireDate())
                .isActive(registerNewCardRequest.getActive())
                .number(registerNewCardRequest.getNumber())
                .issuerCode(registerNewCardRequest.getIssuerCode())
                .issuerName(registerNewCardRequest.getIssuerName())
                .type(registerNewCardRequest.getType())
                .customer(customer)
                .serial(registerNewCardRequest.getSerial())
                .build();
        CustomerInfoDto customerInfo = new CustomerInfoDto.Builder()
                .phoneNumber(customer.getPhoneNumber())
                .nationalCode(customer.getNationalCode())
                .lname(customer.getLname())
                .fname(customer.getFname())
                .address(customer.getAddress())
                .build();
        CardInfoDto cardInfo = new CardInfoDto.Builder()
                .number(card.getNumber())
                .issuerCode(card.getIssuerCode())
                .issuerName(card.getIssuerName())
                .type(card.getType())
                .accountNumber(card.getAccountNumber())
                .active(card.getActive())
                .expireDate(card.getExpireDate())
                .serial(card.getSerial())
                .build();
        cardService.registerCard(card, customer);
        if (Boolean.TRUE.equals(cardInfo.getActive())) {
            cmsCacheService.updateCardInfo(customerInfo, cardInfo);
        }
    }
}
