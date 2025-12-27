package com.isc.assignment.cms.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterNewCustomerRequestDto {

    @NotBlank(message = "شماره ملی مشتری باید مقدار داشته باشد")
    @Size(min = 10, max = 10, message = "شماره ملی مشتری باید ده رقم باشد")
    private final String nationalCode;

    private final String phoneNumber;
    private final String address;
    private final String fname;
    private final String lname;

    public RegisterNewCustomerRequestDto(String nationalCode,
                                         String phoneNumber,
                                         String address,
                                         String fname,
                                         String lname) {

        this.nationalCode = nationalCode;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.fname = fname;
        this.lname = lname;
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
