package com.example.engine;

import java.io.IOException;
import java.net.Socket;

public class PortScanner {

    public static void main(String[] args) {
        String host = "example.com";
        int startPort = 1;
        int endPort = 1024;

        for (int port = startPort; port <= endPort; port++) {
            try (Socket socket = new Socket(host, port)) {
                System.out.println("Port " + port + " is open");
            } catch (IOException e) {
                // Port is closed or filtered
            }
        }
    }
}
