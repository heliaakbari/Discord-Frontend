package com.example.gui;

import com.example.mutual.Command;
import com.example.mutual.Data;
import com.example.mutual.User;
import com.example.mutual.UserShort;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import static java.nio.file.Files.readAllBytes;

public class SettingController implements Initializable {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private User currentUser;
    private UserShort userShort;

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

    public SettingController(ObjectOutputStream out, ObjectInputStream in, User currentUser, UserShort userShort) {
        this.out = out;
        this.in = in;
        this.currentUser = currentUser;
        this.userShort = userShort;
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle){
        imageHolder.getChildren().add(userShort.profileStatus(100.0));
        currentUsername.setText(currentUser.getUsername());
        currentPassword.setText(currentUser.getPassword());
        currentEmail.setText(currentUser.getEmail());
        currentPhoneNum.setText(currentUser.getPhoneNum());
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
        }
        else {
            imageWarning.setText("");
            String[] splitName = fileNameAndType.split("\\.");
            photoFormat = splitName[splitName.length - 1];
        }
        try {
            System.out.println(currentUser.getUsername());
            System.out.println(photo.length);
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
