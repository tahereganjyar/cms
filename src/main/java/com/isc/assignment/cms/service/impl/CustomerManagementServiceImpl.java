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

    /**
     * Registers a new customer in cms.
     * <p>
     * This method creates a {@link Customer} instance using the data provided
     * in {@link RegisterNewCustomerRequestDto} and delegates the persistence logic
     * to {@code customerService.getOrRegisterCustomer(Customer)}.
     * </p>
     *
     * <p>
     * If a customer with the same national code already exists, the existing
     * customer will be returned or reused according to the business logic
     * implemented in {@code customerService}.
     * </p>
     *
     * @param registerNewCustomer the DTO containing customer registration data;
     *                            must not be {@code null}
     */
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

    /**
     * Retrieves the active cards of a customer identified by the given national code.
     * <p>
     * This method first attempts to fetch the customer's card information from the cache.
     * If no data is found in the cache, it queries the database for the customer's active cards,
     * converts them into {@link CardInfoDto} objects, updates the cache, and returns the result.
     * </p>
     *
     * <p>
     * Cache lookup strategy:
     * <ul>
     *   <li>Read card data from cache using the customer's national code</li>
     *   <li>If cache miss occurs, read active card data from the database</li>
     *   <li>Populate the cache with the retrieved card information</li>
     * </ul>
     * </p>
     *
     * @param customerNationalCode the national code of the customer whose cards are to be retrieved;
     *                             must not be {@code null}
     * @return a set of {@link CardInfoDto} representing the customer's active cards;
     * never {@code null}
     */
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
