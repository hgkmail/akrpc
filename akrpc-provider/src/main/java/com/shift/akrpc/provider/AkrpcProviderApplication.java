package com.shift.akrpc.provider;

import com.shift.akrpc.common.config.RpcDiscoveryProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(value = {RpcDiscoveryProperties.class})
public class AkrpcProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AkrpcProviderApplication.class, args);
    }

}
