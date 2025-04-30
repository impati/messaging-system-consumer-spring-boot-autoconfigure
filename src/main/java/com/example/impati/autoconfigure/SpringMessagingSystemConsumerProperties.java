package com.example.impati.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.messaging-system.consumer")
public record SpringMessagingSystemConsumerProperties(
        String url, String clientName
) {

}
