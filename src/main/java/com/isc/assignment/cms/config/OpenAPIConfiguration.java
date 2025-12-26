package com.isc.assignment.cms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("سرویس های مدیریت کارت")
                        .version("2.1.4")
                        .description("این سرویس ها جهت ثبت کارت و مشتری، اطلاعات کارت های مشتری و بازیابی مشخصات کارت استفاده میشود "));
    }
}
