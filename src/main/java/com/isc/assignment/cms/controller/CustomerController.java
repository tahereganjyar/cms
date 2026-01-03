package com.isc.assignment.cms.controller;


import com.isc.assignment.cms.model.dto.CardInfoDto;
import com.isc.assignment.cms.model.dto.RegisterNewCustomerRequestDto;
import com.isc.assignment.cms.service.api.CustomerManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "سرویس های مدیریت مشتری")
@RestController
@RequestMapping(value = "/api/v1/customers")
public class CustomerController {

    private final CustomerManagementService customerManagementService;

    public CustomerController(CustomerManagementService customerManagementService) {

        this.customerManagementService = customerManagementService;
    }

    @Operation(summary = "ثبت مشتری جدید",
            description = "این سرویس با دریافت اطلاعات مشتری آن را ثبت میکند")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "موفق بودن عملیات"),
            @ApiResponse(responseCode = "500", description = "خطای ناشناخته"),
            @ApiResponse(responseCode = "400", description = "خطای نامعتبر بودن داده های ارسالی"),
            @ApiResponse(responseCode = "401", description = "خطای دسترسی غیرمجاز")
    })
    @PostMapping
    public ResponseEntity<ResponseDto> registerNewCustomer(@Valid @RequestBody RegisterNewCustomerRequestDto registerNewCustomer) {

        customerManagementService.registerNewCustomer(registerNewCustomer);
        ResponseDto<Boolean> response = new ResponseDto<>();
        response.setResult(Boolean.TRUE);
        response.setMessage("ثبت مشتری جدید با موفقیت انجام شد");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "بازیابی تمامی کارتهای یک مشتری بر اساس کد ملی",
            description = "این سرویس تمامی کارتهای مشتری(فعال/غیرفعال) را ابتدا از حافظه ی داخلی بازیابی میکند." +
                    " در صورت نبودن این اطلاعات، مشخصات کارتها از دیتابیس بازیابی میشود")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "موفق بودن عملیات"),
            @ApiResponse(responseCode = "500", description = "خطای ناشناخته"),
            @ApiResponse(responseCode = "400", description = "خطای نامعتبر بودن داده های ارسالی"),
            @ApiResponse(responseCode = "204", description = "خالی بودن نتیجه"),
            @ApiResponse(responseCode = "401", description = "خطای دسترسی غیرمجاز")
    })
    @GetMapping("{nationalCode}/cards")
    public ResponseEntity<ResponseDto> getCardsOfCustomer(@PathVariable String nationalCode) {

        Set<CardInfoDto> result = customerManagementService.getCardsOfCustomer(nationalCode);
        ResponseDto<Set<CardInfoDto>> response = new ResponseDto<>();
        response.setResult(result);
        if (result.isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
