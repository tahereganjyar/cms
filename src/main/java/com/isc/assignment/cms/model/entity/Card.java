package com.isc.assignment.cms.model.entity;

import com.isc.assignment.cms.model.enums.CardTypeEnum;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "CARD",
        indexes = {
                @Index(
                        name = "idx_card_cus_id_active",
                        columnList = "CUS_ID, CDACT"
                )
        }
)
public class Card {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CDNUM", length = 16, nullable = false)
    private String number;

    @Column(name = "CDISC", length = 6, nullable = false)
    private String issuerCode;

    @Column(name = "CDISN")
    private String issuerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "CDTYP", nullable = false)
    private CardTypeEnum type;

    @Column(name = "CDEXP")
    private String expireDate;

    @Column(name = "CDANU", length = 10, nullable = false)
    private String accountNumber;

    @Column(name = "CDACT")
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "CUS_ID")
    private Customer customer;

    @Column(name = "CDSER", length = 3, nullable = false)
    private String serial;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return serial.equals(card.serial);
    }

    @Override
    public int hashCode() {

        return Objects.hash(serial);
    }

    private Card(Builder builder) {

        this.accountNumber = builder.accountNumber;
        this.expireDate = builder.expireDate;
        this.id = builder.id;
        this.isActive = builder.isActive;
        this.issuerCode = builder.issuerCode;
        this.number = builder.number;
        this.type = builder.type;
        this.issuerName = builder.issuerName;
        this.customer = builder.customer;
        this.serial = builder.serial;
    }

    public static class Builder {

        private Long id;
        private String number;
        private String issuerCode;
        private String issuerName;
        private CardTypeEnum type;
        private String expireDate;
        private String accountNumber;
        private Boolean isActive;
        private Customer customer;
        private String serial;


        public Builder id(Long id) {

            this.id = id;
            return this;
        }

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

        public Builder isActive(Boolean isActive) {

            this.isActive = isActive;
            return this;
        }


        public Builder customer(Customer customer) {

            this.customer = customer;
            return this;
        }

        public Builder serial(String serial) {

            this.serial = serial;
            return this;
        }

        public Card build() {

            return new Card(this);
        }
    }

    public Long getId() {

        return id;
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

    public Customer getCustomer() {

        return customer;
    }

    public String getSerial() {

        return serial;
    }
}
