package com.shift.akrpc.consumer;

import com.shift.akrpc.common.config.RpcConsumerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {RpcConsumerProperties.class})
public class AkrpcConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AkrpcConsumerApplication.class, args);
    }

}
