package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/date.db")) {
            System.out.println("Подключение к базе данных успешно!");

            // Пример выполнения простого запроса
            String query = "SELECT sqlite_version()";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                if (resultSet.next()) {
                    String version = resultSet.getString(1);
                    System.out.println("Версия SQLite: " + version);
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при подключении к базе данных: " + e.getMessage());
        }
    }
}
