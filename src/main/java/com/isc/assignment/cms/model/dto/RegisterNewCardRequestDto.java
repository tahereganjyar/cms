package com.isc.assignment.cms.model.dto;

import com.isc.assignment.cms.model.enums.CardTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterNewCardRequestDto {

    @NotBlank(message = "شماره ملی مشتری باید مقدار داشته باشد")
    @Size(min = 10, max = 10, message = "شماره ملی مشتری باید ده رقم باشد")
    private final String nationalCode;

    @NotBlank(message = "شماره کارت باید مقدار داشته باشد")
    @Size(min = 16, max = 16, message = "شماره کارت باید 16 رقم باشد")
    private final String number;

    @NotBlank(message = "کد صادرکننده کارت باید مقدار داشته باشد")
    @Size(min = 6, max = 6, message = "کد صادرکننده کارت باید 6 رقم باشد")
    private final String issuerCode;

    private final String issuerName;

    @NotNull(message = "نوع کارت باید مقدار داشته باشد")
    private final CardTypeEnum type;

    @NotBlank(message = "تاریخ انقضا کارت باید مقدار داشته باشد")
    @Size(min = 7, max = 7, message = "تاریخ انقضا کارت باید به فرمت yyyy/mm باشد")
    private final String expireDate;

    @NotBlank(message = "شماره حساب باید مقدار داشته باشد")
    @Size(min = 10, max = 10, message = "شماره حساب باید 10 رقم باشد")
    private final String accountNumber;

    private final Boolean active;

    @NotBlank(message = "سریال کارت باید مقدار داشته باشد")
    @Size(min = 3, max = 3, message = "سریال کارت باید 3 رقم باشد")
    private final String serial;

    public RegisterNewCardRequestDto(String nationalCode,
                                     String number,
                                     String issuerCode,
                                     String issuerName,
                                     CardTypeEnum type,
                                     String expireDate,
                                     String accountNumber,
                                     Boolean active,
                                     String serial) {

        this.nationalCode = nationalCode;
        this.number = number;
        this.issuerCode = issuerCode;
        this.issuerName = issuerName;
        this.type = type;
        this.expireDate = expireDate;
        this.accountNumber = accountNumber;
        this.active = active == null ? Boolean.FALSE : active;
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

        return active;
    }

    public String getSerial() {

        return serial;
    }
}