package com.example.engine;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class JMeterRunner {
    public static void main(String[] args) {
        String targetUrl = args.length > 0 ? args[0] : "http://google.com";
        int totalThreads = args.length > 1 ? Integer.parseInt(args[1]) : 20;

        if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
            targetUrl = "http://" + targetUrl;
        }

        System.out.println("Starting Native Stress Testing Engine...");
        System.out.println("Target: " + targetUrl);
        System.out.println("Concurrent Threads: " + totalThreads);
        System.out.println("--------------------------------------------------");

        ExecutorService executor = Executors.newFixedThreadPool(totalThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < totalThreads; i++) {
            final String finalUrl = targetUrl;
            final int reqId = i + 1;
            executor.submit(() -> {
                try {
                    URL url = new URL(finalUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(3000);
                    conn.setReadTimeout(3000);

                    int responseCode = conn.getResponseCode();
                    if (responseCode >= 200 && responseCode < 400) {
                        successCount.incrementAndGet();
                        System.out.println("[Request #" + reqId + "] SUCCESS - Response Code: " + responseCode);
                    } else {
                        failCount.incrementAndGet();
                        System.out.println("[Request #" + reqId + "] FAILED - Response Code: " + responseCode);
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.out.println("[Request #" + reqId + "] ERROR: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("--------------------------------------------------");
        System.out.println("STRESS TEST SUMMARY:");
        System.out.println("Total Time Elapsed : " + duration + " ms");
        System.out.println("Successful Requests: " + successCount.get());
        System.out.println("Failed Requests    : " + failCount.get());
        System.out.println("Success Rate       : " + (successCount.get() * 100 / totalThreads) + "%");
    }
}