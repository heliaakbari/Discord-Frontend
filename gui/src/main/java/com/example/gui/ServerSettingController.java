package com.example.gui;

import com.example.mutual.Command;
import com.example.mutual.Data;
import com.example.mutual.Role;
import com.example.mutual.UserShort;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ServerSettingController {

    protected Role role;
    protected String currentUser;
    protected String currentServer;
    protected String currentChannel;

    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    protected ObjectOutputStream fout;
    protected ObjectInputStream fin;

    protected ArrayList<String> channels;
    protected HashMap<String, Role> serverMembers;
    protected ArrayList<UserShort> channelMembers;
    protected ArrayList<UserShort> friends;

    @FXML
    protected Tab addUserToChannel;
    @FXML
    protected Tab deleteChannel;
    @FXML
    protected Tab createChannel;
    @FXML
    protected Tab deleteUserFromServer;
    @FXML
    protected Tab deleteUserFromChannel;
    @FXML
    protected Tab addUserToServer;
    @FXML
    protected Tab serverRoles;
    @FXML
    protected Tab chatHistory;
    @FXML
    protected Tab leave;
    @FXML
    protected Tab rename_delete_server;

    @FXML
    protected ChoiceBox channels1;
    @FXML
    protected ChoiceBox channels2;
    @FXML
    protected ChoiceBox channels3;
    @FXML
    protected ChoiceBox friends1;
    @FXML
    protected ChoiceBox friends2;
    @FXML
    protected ChoiceBox serverMembers1;
    @FXML
    protected ChoiceBox channelMembers1;

    @FXML
    protected Text createChannelWarning;
    @FXML
    protected TextField newChannelName;
    @FXML
    protected Text newServerName;
    @FXML
    protected TextField serverNameWarning;


    public ServerSettingController(Role role, String currentUser, String currentServer, ObjectOutputStream out, ObjectInputStream in, ObjectOutputStream fout, ObjectInputStream fin) {
        this.in = in;
        this.out = out;
        this.role = role;
        this.currentServer = currentServer;
        this.currentUser = currentUser;
        this.fin = fin;
        this.fout = fout;
    }

    @FXML
    public void initialize() {
        new GetRole(this).restart();
    }

    @FXML
    public void addToServerOnButton(Event e) {

        ArrayList<String> choice = new ArrayList<>();
        choice.add((String) friends2.getSelectionModel().getSelectedItem());
        try {
            out.writeObject(Command.addPeopleToServer(currentUser, currentServer, choice));
            in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

    }

    @FXML
    public void createChannelOnButton(Event e) {

        String channelName = newChannelName.getText();
        if (channelName.equals("")) {
            createChannelWarning.setText("type a name first!");
            createChannelWarning.setFill(Color.RED);
        }
        else {
            try {
                out.writeObject(Command.newChannel(currentUser, currentServer, channelName));
                Data data = (Data) in.readObject();
                if (!(boolean) data.getPrimary()) {
                    createChannelWarning.setText("this name is taken, try another one!");
                    createChannelWarning.setFill(Color.RED);
                }
                else {
                    createChannelWarning.setText("channel created successfully");
                    createChannelWarning.setFill(Color.GREEN);
                }
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }

        }
    }

    @FXML
    public void addToChannelOnButton(Event e) {
        String channel = (String) channels1.getSelectionModel().getSelectedItem();
        String person = (String) friends1.getSelectionModel().getSelectedItem();

        try{
            out.writeObject(Command.addOneMemberToChannel(currentUser, person, currentServer, channel));
            Data data = (Data) in.readObject();
        } catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }

    }

    @FXML
    public void deleteChannelOnButton(Event e) {
        String channel = (String) channels2.getSelectionModel().getSelectedItem();
        try {
            out.writeObject(Command.deleteChannel(currentUser,currentServer, channel));
            Data data = (Data) in.readObject();

        } catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    public void deleteFromServerOnButton(Event e) {
        String person = (String) serverMembers1.getSelectionModel().getSelectedItem();
        try {
            out.writeObject(Command.banFromServer(person, currentServer));
            Data data = (Data) in.readObject();

        } catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }

    }

    @FXML
    public void deleteFromChannelOnButton(Event e) {
        String person = (String) channelMembers1.getSelectionModel().getSelectedItem();
        String channel = (String) channels3.getSelectionModel().getSelectedItem();
        try {
            out.writeObject(Command.banFromChannel(person, currentServer, channel));
            Data data = (Data) in.readObject();

        } catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }

    }

    @FXML
    public void leaveFromChannelOnButton(Event e){

    }

    @FXML
    public void leaveFromServerOnButton(Event e){
        try{
            out.writeObject(Command.banFromServer(currentUser, currentServer));
            Data data = (Data) in.readObject();
        } catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }

        changeToFriendsView(e);
    }

    @FXML
    public void deleteServerOnButton(Event e){
        try{
            out.writeObject(Command.deleteServer(currentUser, currentServer));
            Data data = (Data) in.readObject();
        } catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }

        changeToFriendsView(e);
    }

    @FXML
    public void renameServerOnButton(Event e){

    }


    @FXML
    public void addUserToChannel(Event e) {
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;

        new AddChannelsAndFriends(this).restart();
    }

    public void changeToFriendsView(Event event){
        FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("friends-view.fxml"));
        FriendsController friendsController = new FriendsController(in, out, fin, fout, currentUser);
        fxmlLoader.setController(friendsController);
        Stage stage = (Stage) (((Node) event.getSource()).getScene().getWindow());
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 1000, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(scene);
        stage.show();
    }

    public void deleteChannel(Event e) {
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
        new AddChannels(this).restart();
    }

    public void deleteUserFromServer(Event e) {
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
        new AddServerMembers(this).restart();
    }

    public void deleteUserFromChannel(Event e) {
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
        new AddChannelsAndChannelMembers(this).restart();
    }

    public void addUserToServer(Event e) {
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
        new AddFriends(this).restart();
    }

    public void serverRoles(Event e) {
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
    }

    public void chatHistory(Event e) {
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
    }

    public void leave(Event e) {
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
    }


}

class GetRole extends Service<Void> {
    ServerSettingController ssc;

    public GetRole(ServerSettingController ssc) {
        this.ssc = ssc;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ssc.out.writeObject(Command.getRole(ssc.currentUser, ssc.currentServer));
                Data data = (Data) ssc.in.readObject();
                ssc.role = (Role) data.getPrimary();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        ssc.leave.setDisable(false);
        ssc.addUserToServer.setDisable(false);
        ArrayList<String> abilities = ssc.role.getAvailableAbilities();
        for (String ability : abilities) {
            switch (ability) {
                case "create channel" -> ssc.createChannel.setDisable(false);
                case "remove channel" -> ssc.deleteChannel.setDisable(false);
                case "remove member from server " -> ssc.deleteUserFromServer.setDisable(false);
                case "remove member from channel" -> ssc.deleteUserFromChannel.setDisable(false);
                case "change server name" -> ssc.rename_delete_server.setDisable(false);
                case "see chat history" -> ssc.chatHistory.setDisable(false);
                case "delete server" -> ssc.rename_delete_server.setDisable(false);

            }
        }

        if (ssc.role.getValues().length() == 9) {
            ssc.addUserToChannel.setDisable(false);
            ssc.serverRoles.setDisable(false);
        }

    }
}

// for delete user from channel
class AddChannelsAndChannelMembers extends Service<Void> {

    ServerSettingController ssc;

    public AddChannelsAndChannelMembers(ServerSettingController ssc) {
        this.ssc = ssc;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ssc.out.writeObject(Command.userChannels(ssc.currentUser, ssc.currentServer));
                Data data = (Data) ssc.in.readObject();
                ssc.channels = (ArrayList<String>) data.getPrimary();


                ssc.out.writeObject(Command.getChannelMembers(ssc.currentUser, ssc.currentServer, ssc.currentChannel));
                data = (Data) ssc.in.readObject();
                ssc.channelMembers = (ArrayList<UserShort>) data.getPrimary();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        ssc.channels3.getItems().clear();
        ssc.channels3.getItems().addAll(ssc.channels);

        ssc.channelMembers1.getItems().clear();
        for (UserShort user : ssc.channelMembers) {
            ssc.channels3.getItems().add(user.getUsername());
        }
    }
}

// for delete user from server
class AddServerMembers extends Service<Void> {

    ServerSettingController ssc;

    public AddServerMembers(ServerSettingController ssc) {
        this.ssc = ssc;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ssc.out.writeObject(Command.getServerMembers(ssc.currentUser, ssc.currentServer));
                Data data = (Data) ssc.in.readObject();
                ssc.serverMembers = (HashMap<String, Role>) data.getPrimary();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        ssc.serverMembers1.getItems().clear();
        for (HashMap.Entry<String, Role> entry : ssc.serverMembers.entrySet()) {
            ssc.serverMembers1.getItems().add(entry.getKey());
        }
    }
}

// for add user to server
class AddFriends extends Service<Void> {

    ServerSettingController ssc;

    public AddFriends(ServerSettingController ssc) {
        this.ssc = ssc;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ssc.out.writeObject(Command.getFriends(ssc.currentUser));
                Data data = (Data) ssc.in.readObject();
                ssc.friends = (ArrayList<UserShort>) data.getPrimary();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        ssc.friends2.getItems().clear();
        for (UserShort user : ssc.friends) {
            ssc.friends2.getItems().add(user.getUsername());
        }
    }
}

// for delete channel
class AddChannels extends Service<Void> {
    ServerSettingController ssc;

    public AddChannels(ServerSettingController ssc) {
        this.ssc = ssc;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ssc.out.writeObject(Command.userChannels(ssc.currentUser, ssc.currentServer));
                Data data = (Data) ssc.in.readObject();
                ssc.channels = (ArrayList<String>) data.getPrimary();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        ssc.channels2.getItems().clear();
        ssc.channels2.getItems().addAll(ssc.channels);

    }
}

// for add user to channel
class AddChannelsAndFriends extends Service<Void> {

    ServerSettingController ssc;

    public AddChannelsAndFriends(ServerSettingController ssc) {
        this.ssc = ssc;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ssc.out.writeObject(Command.userChannels(ssc.currentUser, ssc.currentServer));
                Data data = (Data) ssc.in.readObject();
                ssc.channels = (ArrayList<String>) data.getPrimary();

                ssc.out.writeObject(Command.getFriends(ssc.currentUser));
                data = (Data) ssc.in.readObject();
                ssc.friends = (ArrayList<UserShort>) data.getPrimary();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        ssc.channels1.getItems().clear();
        ssc.channels1.getItems().addAll(ssc.channels);

        ssc.friends1.getItems().clear();
        for (UserShort user : ssc.friends) {
            ssc.friends1.getItems().add(user.getUsername());
        }
    }
}

class OpenServerRoles extends Service<Void> {
    ServerSettingController ssc;

    public OpenServerRoles(ServerSettingController ssc) {
        this.ssc = ssc;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Command cmd = Command.getServerMembers(ssc.currentUser, ssc.currentServer);
                ssc.out.writeObject(cmd);
                Data data = (Data) ssc.in.readObject();
                ssc.serverMembers = (HashMap<String, Role>) data.getPrimary();

                return null;
            }
        };
    }

    @Override
    protected void succeeded() {

    }
}
