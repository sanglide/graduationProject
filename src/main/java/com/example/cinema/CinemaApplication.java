package com.example.cinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CinemaApplication {

    public static void main(String[] args) {

//        SpringApplication.run(CinemaApplication.class, args);
        SpringApplicationBuilder builder = new SpringApplicationBuilder(CinemaApplication.class);
        builder.headless(false).run(args);
    }

}
