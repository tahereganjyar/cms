package com.isc.assignment.cms.model.dto;

import com.isc.assignment.cms.model.enums.CardTypeEnum;

public class RegisterNewCardRequestDto {

    private final String nationalCode;
    private final String number;
    private final String issuerCode;
    private final String issuerName;
    private final CardTypeEnum type;
    private final String expireDate;
    private final String accountNumber;
    private final Boolean isActive;
    private final String serial;

    public RegisterNewCardRequestDto(String nationalCode,
                                     String number,
                                     String issuerCode,
                                     String issuerName,
                                     CardTypeEnum type,
                                     String expireDate,
                                     String accountNumber,
                                     Boolean isActive,
                                     String serial) {

        this.nationalCode = nationalCode;
        this.number = number;
        this.issuerCode = issuerCode;
        this.issuerName = issuerName;
        this.type = type;
        this.expireDate = expireDate;
        this.accountNumber = accountNumber;
        this.isActive = isActive;
        this.serial = serial;
    }

    public String getNationalCode() {

        return nationalCode;
    }

    public String getNumber() {

        return number;
    }

    public String getIssuerCode() {

        return issuerCode;
    }

    public String getIssuerName() {

        return issuerName;
    }

    public CardTypeEnum getType() {

        return type;
    }

    public String getExpireDate() {

        return expireDate;
    }

    public String getAccountNumber() {

        return accountNumber;
    }

    public Boolean getActive() {

        return isActive;
    }

    public String getSerial() {

        return serial;
    }
}