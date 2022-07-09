package com.example.gui;

import com.example.mutual.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class HelloApplication extends Application {

    private static Socket socket;
    private static Socket fileSocket;
    private static ObjectOutputStream out;
    private static ObjectOutputStream fout;
    private static ObjectInputStream fin;
    private static ObjectInputStream in;
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        fxmlLoader.setController(new LoginController(in,out,fin,fout));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        Image logo = new Image("C:\\Jetbrains\\AP\\DiscordGUI\\Discord-Frontend\\logo.png");
        stage.getIcons().add(logo);
        stage.setTitle("Discord");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        File f1 = new File("C:\\discord");
        boolean bool = f1.mkdir();

        try {
            socket = new Socket("localhost", 8643);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            fileSocket = new Socket("localhost", 8999);
            fout = new ObjectOutputStream(fileSocket.getOutputStream());
            fin = new ObjectInputStream(fileSocket.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        launch();
    }
}
