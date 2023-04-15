package com.zgc.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ServletComponentScan
@MapperScan("com.zgc.app.mapper")
public class ReggieSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieSystemApplication.class, args);
    }
    @Bean
    public RestTemplate setRestTemplate(){
        return new RestTemplate();
    }
}
