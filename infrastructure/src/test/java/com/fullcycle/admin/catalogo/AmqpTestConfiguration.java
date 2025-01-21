package com.fullcycle.admin.catalogo;

import com.rabbitmq.client.Channel;
import org.mockito.BDDMockito;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.test.RabbitListenerTest;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;

@Configuration
@RabbitListenerTest(spy = false, capture = true)
public class AmqpTestConfiguration {

    @Bean
    public TestRabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        return new TestRabbitTemplate(connectionFactory);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        final var factory = mock(ConnectionFactory.class);
        final var connection = mock(Connection.class);
        final var channel = mock(Channel.class);
        BDDMockito.willReturn(connection).given(factory).createConnection();
        BDDMockito.willReturn(channel).given(connection).createChannel(anyBoolean());
        BDDMockito.given(channel.isOpen()).willReturn(true);
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        final var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }
}
