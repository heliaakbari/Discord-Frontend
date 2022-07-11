package com.example.gui;

import com.example.mutual.Command;
import com.example.mutual.Data;
import com.example.mutual.User;
import com.example.mutual.UserShort;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
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
    public void changeUsernameOnButton(Event e){
        try {
            out.writeObject(Command.changeUsername(currentUser.getUsername(), newUsername.getText()));
            Data data = (Data) in.readObject();
            if (data.getKeyword().equals("checkChangeUsername") && (boolean) data.getPrimary()){
                usernameWarning.setText("username changed successfully!");
                currentUsername.setText(newUsername.getText());
                currentUser.setUsername(newUsername.getText());
                userShort.setUsername(newUsername.getText());
                newUsername.setText(null);


            }
            else {
                usernameWarning.setText("this username is already taken");
            }
        } catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }

    }

    @FXML
    public void changePasswordOnButton(Event e){

    }
    @FXML
    public void changeEmailOnButton(Event e){

    }
    @FXML
    public void changePhoneNumOnButton(Event e){

    }
    @FXML
    public void editButtonClicked(Event e){

    }
}
