package com.isc.assignment.cms.controller;


import com.isc.assignment.cms.model.dto.CardInfoDto;
import com.isc.assignment.cms.model.dto.RegisterNewCustomerRequestDto;
import com.isc.assignment.cms.service.api.CustomerManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "سرویس های مدیریت مشتری")
@RestController
@RequestMapping(value = "/api")
public class CustomerController {

    private final CustomerManagementService customerManagementService;

    public CustomerController(CustomerManagementService customerManagementService) {

        this.customerManagementService = customerManagementService;
    }

    @Operation(summary = "ثبت مشتری جدید",
            description = "این سرویس با دریافت اطلاعات مشتری آن را ثبت میکند")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "موفق بودن عملیات"),
            @ApiResponse(responseCode = "201", description = "یافت نشدن اطلاعات"),
            @ApiResponse(responseCode = "401", description = "خطای دسترسی غیرمجاز")
    })
    @PostMapping("/v1/customers")
    public void registerNewCustomer(@RequestBody RegisterNewCustomerRequestDto registerNewCustomer) {

        customerManagementService.registerNewCustomer(registerNewCustomer);
    }

    @Operation(summary = "بازیابی تمامی کارتهای یک مشتری بر اساس کد ملی",
            description = "این سرویس تمامی کارتهای مشتری(فعال/غیرفعال) را ابتدا از حافظه ی داخلی بازیابی میکند." +
                    " در صورت نبودن این اطلاعات، مشخصات کارتها از دیتابیس بازیابی میشود")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "موفق بودن عملیات"),
            @ApiResponse(responseCode = "201", description = "یافت نشدن اطلاعات"),
            @ApiResponse(responseCode = "401", description = "خطای دسترسی غیرمجاز")
    })
    @GetMapping("/v1/customers/{nationalCode}/cards")
    public Set<CardInfoDto> getCardsOfCustomer(@PathVariable String nationalCode) {

        return customerManagementService.getCardsOfCustomer(nationalCode);
    }
}
