package com.github.trask.demo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@RestController
public class EarthController {

    @RequestMapping("/")
    public String orbit() throws SQLException {

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:earth")) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("select 1");
            }
        }

        return "success!";
    }
}
