package com.github.trask.demo;

import static org.apache.kafka.clients.consumer.ConsumerConfig.CLIENT_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@SpringBootApplication
public class MarsMain {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(MarsMain.class, args);

        System.out.println("waiting for events...");
        Thread.sleep(Long.MAX_VALUE);
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(CLIENT_ID_CONFIG, "mars");
        configs.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put("bootstrap.servers", "ktdemo.servicebus.windows.net:9093");
        configs.put("group.id", "$Default");
        configs.put("request.timeout.ms", "60000");
        configs.put("security.protocol", "SASL_SSL");
        configs.put("sasl.mechanism", "PLAIN");
        configs.put("sasl.jaas.config",
                "org.apache.kafka.common.security.plain.PlainLoginModule"
                        + " required"
                        + " username=\"$ConnectionString\""
                        + " password=\"" + System.getenv("EVENTHUBS_CONNECTION_STRING") + "\";");
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
