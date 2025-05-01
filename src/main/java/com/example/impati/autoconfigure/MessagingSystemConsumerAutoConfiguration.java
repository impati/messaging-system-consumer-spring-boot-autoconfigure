package com.example.impati.autoconfigure;

import com.example.impati.messaging_system_consumer.core.ChannelRegistration;
import com.example.impati.messaging_system_consumer.core.ChannelSubscriber;
import com.example.impati.messaging_system_consumer.core.ClientRegister;
import com.example.impati.messaging_system_consumer.core.MessagingSystemConsumer;
import com.example.impati.messaging_system_consumer.core.MessagingSystemListener;
import com.example.impati.messaging_system_consumer.core.MessagingSystemPoller;
import com.example.impati.messaging_system_consumer.core.MessagingSystemProperties;
import com.example.impati.messaging_system_consumer.core.SimpleChannelSubscriber;
import com.example.impati.messaging_system_consumer.core.SimpleClientRegister;
import com.example.impati.messaging_system_consumer.core.SimpleMessagingSystemConsumer;
import com.example.impati.messaging_system_consumer.core.SimpleMessagingSystemPoller;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(SpringMessagingSystemConsumerProperties.class)
public class MessagingSystemConsumerAutoConfiguration {

    private final SpringMessagingSystemConsumerProperties properties;

    public MessagingSystemConsumerAutoConfiguration(SpringMessagingSystemConsumerProperties properties) {
        this.properties = properties;
    }

    @Bean
    public MessagingSystemProperties properties() {
        return new MessagingSystemProperties(properties.url(), properties.clientName());
    }

    @Bean
    @ConditionalOnMissingBean
    public ChannelRegistration channelRegistration() {
        return new ChannelRegistration.ChannelRegistrationBuilder().build();
    }

    @Bean
    @ConditionalOnMissingBean
    public WebClient.Builder webClient() {
        return new WebClientConfig().webClientBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientRegister clientRegister(WebClient.Builder webClientBuilder, MessagingSystemProperties properties) {
        return new SimpleClientRegister(webClientBuilder, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ChannelSubscriber channelSubscriber(WebClient.Builder webClientBuilder, MessagingSystemProperties properties) {
        return new SimpleChannelSubscriber(webClientBuilder, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public <T> MessagingSystemConsumer<T> messagingSystemConsumer(WebClient.Builder webClientBuilder, MessagingSystemProperties properties) {
        return new SimpleMessagingSystemConsumer<>(webClientBuilder, properties);
    }

    @Bean
    @ConditionalOnBean(MessagingSystemListener.class)
    @ConditionalOnMissingBean(MessagingSystemPoller.class)
    public <T> MessagingSystemPoller messagingSystemPoller(
            MessagingSystemConsumer<T> messagingSystemConsumer,
            MessagingSystemListener<T> messagingSystemListener,
            ChannelRegistration channelRegistration
    ) {
        return new SimpleMessagingSystemPoller<>(
                messagingSystemConsumer,
                messagingSystemListener,
                channelRegistration
        );
    }

    @Bean
    @ConditionalOnBean(MessagingSystemPoller.class)
    public ConsumerInitializer consumerInitializer(
            ClientRegister clientRegister,
            ChannelSubscriber channelSubscriber,
            ChannelRegistration channelRegistration,
            MessagingSystemPoller messagingSystemPoller
    ) {
        return new ConsumerInitializer(
                clientRegister,
                channelSubscriber,
                channelRegistration,
                properties,
                messagingSystemPoller
        );
    }
}
