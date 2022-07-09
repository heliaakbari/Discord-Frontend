package com.example.gui;

import com.example.mutual.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class LoginController {

    private  ObjectOutputStream out;
    private  ObjectInputStream in;
    private  ObjectOutputStream fout;
    private  ObjectInputStream fin;

    private Stage stage;
    private Scene scene;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private Button login_button;

    @FXML
    private Text login_signup;

    public LoginController(){
        ;
    }


    public LoginController(ObjectInputStream in, ObjectOutputStream out,ObjectInputStream fin, ObjectOutputStream fout){
        this.in = in;
        this.out = out;
        this.fin = fin;
        this.fout = fout;
    }

    @FXML
    public void loginOnButton(Event event) {
        try {
            out.writeObject(Command.login(username.getText(),password.getText()));
            Data data = (Data) in.readObject();
            if((boolean) data.getPrimary()==false){
                System.out.println("not correct");
                //and other actions
            }
            else {
                System.out.println("correct");
                //and other actions
                FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("friends-view.fxml"));
                FriendsController friendsController = new FriendsController(in,out,fin,fout,username.getText());
                fxmlLoader.setController(friendsController);
                stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
                try {
                    scene = new Scene(fxmlLoader.load(), 1000, 600);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void changeToSignUp(Event event){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("signup-view.fxml"));
        fxmlLoader.setController(new SignupController(in,out,fin,fout));
        stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
        try {
            scene = new Scene(fxmlLoader.load(), 1000, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(scene);
        stage.show();
    }
}
