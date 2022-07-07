package com.example.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import com.example.mutual.*;
/**
 * a seperated thread to wait for commands
 */
public class Listener extends Thread {
    private ClientHandler clientHandler;
    private ObjectInputStream in;


    public Listener(ClientHandler clientHandler, ObjectInputStream in) {
        this.clientHandler = clientHandler;
        this.in = in;
    }

    @Override
    public void run() {
        Command cmd;
        while (true) {
            try {
                cmd = (Command) in.readObject();
                System.out.println("got a command: " + cmd.getKeyword());
                clientHandler.getCommandFromListener(cmd);
                if (cmd.getKeyword().equals("exit")) {
                    return;
                }

            } catch (IOException e) {
                clientHandler.getCommandFromListener(Command.exit());
                clientHandler.interrupt();
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
