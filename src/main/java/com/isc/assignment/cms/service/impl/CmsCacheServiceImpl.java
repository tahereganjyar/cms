package com.isc.assignment.cms.service.impl;

import com.isc.assignment.cms.common.BusinessException;
import com.isc.assignment.cms.config.AsyncConfiguration;
import com.isc.assignment.cms.model.dto.CardInfoDto;
import com.isc.assignment.cms.model.dto.CustomerInfoDto;
import com.isc.assignment.cms.model.entity.Card;
import com.isc.assignment.cms.model.entity.Customer;
import com.isc.assignment.cms.model.enums.CardTypeEnum;
import com.isc.assignment.cms.service.api.CardService;
import com.isc.assignment.cms.service.api.CardValidationService;
import com.isc.assignment.cms.service.api.CmsCacheService;
import com.isc.assignment.cms.service.api.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CmsCacheServiceImpl implements CmsCacheService {

    private final Logger logger = LoggerFactory.getLogger(CmsCacheServiceImpl.class);

    private final Map<CustomerInfoDto, Set<CardInfoDto>> cmsCache;

    private final CardService cardService;
    private final CustomerService customerService;
    private final CardValidationService cardValidationService;

    /**
     * Initializes the CMS cache service and loads initial customer and card data from a file.
     * <p>
     * This constructor reads an external data file (configured via {@code data.file.path})
     * and uses its contents to populate both the database and the in-memory cache.
     * Each line of the file represents customer and card information and is processed
     * in parallel for improved performance.
     * </p>
     *
     * <p>
     * Initialization rules:
     * <ul>
     *   <li>Each line must contain exactly 13 comma-separated values</li>
     *   <li>Inactive cards are persisted only in the database</li>
     *   <li>Active cards are validated to prevent duplicates per customer,
     *       issuer, and card type</li>
     *   <li>Only valid active cards are stored in the in-memory cache</li>
     * </ul>
     * </p>
     *
     * <p>
     * If a duplicate active card is detected for the same customer, issuer, and card type,
     * the record is ignored and logged as redundant.
     * </p>
     *
     * <p>
     * Any errors occurring while reading individual lines are logged and do not
     * interrupt the initialization process. A fatal error during file access
     * results in a {@link BusinessException}.
     * </p>
     *
     * @param datafilePath the path to the initialization data file
     * @param cardService the service responsible for card persistence and retrieval
     * @param customerService the service responsible for customer persistence and retrieval
     * @param cardValidationService the service used to validate card registration rules
     *
     * @throws BusinessException if the initialization file cannot be read
     */
    public CmsCacheServiceImpl(@Value("${data.file.path}") String datafilePath,
                               CardService cardService,
                               CustomerService customerService,
                               CardValidationService cardValidationService) {

        this.cardService = cardService;
        this.customerService = customerService;
        this.cardValidationService = cardValidationService;
        cmsCache = new ConcurrentHashMap<>();
        try (Stream<String> lines = Files.lines(Path.of(datafilePath))) {
            lines.parallel().forEach(row -> {

                try {
                    Object[] rowSplits = row.split(",");
                    if (rowSplits.length != 13) {
                        logger.error("فرمت داده های فایل اطلاعات اولیه صحیح نیست");
                        return;
                    }
                    CustomerInfoDto customerInfo = getCustomerInfo(rowSplits);
                    CardInfoDto cardInfo = getCardInfoDto(rowSplits);
                    if (Boolean.FALSE.equals(cardInfo.getActive())) {
                        registerCardToDatabase(customerInfo, cardInfo);
                        return;
                    }
                    Set<CardInfoDto> cardInfos = Optional.ofNullable(cmsCache.get(customerInfo)).stream()
                            .flatMap(Collection::stream)
                            .filter(card -> card.getActive() == Boolean.TRUE &&
                                    card.getIssuerCode().equals(cardInfo.getIssuerCode()) &&
                                    card.getType().getCode() == cardInfo.getType().getCode())
                            .collect(Collectors.toSet());
                    if (cardInfos.isEmpty()) {
                        registerCardToDatabase(customerInfo, cardInfo);
                        registerCardToCache(customerInfo, cardInfo);
                    } else {
                        logger.error("redundant data");
                    }
                } catch (Exception e) {
                    logger.error("an error in reading file for initializing data:", e);
                }
            });
        } catch (Exception e) {
            throw new BusinessException("خطا در خواندن فایل داده های اولیه");
        }
    }

    /**
     * Retrieves the cached cards of a customer by national code.
     * <p>
     * This method looks up the customer's cards in the in-memory cache. If no cards
     * are found for the given customer, an empty set is returned. The returned set
     * is unmodifiable to prevent external modification of the cache.
     * </p>
     *
     * @param customerNationalCode the national code of the customer whose cards
     *                             are to be retrieved; must not be {@code null}
     * @return an unmodifiable set of {@link CardInfoDto} objects representing the
     *         customer's cards; never {@code null}, empty if no cards are found
     */
    @Override
    public Set<CardInfoDto> getCardsOfCustomer(String customerNationalCode) {

        CustomerInfoDto customer = new CustomerInfoDto.Builder()
                .nationalCode(customerNationalCode)
                .build();
        Set<CardInfoDto> cardInfos = cmsCache.get(customer);
        if (cardInfos == null) {

            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(cardInfos);
    }

    /**
     * Asynchronously updates the in-memory cache with the given card information
     * for a specific customer.
     * <p>
     * This method delegates to {@link #registerCardToCache(CustomerInfoDto, CardInfoDto)}
     * to add or update the card info in the cache. The operation is executed
     * asynchronously using the executor defined by {@code AsyncConfiguration.EXECUTOR_NAME}.
     * </p>
     *
     * @param customerInfo the customer information associated with the card;
     *                     must not be {@code null}
     * @param cardInfo the card information to update in the cache;
     *                 must not be {@code null}
     */
    @Async(AsyncConfiguration.EXECUTOR_NAME)
    @Override
    public void updateCardInfo(CustomerInfoDto customerInfo, CardInfoDto cardInfo) {

        registerCardToCache(customerInfo, cardInfo);
    }

    private void registerCardToCache(CustomerInfoDto customerInfo, CardInfoDto cardInfo) {

        cmsCache.compute(customerInfo, (key, value) -> {
            if (value == null) {
                Set<CardInfoDto> newValue = new HashSet<>();
                newValue.add(cardInfo);
                return newValue;
            } else {
                if (Boolean.TRUE.equals(cardInfo.getActive())) {
                    Set<CardInfoDto> cardInfos = value.stream().filter(cardInfoDto -> cardInfoDto.getActive() == Boolean.TRUE &&
                            cardInfoDto.getIssuerCode().equals(cardInfo.getIssuerCode()) &&
                            cardInfoDto.getType() == cardInfo.getType()).collect(Collectors.toSet());
                    if (cardInfos.isEmpty()) {
                        value.add(cardInfo);
                    } else {
                        logger.error("Unable to add extra card. Every user must have almost 2 cards. user :{} and card : {}",
                                customerInfo.getNationalCode(), cardInfo.getNumber());
                    }
                }
                return value;
            }
        });
    }

    /**
     * Persists a card and its associated customer to the database.
     * <p>
     * This method performs the following steps:
     * <ol>
     *   <li>Validates that the card serial number is not already registered using
     *       {@link CardValidationService#checkCardRegisteredBefore(String)}</li>
     *   <li>Builds a {@link Customer} entity from the provided {@link CustomerInfoDto}</li>
     *   <li>Registers the customer if not already existing via
     *       {@link CustomerService#getOrRegisterCustomer(Customer)}</li>
     *   <li>Builds a {@link Card} entity from the provided {@link CardInfoDto}</li>
     *   <li>Registers the card for the customer using {@link CardService#registerCard(Card, Customer)}</li>
     * </ol>
     * </p>
     *
     * @param customerInfoDto the DTO containing customer information; must not be {@code null}
     * @param cardInfoDto the DTO containing card information; must not be {@code null}
     *
     * @throws BusinessException if the card serial is already registered
     */
    private void registerCardToDatabase(CustomerInfoDto customerInfoDto, CardInfoDto cardInfoDto) {

        cardValidationService.checkCardRegisteredBefore(cardInfoDto.getSerial());
        Customer customer = new Customer.Builder()
                .phoneNumber(customerInfoDto.getPhoneNumber())
                .nationalCode(customerInfoDto.getNationalCode())
                .address(customerInfoDto.getAddress())
                .lname(customerInfoDto.getLname())
                .fname(customerInfoDto.getFname())
                .build();
        final Customer registeredCustomer = customerService.getOrRegisterCustomer(customer);
        Card card = new Card.Builder()
                .customer(registeredCustomer)
                .isActive(cardInfoDto.getActive())
                .issuerName(cardInfoDto.getIssuerName())
                .issuerCode(cardInfoDto.getIssuerCode())
                .number(cardInfoDto.getNumber())
                .expireDate(cardInfoDto.getExpireDate())
                .accountNumber(cardInfoDto.getAccountNumber())
                .type(cardInfoDto.getType())
                .serial(cardInfoDto.getSerial())
                .build();
        cardService.registerCard(card, registeredCustomer);
    }

    /**
     * Converts a row of raw data into a {@link CardInfoDto}.
     * <p>
     * This method extracts card-related fields from the provided array of objects,
     * parses the values as needed (e.g., card type, active status), and builds a
     * {@link CardInfoDto} instance using the builder pattern.
     * </p>
     *
     * <p>
     * Expected row format (columns indices):
     * <ul>
     *   <li>1: Card Number</li>
     *   <li>2: Issuer Code</li>
     *   <li>3: Issuer Name</li>
     *   <li>4: Card Type (as byte)</li>
     *   <li>9: Active flag (Boolean)</li>
     *   <li>10: Expiration Date</li>
     *   <li>11: Account Number</li>
     *   <li>12: Card Serial</li>
     * </ul>
     * </p>
     *
     * @param rowSplits the array of objects representing a row of card data;
     *                  must have at least 13 elements
     * @return a {@link CardInfoDto} built from the provided data
     * @throws NumberFormatException if the card type cannot be parsed as a byte
     */
    private CardInfoDto getCardInfoDto(Object[] rowSplits) {

        final String cardNumber = (String) rowSplits[1];
        final String issuerCode = (String) rowSplits[2];
        final String issuerName = (String) rowSplits[3];
        final byte cardType = Byte.parseByte(rowSplits[4].toString());
        final Boolean isActive = Boolean.valueOf(rowSplits[9].toString());
        final String expireDate = (String) rowSplits[10];
        final String accountNumber = (String) rowSplits[11];
        final String cardSerial = (String) rowSplits[12];
        return new CardInfoDto.Builder()
                .issuerName(issuerName)
                .active(isActive)
                .accountNumber(accountNumber)
                .expireDate(expireDate)
                .type(CardTypeEnum.fromCode(cardType))
                .issuerCode(issuerCode)
                .number(cardNumber)
                .serial(cardSerial)
                .build();
    }

    /**
     * Converts a row of raw data into a {@link CustomerInfoDto}.
     * <p>
     * This method extracts customer-related fields from the provided array of objects
     * and builds a {@link CustomerInfoDto} instance using the builder pattern.
     * </p>
     *
     * <p>
     * Expected row format (column indices):
     * <ul>
     *   <li>0: National Code</li>
     *   <li>5: First Name</li>
     *   <li>6: Last Name</li>
     *   <li>7: Address</li>
     *   <li>8: Phone Number</li>
     * </ul>
     * </p>
     *
     * @param rowSplits the array of objects representing a row of customer data;
     *                  must have at least 9 elements
     * @return a {@link CustomerInfoDto} built from the provided data
     */
    private CustomerInfoDto getCustomerInfo(Object[] rowSplits) {

        final String nationalCode = (String) rowSplits[0];
        final String fname = (String) rowSplits[5];
        final String lname = (String) rowSplits[6];
        final String address = (String) rowSplits[7];
        final String phoneNumber = (String) rowSplits[8];
        return new CustomerInfoDto.Builder()
                .address(address)
                .fname(fname)
                .lname(lname)
                .nationalCode(nationalCode)
                .phoneNumber(phoneNumber).build();
    }
}
