package com.isc.assignment.cms.service.impl;

import com.isc.assignment.cms.service.api.CardService;
import com.isc.assignment.cms.service.api.CardValidationService;
import com.isc.assignment.cms.service.api.CmsCacheService;
import com.isc.assignment.cms.service.api.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

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
}