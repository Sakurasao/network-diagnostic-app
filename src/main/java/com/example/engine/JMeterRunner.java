package com.example.engine;

import java.io.IOException;

public class JMeterRunner {

    public static void main(String[] args) {
        ProcessBuilder processBuilder = new ProcessBuilder("jmeter", "-n", "-t", "test.jmx", "-l", "results.jtl");
        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            System.out.println("JMeter process exited with code " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
