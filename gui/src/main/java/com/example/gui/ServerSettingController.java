package com.example.gui;

import com.example.mutual.Command;
import com.example.mutual.Data;
import com.example.mutual.Role;
import com.example.mutual.UserShort;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tab;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
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
//    @FXML
//    protected Tab ;
//    @FXML
//    protected Tab ;

    @FXML
    protected ChoiceBox channels1;
    @FXML
    protected ChoiceBox channels2 ;
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


    public ServerSettingController(Role role,String currentUser,String currentServer,ObjectOutputStream out,ObjectInputStream in,ObjectOutputStream fout,ObjectInputStream fin){
        this.in = in;
        this.out = out;
        this.role = role;
        this.currentServer = currentServer;
        this.currentUser = currentUser;
        this.fin = fin;
        this.fout = fout;
    }

    @FXML
    public void initialize(){
        new GetRole(this).restart();
    }

    public void addUserToChannel(Event e){
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;

        new AddChannelsAndFriends(this).restart();
    }

    public void deleteChannel(Event e){
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
        new AddChannels(this).restart();
    }

    public void deleteUserFromServer(Event e){
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
        new AddServerMembers(this).restart();
    }

    public void deleteUserFromChannel(Event e){
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
        new AddChannelsAndChannelMembers(this).restart();
    }

    public void addUserToServer(Event e){
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
        new AddFriends(this).restart();
    }

    public void serverRoles(Event e){
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
    }

    public void chatHistory(Event e){
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
    }

    public void leave(Event e){
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
    }
}

class GetRole extends Service<Void>{
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
        ArrayList<String> abilities = ssc.role.getAvailableAbilities();
        for (String ability: abilities) {
//            switch (ability){
//                case "create channel" ->
//                case "remove channel" ->
//                case "remove channel" ->
//                case "restrict member" ->
//                case "ban member" ->
//                case "change server name" ->
//                case "see chat history" ->
//                case "delete server" ->
//                case
//                case
//
//            }
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
        for (UserShort user: ssc.channelMembers) {
            ssc.channels3.getItems().add(user.getUsername());
        }
    }
}

// for delete user from server
class AddServerMembers extends Service<Void>{

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
            for (HashMap.Entry<String,Role> entry : ssc.serverMembers.entrySet()) {
                ssc.serverMembers1.getItems().add(entry.getKey());
            }
    }
}

// for add user to server
class AddFriends extends Service<Void>{

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
        for (UserShort user: ssc.friends) {
            ssc.friends2.getItems().add(user.getUsername());
        }
    }
}

// for delete channel
class AddChannels extends Service<Void>{
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
class AddChannelsAndFriends extends Service<Void>{

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
        for (UserShort user: ssc.friends) {
            ssc.friends1.getItems().add(user.getUsername());
        }
    }
}

class OpenServerRoles extends Service<Void>{
    ServerSettingController ssc;

    public OpenServerRoles(ServerSettingController ssc){
        this.ssc = ssc;
    }
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Command cmd = Command.getServerMembers(ssc.currentUser,ssc.currentServer);
                ssc.out.writeObject(cmd);
                Data data = (Data) ssc.in.readObject();
                ssc.serverMembers =(HashMap<String, Role>) data.getPrimary();

                return null;
            }
        };
    }

    @Override
    protected void succeeded() {

    }
}
