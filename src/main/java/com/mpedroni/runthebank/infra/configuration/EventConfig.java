package com.mpedroni.runthebank.infra.configuration;

import com.mpedroni.runthebank.infra.services.EventService;
import com.mpedroni.runthebank.infra.services.impl.RabbitMqEventService;
import com.mpedroni.runthebank.infra.services.local.FakeEventService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class EventConfig {

    @Bean
    @Profile({"test-e2e"})
    EventService localVideoCreatedEventService() {
        return new FakeEventService();
    }

    @Bean
    @ConditionalOnMissingBean
    EventService rabbitMqEventService(RabbitTemplate template) {
        return new RabbitMqEventService(template);
    }
}