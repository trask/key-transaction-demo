package com.github.trask.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@RestController
public class MercuryController {

    @RequestMapping("/earth")
    public String earth() throws Exception {
        return contact("http://localhost:8081/earth");
    }

    @RequestMapping("/mars")
    public String mars() throws Exception {
        return contact("http://localhost:8081/mars");
    }

    @RequestMapping("/other")
    public String other() throws Exception {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:mercury")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("select 1");
            }
        }
        return "success!";
    }

    private static String contact(String url) throws URISyntaxException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            return "couldn't connect to venus";
        }
        if (response.statusCode() != 200) {
            return "error connecting to venus: " + response.statusCode();
        }
        return response.body();
    }
}
