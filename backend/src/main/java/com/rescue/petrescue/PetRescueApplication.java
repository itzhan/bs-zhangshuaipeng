package com.rescue.petrescue;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.rescue.petrescue.mapper")
public class PetRescueApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetRescueApplication.class, args);
    }
}
