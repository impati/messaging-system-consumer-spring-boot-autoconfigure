package com.example.impati.autoconfigure;

import com.example.impati.messaging_system_consumer.core.ChannelRegistration;
import com.example.impati.messaging_system_consumer.core.ClientRegister;
import com.example.impati.messaging_system_consumer.core.MessagingSystemPoller;
import jakarta.annotation.PostConstruct;

public class ConsumerInitializer {

    private final ClientRegister clientRegister;
    private final ChannelRegistration channelRegistration;
    private final SpringMessagingSystemConsumerProperties properties;
    private final MessagingSystemPoller messagingSystemPoller;

    public ConsumerInitializer(ClientRegister clientRegister,
                               ChannelRegistration channelRegistration,
                               SpringMessagingSystemConsumerProperties properties,
                               MessagingSystemPoller messagingSystemPoller) {
        this.clientRegister = clientRegister;
        this.channelRegistration = channelRegistration;
        this.properties = properties;
        this.messagingSystemPoller = messagingSystemPoller;
    }

    @PostConstruct
    void init() {
        clientRegister.register(properties.clientName());
        channelRegistration.getChannels().values().forEach(messagingSystemPoller::listen);
    }
}
