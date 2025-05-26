package com.example.testadvancedbonus;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Random;


public class ChickenTapper extends Application {

    private Pane gamePane;
    private Label scoreLabel;
    private String name;
    private int score = 0;
    private AudioClip hitSound;
    Label missesLabel;
    private int misses = 0;
    private final int maxMisses = 5;
    private String difficulty = "EASY";
    private final Random random = new Random();

    private final int WIDTH = 1500;
    private final int HEIGHT = 700;

    @Override
    public void start(Stage stage) {

        // HOME PAGE
        VBox homePage = new VBox(20);
        homePage.setAlignment(Pos.CENTER);

        hitSound = new AudioClip(getClass().getResource("/hit.wav").toExternalForm());
        hitSound.setVolume(0.1);
        Media media = new Media(getClass().getResource("/overlay.mp4").toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaView.setPreserveRatio(false);
        mediaView.setFitWidth(WIDTH - 1);
        mediaView.setFitHeight(HEIGHT - 1);

        Label welcomeLabel = new Label("Welcome to Chicken Tapper!");
        welcomeLabel.getStyleClass().add("title");

        Label instructionsLabel = new Label("Click on the chickens to score points. " + "If you miss too many, the game is over!");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");
        nameField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setMaxWidth(300);

        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(150);
        loginButton.getStyleClass().add("button");

        Button registerButton = new Button("Register");
        registerButton.setMaxWidth(100);
        registerButton.getStyleClass().add("button");

        homePage.getChildren().addAll(welcomeLabel, instructionsLabel, nameField, passwordField, loginButton, registerButton);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(mediaView, homePage);

        Scene homeScene = new Scene(stackPane, WIDTH, HEIGHT);
        homeScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        mediaPlayer.play();
        stage.setScene(homeScene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        stage.setTitle("Chicken Tapper");
        stage.show();

        HBox topBar = new HBox(10);
        topBar.setAlignment(Pos.CENTER);
        Button startButton = new Button("Start");
        scoreLabel = new Label("Score: 0");
        missesLabel = new Label("Misses: 0");

        ComboBox<String> difficultyBox = new ComboBox<>();
        difficultyBox.getItems().addAll("EASY", "MEDIUM", "HARD");
        difficultyBox.setValue("EASY");
        difficultyBox.setOnAction(e -> difficulty = difficultyBox.getValue());
        Label difficultyLabel = new Label("Difficulty:");

        Button backButton = new Button("Back");
        backButton.setOnAction(
                e -> {
                    stage.setScene(homeScene);
                    mediaPlayer.play();
                }
        );

        topBar.getChildren().addAll(startButton, scoreLabel, missesLabel, difficultyLabel , difficultyBox, backButton);

        gamePane = new Pane();
        gamePane.setPrefSize(WIDTH+100, HEIGHT+100);
        gamePane.setStyle("-fx-background-color: #2a2a40; -fx-border-color: #1e90ff; -fx-border-radius: 12px");

        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 10; -fx-background-color: #2a2a40;");

        root.getChildren().addAll(topBar, gamePane);

        Scene gameScene = new Scene(root);
        gameScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        loginButton.setOnAction(e -> {
            name = nameField.getText();
            String password = passwordField.getText();
            if (name.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter both name and password.");
                alert.setTitle("Error");
                alert.show();
            } else {
                if (DBOperations.loginUser(name, password)) {
                    mediaPlayer.stop();
                    nameField.clear();
                    passwordField.clear();
                    stage.setTitle("Chicken Tapper - " + name);
                    gamePane.getChildren().clear();
                    score = 0;
                    misses = 0;
                    scoreLabel.setText("Score: 0");
                    missesLabel.setText("Misses: 0");
                    stage.setScene(gameScene);
                    stage.setFullScreen(true);
                }
            }
        });
        registerButton.setOnAction(e -> {
            name = nameField.getText();
            String password = passwordField.getText();
            if (name.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter both name and password.");
                alert.setTitle("Error");
                alert.show();
            } else {
                if(DBOperations.getUsername(name) != null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Username already exists. Please choose a different name.");
                    alert.setTitle("Registration Error");
                    alert.setHeaderText("Error");
                    alert.show();
                    return;
                }
                DBOperations.registerUser(name, password);
                nameField.clear();
                passwordField.clear();
                mediaPlayer.stop();
                stage.setTitle("Chicken Tapper - " + name);
                gamePane.getChildren().clear();
                score = 0;
                misses = 0;
                scoreLabel.setText("Score: 0");
                missesLabel.setText("Misses: 0");
                stage.setScene(gameScene);
                stage.setFullScreen(true);
            }
        });
        startButton.setOnAction(e -> startGame(name));
    }

    private void startGame(String name) {
        score = 0;
        scoreLabel.setText("Score: 0");
        gamePane.getChildren().clear();
        misses = 0;
        missesLabel.setText("Misses: 0");
        spawnChicken(name);
    }

    private void spawnChicken(String name) {
        ImageView chicken = new ImageView(getClass().getResource("/chicken.png").toExternalForm()); // reusing chicken image
        int size;
        switch (difficulty) {
            case "EASY" -> size = 120;
            case "MEDIUM" -> size = 80;
            case "HARD" -> size = 70;
            default -> size = 100;
        }

        chicken.setFitWidth(size);
        chicken.setFitHeight(size);
        chicken.setPreserveRatio(true);

        double x = random.nextDouble() * (WIDTH - size);
        double y = random.nextDouble() * (HEIGHT - size);
        chicken.setLayoutX(x);
        chicken.setLayoutY(y);

        int disappear = switch (difficulty) {
            case "EASY" -> Difficulty.EASY;
            case "MEDIUM" -> Difficulty.MEDIUM;
            case "HARD" ->  Difficulty.HARD;
            default -> 1000;
        };
        PauseTransition timeout = new PauseTransition(Duration.millis(disappear));
        timeout.setOnFinished(e -> {
            if (gamePane.getChildren().contains(chicken)) {
                gamePane.getChildren().remove(chicken);
                misses++;
                missesLabel.setText("Misses: " + misses);
                if (misses >= maxMisses) {
                    hitSound.stop();
                    gamePane.getChildren().add(createGameOverPopup(name, score));
                    misses = 0;
                    score = 0;
                    scoreLabel.setText("Score: 0");
                    missesLabel.setText("Misses: 0");
                } else {
                    spawnChicken(name);
                }
            }
        });
        timeout.play();

        chicken.setOnMouseClicked(e -> {
            hitSound.play();
            timeout.stop();
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), chicken);
            scale.setToX(0);
            scale.setToY(0);
            scale.setOnFinished(event -> {
                gamePane.getChildren().remove(chicken);
            });
            score++;
            scoreLabel.setText("Score: " + score);
            spawnChicken(name);
            scale.play();
            timeout.play();
        });

        gamePane.getChildren().add(chicken);
    }
    private Pane createGameOverPopup(String name, int score) {
        StackPane container = new StackPane();
        VBox popup = new VBox(15);
        popup.setAlignment(Pos.CENTER);
        popup.setStyle("-fx-background-color: #2a2a40; -fx-border-color: #1e90ff; -fx-border-radius: 10; -fx-background-radius: 10");

        Label header = new Label("Too many misses! Better luck next time");
        header.getStyleClass().add("title");
        header.setStyle("-fx-font-weight: bold;");

        Label msg = new Label("Game Over! Score: " + score);

        DBOperations.insertScore(name, score);
        if (DBOperations.getHighScore(name) < score) {
            DBOperations.updateHighScore(name, score);
            msg.setText(msg.getText() + "\nNew High Score!!!");
        } else {
            msg.setText(msg.getText() + "\nYour High Score: " + DBOperations.getHighScore(name));
        }
        Button okButton = new Button("OK");
        okButton.setStyle("-fx-font-weight: bold;");
        okButton.setOnAction(e -> {
            gamePane.getChildren().remove(container);
        });

        popup.getChildren().addAll(header, msg, okButton);

        container.getChildren().add(popup);
        container.setPrefSize(WIDTH+15, HEIGHT+93);

        return container;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
