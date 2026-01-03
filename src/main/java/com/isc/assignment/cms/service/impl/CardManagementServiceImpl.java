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

    /**
     * Registers a new card for an existing customer.
     * <p>
     * This method first retrieves the customer using the national code provided
     * in {@link RegisterNewCardRequestDto}. If no matching customer is found,
     * a {@link BusinessException} is thrown.
     * </p>
     *
     * <p>
     * After successful customer retrieval, a {@link Card} entity is created from
     * the request data and persisted via {@code cardService.registerCard(Card, Customer)}.
     * If the card is marked as active, the card information is also stored in the cache.
     * </p>
     *
     * @param registerNewCardRequest the DTO containing card registration data;
     *                               must not be {@code null}
     * @throws BusinessException if no customer exists for the provided national code
     */
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
