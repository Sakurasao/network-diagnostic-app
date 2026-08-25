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
package com.example.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    private final Label statusLabel = new Label("Status: IDLE");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Network Diagnostic & Stress Testing Tool");

        TabPane tabPane = new TabPane();

        // 1. Ping Tab
        Tab pingTab = new Tab("Ping / Traceroute");
        VBox pingContent = new VBox(10);
        pingContent.setPadding(new Insets(10));
        Button pingButton = new Button("Run Ping");
        Button clearPingBtn = new Button("Clear Log");
        TextArea pingLog = new TextArea();
        pingLog.setEditable(false);

        pingButton.setOnAction(e -> {
            pingLog.clear();
            runProcess(new String[]{"ping", "127.0.0.1"}, pingLog);
        });
        clearPingBtn.setOnAction(e -> pingLog.clear());

        HBox pingControls = new HBox(10, pingButton, clearPingBtn);
        pingContent.getChildren().addAll(pingControls, pingLog);
        pingTab.setContent(pingContent);

        // 2. Port Scanner Tab
        Tab portTab = new Tab("Port Scanner");
        VBox portContent = new VBox(10);
        portContent.setPadding(new Insets(10));
        Button portButton = new Button("Run Port Scan");
        Button clearPortBtn = new Button("Clear Log");
        TextArea portLog = new TextArea();
        portLog.setEditable(false);

        portButton.setOnAction(e -> {
            portLog.clear();
            runProcess(new String[]{"java", "-cp", "target/classes", "com.example.engine.PortScanner"}, portLog);
        });
        clearPortBtn.setOnAction(e -> portLog.clear());

        HBox portControls = new HBox(10, portButton, clearPortBtn);
        portContent.getChildren().addAll(portControls, portLog);
        portTab.setContent(portContent);

        // 3. JMeter Tab
        Tab jmeterTab = new Tab("JMeter Stress Test");
        VBox jmeterContent = new VBox(10);
        jmeterContent.setPadding(new Insets(10));
        Button jmeterButton = new Button("Run JMeter Test");
        Button clearJmeterBtn = new Button("Clear Log");
        TextArea jmeterLog = new TextArea();
        jmeterLog.setEditable(false);

        jmeterButton.setOnAction(e -> {
            jmeterLog.clear();
            runProcess(new String[]{"java", "-cp", "target/classes", "com.example.engine.JMeterRunner"}, jmeterLog);
        });
        clearJmeterBtn.setOnAction(e -> jmeterLog.clear());

        HBox jmeterControls = new HBox(10, jmeterButton, clearJmeterBtn);
        jmeterContent.getChildren().addAll(jmeterControls, jmeterLog);
        jmeterTab.setContent(jmeterContent);

        tabPane.getTabs().addAll(pingTab, portTab, jmeterTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Status Bar
        HBox statusBar = new HBox(10, statusLabel);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: #ffffff;");
        statusLabel.setStyle("-fx-text-fill: #00ff00; -fx-font-weight: bold;");

        BorderPane root = new BorderPane();
        root.setCenter(tabPane);
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 720, 520);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles/main-theme.css").toExternalForm());
        } catch (Exception ignored) {}

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void runProcess(String[] command, TextArea log) {
        new Thread(() -> {
            Platform.runLater(() -> statusLabel.setText("Status: RUNNING..."));
            try {
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.redirectErrorStream(true);
                Process p = pb.start();

                p.getInputStream().lines().forEach(line -> 
                    Platform.runLater(() -> log.appendText(line + "\n"))
                );

                int exitCode = p.waitFor();
                Platform.runLater(() -> log.appendText("\n[Process finished with exit code: " + exitCode + "]\n"));
            } catch (Exception e) {
                Platform.runLater(() -> log.appendText("Error: " + e.getMessage() + "\n"));
            } finally {
                Platform.runLater(() -> statusLabel.setText("Status: IDLE"));
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package com.example.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    private final Label statusLabel = new Label("Status: IDLE");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Network Diagnostic & Stress Testing Tool");

        TabPane tabPane = new TabPane();

        // 1. Ping Tab
        Tab pingTab = new Tab("Ping / Traceroute");
        VBox pingContent = new VBox(10);
        pingContent.setPadding(new Insets(10));
        Button pingButton = new Button("Run Ping");
        Button clearPingBtn = new Button("Clear Log");
        TextArea pingLog = new TextArea();
        pingLog.setEditable(false);

        pingButton.setOnAction(e -> {
            pingLog.clear();
            runProcess(new String[]{"ping", "127.0.0.1"}, pingLog);
        });
        clearPingBtn.setOnAction(e -> pingLog.clear());

        HBox pingControls = new HBox(10, pingButton, clearPingBtn);
        pingContent.getChildren().addAll(pingControls, pingLog);
        pingTab.setContent(pingContent);

        // 2. Port Scanner Tab
        Tab portTab = new Tab("Port Scanner");
        VBox portContent = new VBox(10);
        portContent.setPadding(new Insets(10));
        Button portButton = new Button("Run Port Scan");
        Button clearPortBtn = new Button("Clear Log");
        TextArea portLog = new TextArea();
        portLog.setEditable(false);

        portButton.setOnAction(e -> {
            portLog.clear();
            runProcess(new String[]{"java", "-cp", "target/classes", "com.example.engine.PortScanner"}, portLog);
        });
        clearPortBtn.setOnAction(e -> portLog.clear());

        HBox portControls = new HBox(10, portButton, clearPortBtn);
        portContent.getChildren().addAll(portControls, portLog);
        portTab.setContent(portContent);

        // 3. JMeter Tab
        Tab jmeterTab = new Tab("JMeter Stress Test");
        VBox jmeterContent = new VBox(10);
        jmeterContent.setPadding(new Insets(10));
        Button jmeterButton = new Button("Run JMeter Test");
        Button clearJmeterBtn = new Button("Clear Log");
        TextArea jmeterLog = new TextArea();
        jmeterLog.setEditable(false);

        jmeterButton.setOnAction(e -> {
            jmeterLog.clear();
            runProcess(new String[]{"java", "-cp", "target/classes", "com.example.engine.JMeterRunner"}, jmeterLog);
        });
        clearJmeterBtn.setOnAction(e -> jmeterLog.clear());

        HBox jmeterControls = new HBox(10, jmeterButton, clearJmeterBtn);
        jmeterContent.getChildren().addAll(jmeterControls, jmeterLog);
        jmeterTab.setContent(jmeterContent);

        tabPane.getTabs().addAll(pingTab, portTab, jmeterTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Status Bar
        HBox statusBar = new HBox(10, statusLabel);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setStyle("-fx-background-color: #1e1e1e; -fx-text-fill: #ffffff;");
        statusLabel.setStyle("-fx-text-fill: #00ff00; -fx-font-weight: bold;");

        BorderPane root = new BorderPane();
        root.setCenter(tabPane);
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 720, 520);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles/main-theme.css").toExternalForm());
        } catch (Exception ignored) {}

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void runProcess(String[] command, TextArea log) {
        new Thread(() -> {
            Platform.runLater(() -> statusLabel.setText("Status: RUNNING..."));
            try {
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.redirectErrorStream(true);
                Process p = pb.start();

                p.getInputStream().lines().forEach(line -> 
                    Platform.runLater(() -> log.appendText(line + "\n"))
                );

                int exitCode = p.waitFor();
                Platform.runLater(() -> log.appendText("\n[Process finished with exit code: " + exitCode + "]\n"));
            } catch (Exception e) {
                Platform.runLater(() -> log.appendText("Error: " + e.getMessage() + "\n"));
            } finally {
                Platform.runLater(() -> statusLabel.setText("Status: IDLE"));
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
