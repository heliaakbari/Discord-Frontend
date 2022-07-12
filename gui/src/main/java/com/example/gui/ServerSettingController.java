package com.example.gui;

import com.example.mutual.Command;
import com.example.mutual.Data;
import com.example.mutual.Role;
import com.example.mutual.UserShort;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    protected GridPane members_grid;
    @FXML
    protected Tab addUserToChannel;
    @FXML
    protected ChoiceBox<String> changerole_member;
    @FXML
    protected Button changerole_button;
    @FXML
    protected ChoiceBox<String> changerole_role;
    @FXML
    protected GridPane role_desc;
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
    protected Button create_channel;
    @FXML
    protected Button delete_channel;
    @FXML
    protected Button delete_from_server;
    @FXML
    protected Button delete_from_channel;
    @FXML
    protected Button add_to_server;
    @FXML
    protected Button chat_history;
    @FXML
    protected Button change_name;
    @FXML
    protected Button pin_msg;
    @FXML
    protected ChoiceBox<String> choicebox_createrole;
    @FXML
    protected Button create_role;
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
    protected TextField role_name;
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

    public void changeRole(Event e){
        Button btn = (Button) e.getSource();
        Role role = null;
        for(HashMap.Entry<String,Role> entry : serverMembers.entrySet()){
            if (entry.getValue().getRoleName().equals(changerole_role.getValue())){
                role = entry.getValue();
                break;
            }
        }
        if(role == null){
            return;
        }
        Command cmd = Command.changeRole(currentUser,changerole_member.getValue(),currentServer,role);
        try {
            out.writeObject(cmd);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            in.readObject();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        new OpenServerRoles(this).restart();
    }

    public void addRole(Event e){
        String abilities = new String("");
        if(create_channel.getText().equals("yes"))
            abilities = abilities+"1";
        else
            abilities = abilities+"0";
        if(delete_channel.getText().equals("yes"))
            abilities = abilities+"1";
        else
            abilities = abilities+"0";
        if(delete_from_server.getText().equals("yes"))
            abilities = abilities+"1";
        else
            abilities = abilities+"0";
        if(delete_from_channel.getText().equals("yes"))
            abilities = abilities+"1";
        else
            abilities = abilities+"0";
        if(delete_from_server.getText().equals("yes"))
            abilities = abilities+"1";
        else
            abilities = abilities+"0";
        if(change_name.getText().equals("yes"))
            abilities = abilities+"1";
        else
            abilities = abilities+"0";
        if(chat_history.getText().equals("yes"))
            abilities = abilities+"1";
        else
            abilities = abilities+"0";
        if(pin_msg.getText().equals("yes"))
            abilities = abilities+"1";
        else
            abilities = abilities+"0";

        abilities = abilities+"0";

        Role role = new Role(abilities,role_name.getText());
        if(choicebox_createrole.getValue().equals("none")){
            return;
        }
        Command cmd =Command.changeRole(currentUser,choicebox_createrole.getValue(),currentServer,role);
        try {
            out.writeObject(cmd);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            in.readObject();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        ((Button)(e.getSource())).setText("done!");
        new OpenServerRoles(this).restart();
    }

    public void noyes(Event e){
        Button btn =(Button) e.getSource();
        if(btn.getText().equals("yes")){
            btn.setText("no");
        }
        else if(btn.getText().equals("no")){
            btn.setText("yes");
        }
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

    public void serverRolesTab(Event e){
        Tab tab = (Tab) e.getSource();
        if(!tab.isSelected())
            return;
        new OpenServerRoles(this).restart();
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
        ssc.members_grid.getChildren().clear();
        ssc.members_grid.setVgap(10);
        ssc.choicebox_createrole.getItems().clear();
        ssc.choicebox_createrole.getItems().add("none");
        ssc.choicebox_createrole.setValue("none");
        ssc.changerole_member.getItems().clear();
        HashSet<String> roles = new HashSet<>();
        for(HashMap.Entry<String,Role> entry : ssc.serverMembers.entrySet()){
            ssc.members_grid.addColumn(0,new Text(entry.getKey()));
            ssc.members_grid.addColumn(1,new Text(entry.getValue().getRoleName()));
            if(!entry.getKey().equals(ssc.currentUser)) {
                ssc.choicebox_createrole.getItems().add(entry.getKey());
                ssc.changerole_member.getItems().add(entry.getKey());
            }
            if(!entry.getValue().getRoleName().equals("creator")){
                roles.add(entry.getValue().getRoleName());
            }
        }
        ssc.changerole_role.getItems().clear();
        ssc.changerole_role.getItems().addAll(roles);

        ssc.changerole_role.getSelectionModel().selectedItemProperty().addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            Role role = null;
            if(newValue==null){
                return;
            }
            for(HashMap.Entry<String,Role> entry : ssc.serverMembers.entrySet()){
                if (entry.getValue().getRoleName().equals(newValue)){
                    role = entry.getValue();
                    break;
                }
            }
            ssc.role_desc.getChildren().clear();
            for(int i=0;i<9;i++){
                if(Role.abilities.get(i).equals("ban member")){
                    continue;
                }
                ssc.role_desc.addColumn(0,new Text(Role.abilities.get(i)));
                ssc.role_desc.addColumn(1, new Text(role.getValues().charAt(i)=='1' ? "yes" : "no"));
            }
        } );
    }
}
