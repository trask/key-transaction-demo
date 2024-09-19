package com.github.trask.demo;

import static org.apache.kafka.clients.producer.ProducerConfig.CLIENT_ID_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VenusController {

    private static final String HUB_NAME = "space";

    @RequestMapping("/earth")
    public String earth() throws Exception {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8082/"))
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            return "couldn't connect to earth";
        }
        if (response.statusCode() != 200) {
            return "error connecting to earth: " + response.statusCode();
        }
        return response.body();
    }

    @RequestMapping("/mars")
    public String mars() throws Exception {

        Producer<String, String> producer = createProducer();

        Future<RecordMetadata> future = producer
                .send(new ProducerRecord<>(HUB_NAME, "red", "dust"));

        future.get();

        return "success!";
    }

    private static Producer<String, String> createProducer() {
        Properties properties = new Properties();
        properties.put(CLIENT_ID_CONFIG, "venus");
        properties.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put("bootstrap.servers", "ktdemo.servicebus.windows.net:9093");
        properties.put("security.protocol", "SASL_SSL");
        properties.put("sasl.mechanism", "PLAIN");
        properties.put("sasl.jaas.config",
                "org.apache.kafka.common.security.plain.PlainLoginModule"
                        + " required"
                        + " username=\"$ConnectionString\""
                        + " password=\"" + System.getenv("EVENTHUBS_CONNECTION_STRING") + "\";");
        return new KafkaProducer<>(properties);
    }
}
