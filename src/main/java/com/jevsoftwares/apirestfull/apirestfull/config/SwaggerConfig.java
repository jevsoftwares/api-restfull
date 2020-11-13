package com.jevsoftwares.apirestfull.apirestfull.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket armazemApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.jevsoftwares.apirestfull.apirestfull"))
                .paths(regex("/api.*"))
                .build()
                .apiInfo(metaInfo());

    }

    private ApiInfo metaInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "Empresa de Armazenamento de Bebidas",
                "Api RestFull para gestão de armazém de bebidas. ",
                "1.0",
                "Termo de licença",
                new Contact("Jayson Mattoso Mareco",
                        "https://www.jevsoftwares.com.br",
                        "jevsoftwares@gmail.com"),
                "Apache License Version 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0.html",
                new ArrayList<VendorExtension>()
        );

        return apiInfo;
    }
}
