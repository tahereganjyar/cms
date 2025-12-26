package com.isc.assignment.cms.controller;


import com.isc.assignment.cms.model.dto.RegisterNewCardRequestDto;
import com.isc.assignment.cms.service.api.CardManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "سرویس های مدیریت کارت")
@RestController
@RequestMapping(value = "/api")
public class CardController {

    private final CardManagementService cardManagementService;

    public CardController(CardManagementService cardManagementService) {

        this.cardManagementService = cardManagementService;
    }

    @Operation(summary = "ثبت کارت جدید",
            description = "این سرویس اطلاعات کارت جدید(فعال/غیرفعال) را ذخیره میکند. " +
                    "باتوجه به اینکه مشصخصات مشتری همراه با اطلاعات کارت ارسال میشود" +
                    " در صورت عدم وجود مشتری موردنظر، خطای مناسب نمایش داده میشود")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "موفق بودن عملیات"),
            @ApiResponse(responseCode = "401", description = "خطای دسترسی غیرمجاز")
    })
    @PostMapping(value = "/v1/cards")
    public void registerNewCard(@RequestBody RegisterNewCardRequestDto registerNewCardRequest) {

         cardManagementService.registerNewCard(registerNewCardRequest);
    }
}
