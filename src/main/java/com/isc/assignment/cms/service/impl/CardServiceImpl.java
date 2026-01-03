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

    /**
     * Registers a card for a given customer while enforcing business constraints.
     * <p>
     * This method ensures that:
     * <ul>
     *   <li>No duplicate card (by serial) is registered in the system</li>
     *   <li>A customer can have at most one active card per issuer and card type</li>
     * </ul>
     * </p>
     *
     * <p>
     * Registration rules:
     * <ul>
     *   <li>If the card is inactive, it is saved immediately after duplicate validation</li>
     *   <li>If the card is active, the system checks that no other active card exists
     *       for the same customer, issuer, and card type</li>
     * </ul>
     * </p>
     *
     * <p>
     * To prevent race conditions in concurrent environments, synchronization locks
     * are used when persisting both active and inactive cards.
     * </p>
     *
     * @param card the card entity to be registered; must not be {@code null}
     * @param customer the customer who owns the card; must not be {@code null}
     *
     * @throws BusinessException if a duplicate card is detected or if an active card
     *                           of the same type and issuer already exists for the customer
     */
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

    /**
     * Retrieves all active cards associated with a customer identified by national code.
     *
     * @param customerNationalCode the national code of the customer whose active cards
     *                             are to be retrieved; must not be {@code null}
     * @return a list of active {@link Card} entities belonging to the specified customer;
     *         never {@code null}
     */
    @Override
    public List<Card> getActiveCardsOfCustomer(String customerNationalCode) {

        return cardRepository.findAllByCustomerNationalCodeAndIsActive(customerNationalCode, Boolean.TRUE);
    }
}
