package com.example.gui;

import com.example.mutual.Command;
import com.example.mutual.Data;
import com.example.mutual.User;
import com.example.mutual.UserShort;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static java.nio.file.Files.readAllBytes;

public class SettingController {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ObjectOutputStream fout;
    private ObjectInputStream fin;
    private User currentUser;
    private UserShort userShort;
    private Stage myStage;
    @FXML
    private Pane imageHolder;
    @FXML
    private Text currentUsername;
    @FXML
    private Text currentPassword;
    @FXML
    private Text currentEmail;
    @FXML
    private Text currentPhoneNum;
    @FXML
    private TextField newUsername;
    @FXML
    private TextField newPassword;
    @FXML
    private TextField newEmail;
    @FXML
    private TextField newPhoneNum;
    @FXML
    private Text usernameWarning;
    @FXML
    private Text imageWarning;
    @FXML
    private Text passwordWarning;
    @FXML
    private Text emailWarning;

    @FXML
    private Button changeUsername;
    @FXML
    private Button changePassword;
    @FXML
    private Button changeEmail;
    @FXML
    private Button changePhoneNum;

    public SettingController(ObjectOutputStream out, ObjectInputStream in,ObjectOutputStream fout, ObjectInputStream fin, User currentUser, UserShort userShort,Stage stage) {
        this.out = out;
        this.in = in;
        this.currentUser = currentUser;
        this.userShort = userShort;
        this.fin = fin;
        this.fout = fout;
        myStage = stage;
    }

    @FXML
    public void initialize(){
        imageHolder.getChildren().add(userShort.profileStatus(100.0));
        currentUsername.setText(currentUser.getUsername());
        currentPassword.setText(currentUser.getPassword());
        currentEmail.setText(currentUser.getEmail());
        currentPhoneNum.setText(currentUser.getPhoneNum());
        myStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.out.println("closed");
                FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("friends-view.fxml"));
                FriendsController friendsController = new FriendsController(in,out,fin,fout, currentUser.getUsername());
                fxmlLoader.setController(friendsController);
                Stage newStage = new Stage();
                newStage.setTitle("Discord");
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load(), 1000, 600);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                newStage.setScene(scene);
                myStage.close();
                newStage.show();
            }
        });
    }

    @FXML
    public void changeProfileOnButton(Event e){
        FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);

        String fileNameAndType = dialog.getFile();
        String path = dialog.getDirectory() + "//" + dialog.getFile();
        if(path.contains("null")){
            System.out.println(path.contains("null"));
            return;
        }
        byte[] photo = null;
        String photoFormat = null;
        try {
            photo = readAllBytes(Paths.get(path));
        } catch (IOException ex) {
            System.out.println("error while reading bytes");
            ex.printStackTrace();
            return;
        }

        if (photo.length > 100000){
            imageWarning.setText("your image size is more than 100kB");
            imageWarning.setFill(Color.RED);
            photo = null;
            return;
        }
        else {
            imageWarning.setText("");
            String[] splitName = fileNameAndType.split("\\.");
            photoFormat = splitName[splitName.length - 1];
        }
        try {
            System.out.println(currentUser.getUsername());
            out.writeObject(Command.changeProfilePhoto(currentUser.getUsername(), photo, photoFormat));
            in.readObject();
        } catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
            System.out.println("error while transferring cmd and data");
        }
        imageHolder.getChildren().remove(0);
        imageHolder.getChildren().add(userShort.profileStatus(100.0));

    }

    @FXML
    public void changeUsernameOnButton(Event e) {
        if (!newUsername.getText().matches("^[0-9a-zA-Z]{6,20}$")){
            usernameWarning.setText("username must be at least 6 and at most 20 characters and only containing english alphabet and numbers");
            usernameWarning.setFill(Color.RED);
            return;
        }
        try {
            out.writeObject(Command.changeUsername(currentUser.getUsername(), newUsername.getText()));
            Data data = (Data) in.readObject();
            if (data.getKeyword().equals("checkChangeUsername") && (boolean) data.getPrimary()){
                usernameWarning.setText("username changed successfully!");
                usernameWarning.setFill(Color.GREEN);

                currentUsername.setText(newUsername.getText());
                currentUser.setUsername(newUsername.getText());
                userShort.setUsername(newUsername.getText());
                newUsername.setText(null);
                newUsername.setVisible(false);
                changeUsername.setVisible(false);

            }
            else {
                usernameWarning.setText("this username is already taken");
                usernameWarning.setFill(Color.RED);
            }
        } catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }

    }

    @FXML
    public void changePasswordOnButton(Event e){
        if (!newPassword.getText().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$")){
            passwordWarning.setText("password must be at least 8 and at most 20 characters and contain capital and small english alphabets and numbers");
            passwordWarning.setFill(Color.RED);
            return;
        }
        try {
            out.writeObject(Command.changeInfo(currentUser.getUsername(), "password", newPassword.getText()));
            passwordWarning.setText("password changed successfully");
            passwordWarning.setFill(Color.GREEN);
            currentPassword.setText(newPassword.getText());
            currentUser.setPassword(newPassword.getText());
            newPassword.setText(null);

            newPassword.setVisible(false);
            changePassword.setVisible(false);

        } catch (IOException  ex){
            ex.printStackTrace();
        }


    }

    @FXML
    public void changeEmailOnButton(Event e){
        if (!newEmail.getText().matches("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")){
            emailWarning.setText("email format isn't correct");
            emailWarning.setFill(Color.RED);
            return;
        }
        try{
            out.writeObject(Command.changeInfo(currentUser.getUsername(), "email", newEmail.getText()));
            emailWarning.setText("email changed successfully");
            emailWarning.setFill(Color.GREEN);
            currentEmail.setText(newEmail.getText());
            currentUser.setEmail(newEmail.getText());
            newEmail.setText(null);
            newEmail.setVisible(false);
            changeEmail.setVisible(false);
        } catch (IOException  ex){
            ex.printStackTrace();
        }

    }
    @FXML
    public void changePhoneNumOnButton(Event e){
        try {;
            out.writeObject(Command.changeInfo(currentUser.getUsername(), "phone", newPhoneNum.getText()));
            currentPhoneNum.setText(newPhoneNum.getText());
            currentUser.setPhoneNum(newPhoneNum.getText());
            newPhoneNum.setText(null);
        } catch (IOException ex){
            ex.printStackTrace();
        }

        newPhoneNum.setVisible(false);
        changePhoneNum.setVisible(false);
    }
    @FXML
    public void editButtonClicked(Event e){
        Node button = (Node) e.getSource();
        String id = button.getId();
        switch (id){
            case "editUsername" :
                newUsername.setVisible(true);
                changeUsername.setVisible(true);
                break;
            case "editPassword" :
                newPassword.setVisible(true);
                changePassword.setVisible(true);
                break;
            case "editEmail" :
                newEmail.setVisible(true);
                changeEmail.setVisible(true);
                break;
            case "editPhoneNum" :
                newPhoneNum.setVisible(true);
                changePhoneNum.setVisible(true);
                break;
        }


    }

    @FXML
    public void onScreenClicked(Event e){
        newUsername.setVisible(false);
        newPassword.setVisible(false);
        newEmail.setVisible(false);
        newPhoneNum.setVisible(false);
        changeUsername.setVisible(false);
        changePassword.setVisible(false);
        changeEmail.setVisible(false);
        changePhoneNum.setVisible(false);

        usernameWarning.setText(null);
        passwordWarning.setText(null);
        emailWarning.setText(null);
    }

    @FXML
    public void logoutOnButton(Event e){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        fxmlLoader.setController(new LoginController(in,out,fin,fout));
         myStage = (Stage)(((Node) e.getSource()).getScene().getWindow());
         myStage.close();
         Stage newStage = new Stage();
         newStage.setResizable(false);
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 1000, 600);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        newStage.setScene(scene);
        myStage.close();
        newStage.show();
    }


}
