package com.kara.payflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PayflowInboundPipeline {

    public static void main(String[] args) {
        SpringApplication.run(PayflowInboundPipeline.class, args);
        System.out.println("PayFlow Inbound Pipeline initialized successfully!");
    }
}