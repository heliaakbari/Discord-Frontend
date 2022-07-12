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
    protected ArrayList<Role> serverMembers;
    protected ArrayList<UserShort> channelMembers;
    protected ArrayList<UserShort> friends;


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

    public void addUserToChannel(Event e){
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
    }

    public void createChannel(Event e){
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
    }

    public void deleteChannel(Event e){
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
    }

    public void deleteUserFromServer(Event e){
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
    }

    public void deleteUserFromChannel(Event e){
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
    }

    public void addUserToServer(Event e){
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
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

class AddChannelMembers extends Service<Void> {

    ServerSettingController ssc;

    public AddChannelMembers(ServerSettingController ssc) {
        this.ssc = ssc;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ssc.out.writeObject(Command.getChannelMembers(ssc.currentUser, ssc.currentServer, ssc.currentChannel));
                Data data = (Data) ssc.in.readObject();
                ssc.channelMembers = (ArrayList<UserShort>) data.getPrimary();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        ssc.channelMembers1.getItems().clear();
        ssc.channelMembers1.getItems().addAll(ssc.channelMembers);
    }
}

class AddServerMembers extends Service<Void>{

    ServerSettingController ssc;
    ChoiceBox serverMembers;

    public AddServerMembers(ServerSettingController ssc, ChoiceBox serverMembers) {
        this.ssc = ssc;
        this.serverMembers = serverMembers;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ssc.out.writeObject(Command.getServerMembers(ssc.currentUser, ssc.currentServer));
                Data data = (Data) ssc.in.readObject();
                ssc.serverMembers = (ArrayList<Role>) data.getPrimary();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        serverMembers.getItems().clear();
        serverMembers.getItems().addAll(ssc.serverMembers);
    }
}

class AddFriends extends Service<Void>{

    ServerSettingController ssc;
    ChoiceBox friends;

    public AddFriends(ServerSettingController ssc, ChoiceBox friends) {
        this.ssc = ssc;
        this.friends = friends;
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
        friends.getItems().clear();
        friends.getItems().addAll(ssc.friends);
    }
}

class AddChannels extends Service<Void>{

    ServerSettingController ssc;
    ChoiceBox channels;

    public AddChannels(ServerSettingController ssc, ChoiceBox channels) {
        this.ssc = ssc;
        this.channels = channels;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ssc.out.writeObject(Command.userChannels(ssc.currentUser, ssc.currentServer));
                Data data = (Data) ssc.in.readObject();
                ssc.channels = (ArrayList<String >) data.getPrimary();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        channels.getItems().clear();
        channels.getItems().addAll(ssc.channels);
    }
}


