package com.github.trask.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MarsEventListener {

    private static final String HUB_NAME = "space";

    @KafkaListener(topics = HUB_NAME)
    public void receiveEvent(ConsumerRecord<String, String> event) throws SQLException {
        System.out.println("received: " + event);
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:mars")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("select 1");
            }
        }
    }
}
