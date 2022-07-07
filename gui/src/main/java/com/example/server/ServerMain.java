package com.example.server;
import com.example.mutual.*;
/**
 * starting point of the server side
 */
public class ServerMain {
    public static void main(String[] args) {
        ServerSide ss = new ServerSide();
        ss.startServer();
    }
}
