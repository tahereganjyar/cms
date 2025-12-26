package com.isc.assignment.cms.service.impl;

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
                        throw new RuntimeException("فرمت داده های فایل اطلاعات اولیه صحیح نیست");
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
            throw new RuntimeException("خطا در خواندن فایل داده های اولیه");
        }
    }

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
