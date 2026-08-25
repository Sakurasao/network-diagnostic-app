package com.example.ui;

import javafx.application.Application;
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
        pingButton.setOnAction(event -> runPing());
        pingContent.getChildren().add(pingButton);
        pingTab.setContent(pingContent);

        // Port Scanner Tab
        Tab portTab = new Tab("Port Scanner");
        VBox portContent = new VBox();
        Button portButton = new Button("Run Port Scan");
        portButton.setOnAction(event -> runPortScan());
        portContent.getChildren().add(portButton);
        portTab.setContent(portContent);

        // JMeter Stress Test Tab
        Tab jmeterTab = new Tab("JMeter Stress Test");
        VBox jmeterContent = new VBox();
        Button jmeterButton = new Button("Run JMeter Test");
        jmeterButton.setOnAction(event -> runJMeterTest());
        jmeterContent.getChildren().add(jmeterButton);
        jmeterTab.setContent(jmeterContent);

        tabPane.getTabs().addAll(pingTab, portTab, jmeterTab);

        Scene scene = new Scene(tabPane, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles/main-theme.css").toExternalForm());

        primaryStage.setTitle("Network Diagnostic & Stress Testing");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void runPing() {
        new Thread(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("ping", "example.com");
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                System.out.println("Ping process exited with code " + exitCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void runPortScan() {
        new Thread(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", "target/classes", "com.example.engine.PortScanner");
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                System.out.println("Port Scan process exited with code " + exitCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void runJMeterTest() {
        new Thread(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", "target/classes", "com.example.engine.JMeterRunner");
                Process process = processBuilder.start();
                int exitCode = process.waitFor();
                System.out.println("JMeter Test process exited with code " + exitCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
