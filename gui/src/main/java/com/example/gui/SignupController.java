package com.example.gui;

import javafx.event.Event;
import javafx.fxml.FXML;
import com.example.mutual.*;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class SignupController implements Initializable {

    private Stage stage;
    private Scene scene;

    @FXML
    private ChoiceBox<String> statusChoiceBox;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String[] statuses = {"online","idle","do not disturb","invisible","none"};

    public SignupController(){
        ;
    }

    public SignupController(ObjectInputStream in, ObjectOutputStream out){
        this.in = in;
        this.out = out;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
            statusChoiceBox.getItems().addAll(statuses);
            statusChoiceBox.setValue(statuses[4]);
    }

    @FXML
    public void changeToLogin(Event event){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        fxmlLoader.setController(new LoginController(in,out));
        stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
        try {
            scene = new Scene(fxmlLoader.load(), 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(scene);
        stage.show();
    }
}
