package com.shift.akrpc.provider;

import com.shift.akrpc.common.annotation.EnableRpcProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRpcProvider
@SpringBootApplication
public class AkrpcProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AkrpcProviderApplication.class, args);
    }

}
