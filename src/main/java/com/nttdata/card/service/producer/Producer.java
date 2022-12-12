package com.nttdata.card.service.producer;

import com.nttdata.card.service.implementation.CardServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(CardServiceImpl.class);

    @Autowired
    public Producer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendPrimaryAccount(String primaryAccountId) {
        LOGGER.info("Sending primary account");
        kafkaTemplate.send("primary-account",primaryAccountId);
    }
}
