package com.isc.assignment.cms.service.impl;

import com.isc.assignment.cms.common.BusinessException;
import com.isc.assignment.cms.model.entity.Card;
import com.isc.assignment.cms.model.entity.Customer;
import com.isc.assignment.cms.repository.CardRepository;
import com.isc.assignment.cms.service.api.CardService;
import com.isc.assignment.cms.service.api.CardValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardServiceImpl implements CardService {

    private final Object activeCardSaveLock = new Object();
    private final Object deactivatedCardSaveLock = new Object();

    private final CardRepository cardRepository;
    private final CardValidationService cardValidationService;

    private final Logger logger = LoggerFactory.getLogger(CardServiceImpl.class);


    public CardServiceImpl(CardRepository cardRepository,
                           CardValidationService cardValidationService) {

        this.cardRepository = cardRepository;
        this.cardValidationService = cardValidationService;
    }

    @Override
    public void registerCard(Card card, Customer customer) {

        if (Boolean.FALSE.equals(card.getActive())) {
            cardValidationService.checkCardRegisteredBefore(card.getSerial());
            synchronized (deactivatedCardSaveLock) {
                cardValidationService.checkCardRegisteredBefore(card.getSerial());
                cardRepository.save(card);
                return;
            }
        }
        long countOfActiveCards = cardRepository.countByCustomerIdAndIssuerCodeAndTypeAndIsActive(customer.getId(),
                card.getIssuerCode(),
                card.getType(),
                Boolean.TRUE);
        if (countOfActiveCards == 0) {
            synchronized (activeCardSaveLock) {
                cardValidationService.checkCardRegisteredBefore(card.getSerial());
                long antherCountOfActiveCards = cardRepository.countByCustomerIdAndIssuerCodeAndTypeAndIsActive(customer.getId(),
                        card.getIssuerCode(),
                        card.getType(),
                        Boolean.TRUE);
                if (antherCountOfActiveCards == 0) {
                    cardRepository.save(card);
                }
            }
        } else {
            logger.error("can not save redandant card in database");
            throw new BusinessException("برای مشتری موردنظر قبلا کارت فعالی از این نوع و از این صادر کننده ثبت شده است");
        }
    }

    @Override
    public List<Card> getActiveCardsOfCustomer(String customerNationalCode) {

        return cardRepository.findAllByCustomerNationalCodeAndIsActive(customerNationalCode, Boolean.TRUE);
    }
}
