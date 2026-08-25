package com.example.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();

        // Ping/Traceroute Tab
        Tab pingTab = new Tab("Ping/Traceroute");
        VBox pingContent = new VBox();
        Button pingButton = new Button("Run Ping");
        Button clearPingLogButton = new Button("Clear Log");
        TextArea pingLog = new TextArea();
        pingLog.setEditable(false);
        pingContent.getChildren().addAll(pingButton, clearPingLogButton, pingLog);
        pingTab.setContent(pingContent);

        // Port Scanner Tab
        Tab portTab = new Tab("Port Scanner");
        VBox portContent = new VBox();
        Button portButton = new Button("Run Port Scan");
        Button clearPortLogButton = new Button("Clear Log");
        TextArea portLog = new TextArea();
        portLog.setEditable(false);
        portContent.getChildren().addAll(portButton, clearPortLogButton, portLog);
        portTab.setContent(portContent);

        // JMeter Stress Test Tab
        Tab jmeterTab = new Tab("JMeter Stress Test");
        VBox jmeterContent = new VBox();
        Button jmeterButton = new Button("Run JMeter Test");
        Button clearJMeterLogButton = new Button("Clear Log");
        TextArea jmeterLog = new TextArea();
        jmeterLog.setEditable(false);
        jmeterContent.getChildren().addAll(jmeterButton, clearJMeterLogButton, jmeterLog);
        jmeterTab.setContent(jmeterContent);

        tabPane.getTabs().addAll(pingTab, portTab, jmeterTab);

        Scene scene = new Scene(tabPane, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/main-theme.css").toExternalForm());

        primaryStage.setTitle("Network Diagnostic & Stress Testing");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void runPing(TextArea log) {
        new Thread(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("ping", "example.com");
                Process process = processBuilder.start();
                process.getInputStream().lines().forEach(line -> Platform.runLater(() -> log.appendText(line + "\n")));
                int exitCode = process.waitFor();
                Platform.runLater(() -> log.appendText("Ping process exited with code " + exitCode + "\n"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void runPortScan(TextArea log) {
        new Thread(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", "target/classes", "com.example.engine.PortScanner");
                Process process = processBuilder.start();
                process.getInputStream().lines().forEach(line -> Platform.runLater(() -> log.appendText(line + "\n")));
                int exitCode = process.waitFor();
                Platform.runLater(() -> log.appendText("Port Scan process exited with code " + exitCode + "\n"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void runJMeterTest(TextArea log) {
        new Thread(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", "target/classes", "com.example.engine.JMeterRunner");
                Process process = processBuilder.start();
                process.getInputStream().lines().forEach(line -> Platform.runLater(() -> log.appendText(line + "\n")));
                int exitCode = process.waitFor();
                Platform.runLater(() -> log.appendText("JMeter Test process exited with code " + exitCode + "\n"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
