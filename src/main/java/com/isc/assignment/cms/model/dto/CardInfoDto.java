package com.isc.assignment.cms.model.dto;

import com.isc.assignment.cms.model.enums.CardTypeEnum;

import java.util.Objects;

public class CardInfoDto {

    private String number;
    private String issuerCode;
    private String issuerName;
    private CardTypeEnum type;
    private String expireDate;
    private String accountNumber;
    private Boolean isActive;
    private String serial;


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardInfoDto that = (CardInfoDto) o;
        return serial.equals(that.serial);
    }

    @Override
    public int hashCode() {

        return Objects.hash(serial);
    }

    public CardInfoDto(Builder builder) {

        this.number = builder.number;
        this.issuerCode = builder.issuerCode;
        this.issuerName = builder.issuerName;
        this.type = builder.type;
        this.expireDate = builder.expireDate;
        this.accountNumber = builder.accountNumber;
        this.isActive = builder.isActive;
        this.serial = builder.serial;
    }

    public static class Builder {

        private String number;
        private String issuerCode;
        private String issuerName;
        private CardTypeEnum type;
        private String expireDate;
        private String accountNumber;
        private Boolean isActive;
        private String serial;

        public Builder number(String number) {

            this.number = number;
            return this;
        }

        public Builder issuerCode(String issuerCode) {

            this.issuerCode = issuerCode;
            return this;
        }

        public Builder issuerName(String issuerName) {

            this.issuerName = issuerName;
            return this;
        }

        public Builder type(CardTypeEnum type) {

            this.type = type;
            return this;
        }

        public Builder expireDate(String expireDate) {

            this.expireDate = expireDate;
            return this;
        }

        public Builder accountNumber(String accountNumber) {

            this.accountNumber = accountNumber;
            return this;
        }

        public Builder active(Boolean active) {

            this.isActive = active;
            return this;
        }

        public Builder serial(String serial) {

            this.serial = serial;
            return this;
        }

        public CardInfoDto build() {

            return new CardInfoDto(this);
        }
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

    @Override
    public String toString() {

        return "CardInfoDto{" +
                "number='" + number + '\'' +
                ", issuerCode='" + issuerCode + '\'' +
                ", issuerName='" + issuerName + '\'' +
                ", type=" + type +
                ", expireDate='" + expireDate + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
