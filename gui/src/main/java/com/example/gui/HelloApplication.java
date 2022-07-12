/**
 * this is the start of our program
 * it establishes connection with our server
 * and calls the login scene
 */
package com.example.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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
    /**
     * command output
     */
    private static ObjectOutputStream out;
    /**
     * data input
     */
    private static ObjectOutputStream fout;
    /**
     * file bytes input for downloading
     */
    private static ObjectInputStream fin;
    /**
     * file bytes output for uploading
     */
    private static ObjectInputStream in;

    /**
     * makes and shows our scene
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        fxmlLoader.setController(new LoginController(in,out,fin,fout));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setTitle("Discord");
        stage.getIcons().add(new Image("C:\\discord\\logo.png"));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * establishes connection
     * and makes directory
     * @param args
     */
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
