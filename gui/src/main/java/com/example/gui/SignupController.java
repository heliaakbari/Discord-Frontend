package com.example.gui;

import javafx.event.Event;
import javafx.fxml.FXML;
import com.example.mutual.*;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import static java.nio.file.Files.readAllBytes;

public class SignupController implements Initializable {

    private Stage stage;
    private Scene scene;

    private boolean ableToSignup;

    @FXML
    private ChoiceBox<String> statusChoiceBox;

    @FXML
    private Text login_signup;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username1;

    @FXML
    private TextField password1;

    @FXML
    private Button signup_button;

    @FXML
    private Button imageButton;

    @FXML
    private Text login_username_description;

    @FXML
    private Text login_username_description1;

    @FXML
    private Text image_warning;

    private byte[] photo;
    private String photoFormat;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ObjectInputStream fin;
    private ObjectOutputStream fout;

    private String[] statuses = {"online","idle","do_not_disturb","invisible","none"};

    public SignupController(){

    }

    public SignupController(ObjectInputStream in, ObjectOutputStream out,ObjectInputStream fin, ObjectOutputStream fout){
        this.in = in;
        this.out = out;
        this.fin = fin;
        this.fout = fout;
    }

    @FXML
    public void signupOnButton(Event event) {
        ableToSignup = true;
        login_username_description.setText(null);
        login_username_description1.setText(null);

        // regex checking
        if (!username.getText().matches("^[0-9a-zA-Z]{6,20}$")) {
            // tell user
            login_username_description.setText("username must be at least 6 and at most 20 characters and only containing english alphabet and numbers");
            login_username_description.setFill(Color.RED);
            ableToSignup = false;
        }
        if (!password.getText().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$")) {
            // tell user
            login_username_description1.setText("password must be at least 8 and at most 20 characters and contain capital and small english alphabets and numbers");
            login_username_description1.setFill(Color.RED);
            ableToSignup = false;
        }


        // signing up
        if (ableToSignup) {

            try {

                User newUser = new User(username.getText(), password.getText(), username1.getText());

                String status = statusChoiceBox.getValue();
                if (status.equals("none"))
                    newUser.setStatus(null);
                else if (status.equals("do not disturb"))
                    newUser.setStatus("do_not_disturb");
                else
                    newUser.setStatus(statusChoiceBox.getValue());

                newUser.setProfilePhoto(photo, photoFormat);
                newUser.setPhoneNum(password1.getText());

            out.writeObject(Command.newUser(newUser));
            Data data = (Data) in.readObject();

            if (!((boolean) data.getPrimary())) {
                System.out.println("not successful");
                login_username_description.setText("this username is taken, try another one");
                login_username_description.setFill(Color.RED);
            } else {
                System.out.println("correct");
                FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("friends-view.fxml"));
                FriendsController friendsController = new FriendsController(in, out, fin, fout, username.getText());
                fxmlLoader.setController(friendsController);
                stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
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
}

    @FXML
    public void changeToLogin(Event event){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        fxmlLoader.setController(new LoginController(in,out,fin,fout));
        stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
        try {
            scene = new Scene(fxmlLoader.load(), 1000, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void imageOnButton(Event event){

        FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);

        String fileNameAndType = dialog.getFile();
        String path = dialog.getDirectory() + "//" + dialog.getFile();

        try {
            photo = readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if(photo.length > 100000){
            image_warning.setText("your image size is more than 100kB");
            image_warning.setFill(Color.RED);
            photo = null;
        }
        else {
            image_warning.setText("");
            String[] splitName = fileNameAndType.split("\\.");
            photoFormat = splitName[splitName.length - 1];
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statusChoiceBox.getItems().addAll(statuses);
        statusChoiceBox.setValue(statuses[4]);
    }
}

