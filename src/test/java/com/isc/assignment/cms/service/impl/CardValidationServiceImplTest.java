package com.isc.assignment.cms.service.impl;

import com.isc.assignment.cms.common.BusinessException;
import com.isc.assignment.cms.repository.CardRepository;
import com.isc.assignment.cms.service.api.CardValidationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
class CardValidationServiceImplTest {

    private CardValidationService cardValidationService;

    @Mock
    private CardRepository cardRepository;


    @Test
    void givenNewSerial_whenCheckCardRegisteredBefore_thenReturnSuccessfully() {

        //Given
        String cardSerial = "123";
        cardValidationService = new CardValidationServiceImpl(cardRepository);

        //When
        Mockito.when(cardRepository.countBySerial(cardSerial)).thenReturn(0L);
        cardValidationService.checkCardRegisteredBefore(cardSerial);

        //Then
        Assertions.assertTrue(Boolean.TRUE);
    }

    @Test
    void givenRegisteredSerial_whenCheckCardRegisteredBefore_thenThrowException() {

        //Given
        String cardSerial = "123";
        cardValidationService = new CardValidationServiceImpl(cardRepository);

        //When
        Mockito.when(cardRepository.countBySerial(cardSerial)).thenReturn(1L);
        BusinessException ex =
                assertThrows(BusinessException.class,
                        () -> cardValidationService.checkCardRegisteredBefore(cardSerial));

        //Then
        assertThat(ex.getMessage())
                .isEqualTo("باشماره سریال ارسالی قبلا کارتی ثبت شده است");
    }
}