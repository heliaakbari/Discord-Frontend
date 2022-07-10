package com.example.gui;

import com.example.mutual.Command;
import com.example.mutual.Data;
import com.example.mutual.User;
import com.example.mutual.UserShort;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingController implements Initializable {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ObjectOutputStream fout;
    private ObjectInputStream fin;
    private User currentUser;
    private UserShort userShort;

    @FXML
    private Pane imageHolder;
    @FXML
    private Button changeProfileButton;
    @FXML
    private Button changeUsernameButton;
    @FXML
    private Text currentUsername;
    @FXML
    private Text password;
    @FXML
    private Text email;
    @FXML
    private Text phoneNum;
    @FXML
    private TextField newUsernameField;
    @FXML
    private Text usernameWarning;

    public SettingController(ObjectOutputStream out, ObjectInputStream in, ObjectOutputStream fout, ObjectInputStream fin, User currentUser, UserShort userShort) {
        this.out = out;
        this.in = in;
        this.fout = fout;
        this.fin = fin;
        this.currentUser = currentUser;
        this.userShort = userShort;
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle){
        imageHolder.getChildren().add(userShort.profileStatus(60.0));
        currentUsername.setText(currentUser.getUsername());
        password.setText(currentUser.getPassword());
        email.setText(currentUser.getEmail());
        phoneNum.setText(currentUser.getPhoneNum());
    }

    @FXML
    public void changeProfileOnButton(){

    }

    @FXML
    public void changeUsernameOnButton(){
        try {
            out.writeObject(Command.changeUsername(currentUser.getUsername(), newUsernameField.getText()));
            Data data = (Data) in.readObject();
            if (data.getKeyword().equals("checkChangeUsername") && (boolean) data.getPrimary()){
                usernameWarning.setText("username changed successfully!");
                newUsernameField.setText(null);
            }
            else {
                usernameWarning.setText("this username is already taken");
            }
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }
}
