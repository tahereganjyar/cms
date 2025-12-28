package com.isc.assignment.cms.service.impl;

import com.isc.assignment.cms.model.dto.CardInfoDto;
import com.isc.assignment.cms.service.api.CardService;
import com.isc.assignment.cms.service.api.CardValidationService;
import com.isc.assignment.cms.service.api.CmsCacheService;
import com.isc.assignment.cms.service.api.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(OutputCaptureExtension.class)
class CmsCacheServiceImplTest {


    private CmsCacheService cmsCacheService;

    @InjectMocks
    private CardService cardService;

    @InjectMocks
    private CustomerService customerService;

    @InjectMocks
    private CardValidationService cardValidationService;


    @Test
    void givenInitFileWithWrongFormat_whenInitiateDataInCacheAndDatabase_thenLogErrorMessage(CapturedOutput output) {

        //Given
        CardService cardServiceMock = Mockito.mock(CardService.class);
        CustomerService customerServiceMock = Mockito.mock(CustomerService.class);
        CardValidationService cardValidationServiceMock = Mockito.mock(CardValidationService.class);

        //When
        cmsCacheService = new CmsCacheServiceImpl("E:\\java-workspace\\cms\\cms\\src\\test\\resources\\init-invalid-data.txt",
                cardServiceMock,
                customerServiceMock,
                cardValidationServiceMock);

        long count = output.getOut()
                .lines()
                .filter(line -> line.contains("فرمت داده های فایل اطلاعات اولیه صحیح نیست"))
                .count();

        //Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void givenInitFileWithDeactivatedCards_whenInitiateDataInCacheAndDatabase_thenJustSaveInDatabase() {

        //Given
        CardService cardServiceMock = Mockito.mock(CardService.class);
        CustomerService customerServiceMock = Mockito.mock(CustomerService.class);
        CardValidationService cardValidationServiceMock = Mockito.mock(CardValidationService.class);

        //When
        Mockito.doNothing().when(cardValidationServiceMock).checkCardRegisteredBefore(Mockito.any());
        Mockito.when(customerServiceMock.getOrRegisterCustomer(Mockito.any())).thenReturn(Mockito.any());

        cmsCacheService = new CmsCacheServiceImpl("E:\\java-workspace\\cms\\cms\\src\\test\\resources\\init-deactivated-cards-data.txt",
                cardServiceMock,
                customerServiceMock,
                cardValidationServiceMock);

        //Then
        verify(cardServiceMock, times(2))
                .registerCard(Mockito.any(), Mockito.any());
    }

    @Test
    void givenInitFileWithActiveCards_whenInitiateDataInCacheAndDatabase_thenSaveInDatabaseAndCache() {

        //Given
        CardService cardServiceMock = Mockito.mock(CardService.class);
        CustomerService customerServiceMock = Mockito.mock(CustomerService.class);
        CardValidationService cardValidationServiceMock = Mockito.mock(CardValidationService.class);

        //When
        Mockito.doNothing().when(cardValidationServiceMock).checkCardRegisteredBefore(Mockito.any());
        Mockito.when(customerServiceMock.getOrRegisterCustomer(Mockito.any())).thenReturn(Mockito.any());

        cmsCacheService = new CmsCacheServiceImpl("E:\\java-workspace\\cms\\cms\\src\\test\\resources\\init-active-cards-data.txt",
                cardServiceMock,
                customerServiceMock,
                cardValidationServiceMock);

        //Then
        verify(cardServiceMock, times(3))
                .registerCard(Mockito.any(), Mockito.any());
        Set<CardInfoDto> cardsOfCustomer = cmsCacheService.getCardsOfCustomer("1111111111");
        CardInfoDto cardInfoDto1 = new CardInfoDto.Builder().serial("123").build();
        CardInfoDto cardInfoDto2 = new CardInfoDto.Builder().serial("456").build();
        Assertions.assertEquals(2, cardsOfCustomer.size());
        Assertions.assertTrue(cardsOfCustomer.contains(cardInfoDto1));
        Assertions.assertTrue(cardsOfCustomer.contains(cardInfoDto2));
    }
}