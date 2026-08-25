package com.example.engine;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PingScanner {

    public static void main(String[] args) {
        String host = "example.com";
        try {
            InetAddress address = InetAddress.getByName(host);
            boolean reachable = address.isReachable(5000);
            System.out.println("Host " + host + " is reachable: " + reachable);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
