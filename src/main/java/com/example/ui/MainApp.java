package com.example.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainApp extends Application {

    private final Label statusLabel = new Label("Status: IDLE");
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final TextArea pingLog = new TextArea();
    private final TextField pingNotes = new TextField();

    private final TextArea portLog = new TextArea();
    private final TextField portNotes = new TextField();

    private final TextArea stressLog = new TextArea();
    private final TextField stressNotes = new TextField();

    // Input manual Metadata Laporan Audit
    private final TextField auditorNameInput = new TextField();
    private final TextField auditLocationInput = new TextField();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Network Diagnostic & Stress Testing Tool");

        // Set Icon Taskbar
        try {
            InputStream iconStream = getClass().getResourceAsStream("/icon.png");
            if (iconStream != null) {
                primaryStage.getIcons().add(new Image(iconStream));
            } else {
                primaryStage.getIcons().add(createAppIconImage());
            }
        } catch (Exception e) {
            primaryStage.getIcons().add(createAppIconImage());
        }

        // Top Header Banner
        HBox headerBar = new HBox(12);
        headerBar.setPadding(new Insets(10, 15, 10, 15));
        headerBar.setAlignment(Pos.CENTER_LEFT);
        headerBar.setStyle("-fx-background-color: #1e1e1e; -fx-border-color: #333333; -fx-border-width: 0 0 1 0;");

        ImageView logoView = new ImageView();
        try {
            InputStream is = getClass().getResourceAsStream("/icon.png");
            if (is != null) {
                logoView.setImage(new Image(is));
            } else {
                logoView.setImage(createAppIconImage());
            }
        } catch (Exception e) {
            logoView.setImage(createAppIconImage());
        }
        logoView.setFitWidth(28);
        logoView.setFitHeight(28);

        Label appTitleLabel = new Label("Network Diagnostic & Stress Testing Tool");
        appTitleLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 15px; -fx-font-weight: bold;");

        headerBar.getChildren().addAll(logoView, appTitleLabel);

        TabPane tabPane = new TabPane();

        // 1. PING TAB
        Tab pingTab = new Tab("Ping & Traceroute");
        VBox pingContent = new VBox(10);
        pingContent.setPadding(new Insets(15));
        GridPane pingForm = new GridPane();
        pingForm.setHgap(10);
        pingForm.setVgap(10);
        TextField pingHostInput = new TextField("192.168.1.1");
        TextField pingCountInput = new TextField("4");
        Button btnPing = new Button("Run Ping");
        Button btnTrace = new Button("Run Traceroute");
        Button btnClearPing = new Button("Clear Log");

        pingForm.add(new Label("Target Host / IP:"), 0, 0);
        pingForm.add(pingHostInput, 1, 0);
        pingForm.add(new Label("Packet Count:"), 0, 1);
        pingForm.add(pingCountInput, 1, 1);

        HBox pingBtnBox = new HBox(10, btnPing, btnTrace, btnClearPing);
        pingLog.setEditable(false);
        VBox.setVgrow(pingLog, Priority.ALWAYS);
        pingNotes.setPromptText("Catatan Hasil Diagnostic Ping...");
        pingContent.getChildren().addAll(pingForm, pingBtnBox, pingLog, new Label("Catatan Audit:"), pingNotes);
        pingTab.setContent(pingContent);

        btnPing.setOnAction(e -> {
            pingLog.clear();
            String host = pingHostInput.getText().trim();
            String count = pingCountInput.getText().trim();
            boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");
            String[] cmd = isWin ? new String[]{"ping", "-n", count, host} : new String[]{"ping", "-c", count, host};
            runExternalCommand(cmd, pingLog);
        });

        btnTrace.setOnAction(e -> {
            pingLog.clear();
            String host = pingHostInput.getText().trim();
            boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");
            String[] cmd = isWin ? new String[]{"tracert", host} : new String[]{"traceroute", host};
            runExternalCommand(cmd, pingLog);
        });
        btnClearPing.setOnAction(e -> pingLog.clear());

        // 2. PORT SCANNER TAB
        Tab portTab = new Tab("Port Scanner");
        VBox portContent = new VBox(10);
        portContent.setPadding(new Insets(15));
        GridPane portForm = new GridPane();
        portForm.setHgap(10);
        portForm.setVgap(10);
        TextField portHostInput = new TextField("192.168.1.1");
        TextField startPortInput = new TextField("20");
        TextField endPortInput = new TextField("90");
        Button btnScanPort = new Button("Start Port Scan");
        Button btnClearPort = new Button("Clear Log");

        portForm.add(new Label("Target IP:"), 0, 0);
        portForm.add(portHostInput, 1, 0);
        portForm.add(new Label("Start Port:"), 0, 1);
        portForm.add(startPortInput, 1, 1);
        portForm.add(new Label("End Port:"), 2, 1);
        portForm.add(endPortInput, 3, 1);

        HBox portBtnBox = new HBox(10, btnScanPort, btnClearPort);
        portLog.setEditable(false);
        VBox.setVgrow(portLog, Priority.ALWAYS);
        portNotes.setPromptText("Catatan Audit Keamanan Port...");
        portContent.getChildren().addAll(portForm, portBtnBox, portLog, new Label("Catatan Audit:"), portNotes);
        portTab.setContent(portContent);

        btnScanPort.setOnAction(e -> {
            portLog.clear();
            String host = portHostInput.getText().trim();
            int startPort = Integer.parseInt(startPortInput.getText().trim());
            int endPort = Integer.parseInt(endPortInput.getText().trim());
            runNativePortScan(host, startPort, endPort, portLog);
        });
        btnClearPort.setOnAction(e -> portLog.clear());

        // 3. STRESS TEST TAB
        Tab stressTab = new Tab("Stress Test");
        VBox stressContent = new VBox(10);
        stressContent.setPadding(new Insets(15));
        GridPane stressForm = new GridPane();
        stressForm.setHgap(10);
        stressForm.setVgap(10);
        TextField targetUrlInput = new TextField("http://google.com");
        TextField threadsInput = new TextField("20");
        Button btnRunStress = new Button("Start Load Test");
        Button btnClearStress = new Button("Clear Log");

        stressForm.add(new Label("Target URL:"), 0, 0);
        stressForm.add(targetUrlInput, 1, 0);
        stressForm.add(new Label("Concurrent Users (Threads):"), 0, 1);
        stressForm.add(threadsInput, 1, 1);

        HBox stressBtnBox = new HBox(10, btnRunStress, btnClearStress);
        stressLog.setEditable(false);
        VBox.setVgrow(stressLog, Priority.ALWAYS);
        stressNotes.setPromptText("Catatan Evaluasi Stress Testing Web...");
        stressContent.getChildren().addAll(stressForm, stressBtnBox, stressLog, new Label("Catatan Audit:"), stressNotes);
        stressTab.setContent(stressContent);

        btnRunStress.setOnAction(e -> {
            stressLog.clear();
            String url = targetUrlInput.getText().trim();
            String threads = threadsInput.getText().trim();
            String[] cmd = new String[]{"java", "-cp", "target/classes", "com.example.engine.JMeterRunner", url, threads};
            runExternalCommand(cmd, stressLog);
        });
        btnClearStress.setOnAction(e -> stressLog.clear());

        // 4. EXPORT TAB (DILENGKAPI FORM PENGUJI & LOKASI)
        Tab exportTab = new Tab("Export Report");
        VBox exportContent = new VBox(15);
        exportContent.setPadding(new Insets(20));
        exportContent.setAlignment(Pos.TOP_LEFT);

        Label exportTitle = new Label("Export Laporan Audit Terpadu");
        exportTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane metaForm = new GridPane();
        metaForm.setHgap(10);
        metaForm.setVgap(10);

        auditorNameInput.setPromptText("Masukkan Nama Penguji / Auditor...");
        auditLocationInput.setPromptText("Masukkan Lokasi / Instansi Pengujian...");

        metaForm.add(new Label("Nama Auditor:"), 0, 0);
        metaForm.add(auditorNameInput, 1, 0);
        metaForm.add(new Label("Lokasi Audit:"), 0, 1);
        metaForm.add(auditLocationInput, 1, 1);

        Label exportDesc = new Label("Laporan ini akan menggabungkan data waktu (otomatis), nama penguji, lokasi, serta seluruh log dan catatan dari ketiga modul pengujian.");
        exportDesc.setWrapText(true);

        Button btnExportAll = new Button("Generate & Save Final Report (.txt)");
        btnExportAll.setStyle("-fx-font-size: 14px; -fx-padding: 8 16;");
        btnExportAll.setOnAction(e -> exportUnifiedReport(primaryStage));

        exportContent.getChildren().addAll(exportTitle, metaForm, new Separator(), exportDesc, btnExportAll);
        exportTab.setContent(exportContent);

        // 5. ABOUT TAB
        Tab aboutTab = new Tab("About Developer");
        VBox aboutContent = new VBox(12);
        aboutContent.setPadding(new Insets(20));

        Label devTitle = new Label("Network Diagnostic & Stress Testing Tool");
        devTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Label devName = new Label("Pengembang : Fadhlullah Hanif Nur Caturangga");
        Label devNim = new Label("NIM         : 241080200112");
        Label devUniv = new Label("Instansi    : Universitas Muhammadiyah Sidoarjo");

        aboutContent.getChildren().addAll(devTitle, new Separator(), devName, devNim, devUniv);
        aboutTab.setContent(aboutContent);

        tabPane.getTabs().addAll(pingTab, portTab, stressTab, exportTab, aboutTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Status Bar
        HBox statusBar = new HBox(10, statusLabel);
        statusBar.setPadding(new Insets(8, 15, 8, 15));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setStyle("-fx-background-color: #121212;");
        statusLabel.setStyle("-fx-text-fill: #00FF66; -fx-font-weight: bold; -fx-font-family: monospace;");

        BorderPane root = new BorderPane();
        root.setTop(headerBar);
        root.setCenter(tabPane);
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 840, 650);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles/main-theme.css").toExternalForm());
        } catch (Exception ignored) {}

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Image createAppIconImage() {
        Canvas canvas = new Canvas(64, 64);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.web("#0D1117"));
        gc.fillRoundRect(0, 0, 64, 64, 16, 16);

        gc.setStroke(Color.web("#00FF66"));
        gc.setLineWidth(5);
        gc.strokeLine(12, 44, 26, 28);
        gc.strokeLine(26, 28, 38, 38);
        gc.strokeLine(38, 38, 52, 18);

        gc.setFill(Color.web("#00E5FF"));
        gc.fillOval(8, 40, 8, 8);
        gc.fillOval(22, 24, 8, 8);
        gc.fillOval(34, 34, 8, 8);

        gc.setFill(Color.web("#00FF66"));
        gc.fillOval(48, 14, 10, 10);

        WritableImage img = new WritableImage(64, 64);
        canvas.snapshot(null, img);
        return img;
    }

    private void runExternalCommand(String[] command, TextArea log) {
        executor.submit(() -> {
            Platform.runLater(() -> statusLabel.setText("Status: RUNNING..."));
            try {
                ProcessBuilder pb = new ProcessBuilder(command);
                pb.redirectErrorStream(true);
                Process p = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String currentLine = line;
                        Platform.runLater(() -> log.appendText(currentLine + "\n"));
                    }
                }
                int exitCode = p.waitFor();
                Platform.runLater(() -> log.appendText("\n[Task Finished with Exit Code: " + exitCode + "]\n"));
            } catch (Exception e) {
                Platform.runLater(() -> log.appendText("Execution Error: " + e.getMessage() + "\n"));
            } finally {
                Platform.runLater(() -> statusLabel.setText("Status: IDLE"));
            }
        });
    }

    private void runNativePortScan(String host, int startPort, int endPort, TextArea log) {
        executor.submit(() -> {
            Platform.runLater(() -> {
                statusLabel.setText("Status: SCANNING PORTS...");
                log.appendText("Scanning " + host + " (Port " + startPort + " to " + endPort + ")...\n--------------------------------------------------\n");
            });

            for (int port = startPort; port <= endPort; port++) {
                final int currentPort = port;
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(host, currentPort), 150);
                    Platform.runLater(() -> log.appendText("[+] Port " + currentPort + " : OPEN\n"));
                } catch (Exception ignored) {
                    Platform.runLater(() -> log.appendText("[-] Port " + currentPort + " : CLOSED\n"));
                }
            }
            Platform.runLater(() -> {
                log.appendText("--------------------------------------------------\n[Port Scan Finished]\n");
                statusLabel.setText("Status: IDLE");
            });
        });
    }

    private void exportUnifiedReport(Stage stage) {
        String auditorName = auditorNameInput.getText().trim();
        String auditLocation = auditLocationInput.getText().trim();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan Hasil Audit Terpadu");
        fileChooser.setInitialFileName("AUDIT_REPORT_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));

        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.println("=========================================================================");
                writer.println("             LAPORAN AUDIT DIAGNOSTIK & STRESS TESTING                   ");
                writer.println("=========================================================================");
                writer.println("Waktu Audit : " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                writer.println("Auditor     : " + (auditorName.isEmpty() ? "-" : auditorName));
                writer.println("Lokasi Audit: " + (auditLocation.isEmpty() ? "-" : auditLocation));
                writer.println("-------------------------------------------------------------------------\n");

                writer.println(">>> 1. MODUL PING & TRACEROUTE <<<");
                writer.println("Catatan Audit: " + (pingNotes.getText().isEmpty() ? "-" : pingNotes.getText()));
                writer.println("Hasil Log:");
                writer.println(pingLog.getText().isEmpty() ? "[Tidak ada data]" : pingLog.getText());
                writer.println("\n-------------------------------------------------------------------------\n");

                writer.println(">>> 2. MODUL PORT SCANNER <<<");
                writer.println("Catatan Audit: " + (portNotes.getText().isEmpty() ? "-" : portNotes.getText()));
                writer.println("Hasil Log:");
                writer.println(portLog.getText().isEmpty() ? "[Tidak ada data]" : portLog.getText());
                writer.println("\n-------------------------------------------------------------------------\n");

                writer.println(">>> 3. MODUL STRESS TEST <<<");
                writer.println("Catatan Audit: " + (stressNotes.getText().isEmpty() ? "-" : stressNotes.getText()));
                writer.println("Hasil Log:");
                writer.println(stressLog.getText().isEmpty() ? "[Tidak ada data]" : stressLog.getText());
                writer.println("\n=========================================================================");

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Laporan Terpadu Berhasil Disimpan di:\n" + file.getAbsolutePath(), ButtonType.OK);
                alert.showAndWait();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal mengekspor laporan: " + ex.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    @Override
    public void stop() {
        executor.shutdownNow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}