package com.isc.assignment.cms.model.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "CUSTOMER",
        indexes = {
                @Index(
                        name = "idx_customer_usnid",
                        columnList = "USNID"
                )
        })
public class Customer {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USNID", length = 10, nullable = false)
    private String nationalCode;

    @Column(name = "USPH")
    private String phoneNumber;

    @Column(name = "USAD")
    private String address;

    @Column(name = "USFN")
    private String fname;

    @Column(name = "USLN")
    private String lname;


    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id.equals(customer.id) && nationalCode.equals(customer.nationalCode);
    }

    public Customer() {

    }

    @Override
    public int hashCode() {

        return Objects.hash(id, nationalCode);
    }

    private Customer(Builder builder) {

        this.address = builder.address;
        this.fname = builder.fname;
        this.lname = builder.lname;
        this.id = builder.id;
        this.nationalCode = builder.nationalCode;
        this.phoneNumber = builder.phoneNumber;
    }

    public static class Builder {

        private Long id;
        private String nationalCode;
        private String phoneNumber;
        private String address;
        private String fname;
        private String lname;

        public Builder id(Long id) {

            this.id = id;
            return this;
        }

        public Builder nationalCode(String nationalCode) {

            this.nationalCode = nationalCode;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {

            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder address(String address) {

            this.address = address;
            return this;
        }

        public Builder fname(String fname) {

            this.fname = fname;
            return this;
        }

        public Builder lname(String lname) {

            this.lname = lname;
            return this;
        }

        public Customer build() {

            return new Customer(this);
        }
    }

    public Long getId() {

        return id;
    }

    public String getNationalCode() {

        return nationalCode;
    }

    public String getPhoneNumber() {

        return phoneNumber;
    }

    public String getAddress() {

        return address;
    }

    public String getFname() {

        return fname;
    }

    public String getLname() {

        return lname;
    }
}
