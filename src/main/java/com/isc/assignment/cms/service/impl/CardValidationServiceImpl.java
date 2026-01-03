package com.isc.assignment.cms.service.impl;

import com.isc.assignment.cms.common.BusinessException;
import com.isc.assignment.cms.repository.CardRepository;
import com.isc.assignment.cms.service.api.CardValidationService;
import org.springframework.stereotype.Service;

@Service
public class CardValidationServiceImpl implements CardValidationService {

    private final CardRepository cardRepository;

    public CardValidationServiceImpl(CardRepository cardRepository) {

        this.cardRepository = cardRepository;
    }

    /**
     * Verifies that no card with the given serial number is already registered.
     * <p>
     * This method checks the existence of a card by its serial number and prevents
     * duplicate card registration by throwing a business exception if a match is found.
     * </p>
     *
     * @param cardSerial the serial number of the card to be checked;
     *                   must not be {@code null}
     * @throws BusinessException if a card with the given serial number already exists
     */
    @Override
    public void checkCardRegisteredBefore(String cardSerial) {

        long countCardsBySerial = cardRepository.countBySerial(cardSerial);
        if (countCardsBySerial != 0) {

            throw new BusinessException("باشماره سریال ارسالی قبلا کارتی ثبت شده است");
        }
    }
}
