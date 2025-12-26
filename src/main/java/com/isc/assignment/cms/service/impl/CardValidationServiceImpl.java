package com.isc.assignment.cms.service.impl;

import com.isc.assignment.cms.repository.CardRepository;
import com.isc.assignment.cms.service.api.CardValidationService;
import org.springframework.stereotype.Service;

@Service
public class CardValidationServiceImpl implements CardValidationService {

    private final CardRepository cardRepository;

    public CardValidationServiceImpl(CardRepository cardRepository) {

        this.cardRepository = cardRepository;
    }

    @Override
    public void checkCardRegisteredBefore(String cardSerial) {

        long countCardsBySerial = cardRepository.countBySerial(cardSerial);
        if (countCardsBySerial != 0) {

            throw new RuntimeException("card has registered before!!!");
        }
    }
}
