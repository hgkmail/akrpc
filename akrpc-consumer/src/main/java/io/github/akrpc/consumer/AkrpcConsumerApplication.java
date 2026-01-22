package io.github.akrpc.consumer;

import io.github.akrpc.common.annotation.EnableRpcProvider;
import io.github.akrpc.common.config.RpcConsumerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableRpcProvider
@SpringBootApplication
@EnableConfigurationProperties(RpcConsumerProperties.class)
public class AkrpcConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AkrpcConsumerApplication.class, args);
    }

}
