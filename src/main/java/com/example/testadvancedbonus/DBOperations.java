package com.example.testadvancedbonus;
import javafx.scene.control.Alert;

import java.sql.*;


public class DBOperations {

    private static final String DB_URL = "jdbc:sqlite:src/main/resources/users.db";

    public static Connection connect(){
        try{
            Connection connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connected to the database successfully.");
            return connection;
        }
        catch (SQLException e){
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
            return null;
        }
    }
    public static void registerUser(String username, String password) {
        String sql = "INSERT INTO users(username, password, h_score) VALUES(?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setInt(3, 0);
            // check if user already exists
            String checkSql = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setString(1, username);
                ResultSet rs = checkPstmt.executeQuery();
                if (rs.next()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Registration Error");
                    alert.setHeaderText("Error");
                    alert.setContentText("Username already exists. Please choose a different username.");
                    alert.show();
                    return;
                }
            }
            pstmt.executeUpdate();
            System.out.println("User registered successfully.");
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
    }
    public static boolean loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Login successful for user: " + username);
                return true;
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Error");
                alert.setHeaderText("Error");
                alert.setContentText("Invalid username or password. Please try again.");
                alert.show();
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error logging in user: " + e.getMessage());
            return false;
        }
    }
    public static String getUsername(String username) {
        String sql = "SELECT username FROM users WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            } else {
                System.out.println("No user found with username: " + username);
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving username: " + e.getMessage());
            return null;
        }
    }
    public static void updateHighScore(String username, int score) {
        String sql = "UPDATE users SET h_score = ? WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, score);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
            System.out.println("High score updated for user: " + username);
        } catch (SQLException e) {
            System.out.println("Error updating high score: " + e.getMessage());
        }
    }

    public static int getHighScore(String username) {
        String sql = "SELECT h_score FROM users WHERE username = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("h_score");
            } else {
                System.out.println("No high score found for user: " + username);
                return 0;
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving high score: " + e.getMessage());
            return 0;
        }
    }

    public static void insertScore(String username, int score) {
        String sql = "INSERT INTO scores(username, score, date) VALUES(?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
            System.out.println("Score inserted successfully for user: " + username);
        } catch (SQLException e) {
            System.out.println("Error inserting score: " + e.getMessage());
        }
    }
}
