package com.isc.assignment.cms.model.dto;

import java.util.Objects;

public class CustomerInfoDto {

    private String nationalCode;
    private String phoneNumber;
    private String address;
    private String fname;
    private String lname;

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerInfoDto that = (CustomerInfoDto) o;
        return nationalCode.equals(that.nationalCode);
    }

    @Override
    public int hashCode() {

        return Objects.hash(nationalCode);
    }

    public CustomerInfoDto(Builder builder) {

        this.nationalCode = builder.nationalCode;
        this.phoneNumber = builder.phoneNumber;
        this.address = builder.address;
        this.fname = builder.fname;
        this.lname = builder.lname;
    }

    public static class Builder {

        private String nationalCode;
        private String phoneNumber;
        private String address;
        private String fname;
        private String lname;

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

        public CustomerInfoDto build() {

            return new CustomerInfoDto(this);
        }
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
