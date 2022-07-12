package com.example.gui;

import com.example.mutual.*;
import com.example.mutual.Data;
import com.example.mutual.Role;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ServerSettingController {

    protected Role role;
    protected String currentUser;
    protected String currentServer;

    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    protected ObjectOutputStream fout;
    protected ObjectInputStream fin;
    protected Stage myStage;

    protected ArrayList<String> channels;
    protected HashMap<String, Role> serverMembers;
    protected ArrayList<UserShort> channelMembers;
    protected ArrayList<UserShort> friends;

    @FXML
    protected GridPane members_grid;
    @FXML
    protected Tab addUserToChannel;
    @FXML
    protected ChoiceBox<String> channels_list_leave;
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
    protected Tab serverName;
    @FXML
    protected Tab deleteServer;
    @FXML
    protected Tab rename_delete_server;


    @FXML
    protected ChoiceBox channels1;
    @FXML
    protected ChoiceBox channels2;
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

    @FXML
    protected Text createChannelWarning;
    @FXML
    protected TextField newChannelName;
    @FXML
    protected TextField newServerName;
    @FXML
    protected Text serverNameWarning;
    @FXML
    protected Text changeServerNameTitle;
    @FXML
    protected Button changeServerNameButton;
    @FXML
    protected Button deleteServerButton;

    @FXML
    protected GridPane messages_grid;

    @FXML
    protected ChoiceBox<String> history_list;

    @FXML
    protected TextField history_num;

    public ServerSettingController(Role role, String currentUser, String currentServer, ObjectOutputStream out, ObjectInputStream in, ObjectOutputStream fout, ObjectInputStream fin, Stage myStage) {
        this.in = in;
        this.out = out;
        this.role = role;
        this.currentServer = currentServer;
        this.currentUser = currentUser;
        this.fin = fin;
        this.fout = fout;
        this.myStage = myStage;
    }

    @FXML
    public void initialize() {
        myStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.out.println("closed");
                FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("friends-view.fxml"));
                FriendsController friendsController = new FriendsController(in, out, fin, fout, currentUser);
                fxmlLoader.setController(friendsController);
                Scene scene= null;
                try {
                    scene = new Scene(fxmlLoader.load(), 1000, 600);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                myStage.setScene(scene);
                myStage.show();
            }
        });
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
        if(role.equals("creator")){
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
        if(choicebox_createrole.getValue().equals("creator")){
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
        if(role.getRoleName().equals("creator"))
            return;
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
        if(channel.equals("general")){
            return;
        }
        try {
            out.writeObject(Command.banFromChannel(person, currentServer, channel));
            Data data = (Data) in.readObject();

        } catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }

    }

    @FXML
    public void leaveFromChannelOnButton(Event e){
        if(channels_list_leave.getValue()==null){
            return;
        }
        if(channels_list_leave.getValue()=="general") {
            return;
        }
            try{
                System.out.println("ban");
                out.writeObject(Command.banFromChannel(currentUser, currentServer,channels_list_leave.getValue()));
                Data data = (Data) in.readObject();
            } catch (IOException | ClassNotFoundException ex){
                ex.printStackTrace();
            }

            try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        new AddChannels(this,channels_list_leave).restart();
    }
    public void showMessages(){
        new AddMessages(this).restart();
    }
    @FXML
    public void leaveFromServerOnButton(Event e){
        if(role.getRoleName().equals("creator")){
            return;
        }
        try{
            out.writeObject(Command.banFromServer(currentUser, currentServer));
            Data data = (Data) in.readObject();
        } catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
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
        String newName = newServerName.getText();;
        if (newName.equals("")){
            serverNameWarning.setText("type a name first!");
            serverNameWarning.setFill(Color.RED);
        }
        else{
            try {
                out.writeObject(Command.changeServerName(currentUser, currentServer, newName));
                Data data = (Data) in.readObject();
                if (!(boolean) data.getPrimary()){
                    serverNameWarning.setText("this name is taken, try another one!");
                    serverNameWarning.setFill(Color.RED);
                }
                else{
                    serverNameWarning.setText("server name changed successfully!");
                    serverNameWarning.setFill(Color.GREEN);
                }

            } catch (IOException | ClassNotFoundException ex){
                ex.printStackTrace();
            }

        }
    }


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
        new AddChannels(this, channels2).restart();
    }


    public void serverRolesTab(Event e){
        Tab tab = (Tab) e.getSource();
        if(!tab.isSelected())
            return;
        new OpenServerRoles(this).restart();
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

    public void OpenHistoryTab(Event e) {
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
        new AddChannels(this,history_list).restart();
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

    public void leave(Event e) {
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected())
            return;
        new AddChannels(this, channels_list_leave);
    }

    public void renameDelete(Event e){
        // ability to rename server
        if (role.getValues().charAt(5) == '1'){
            newServerName.setVisible(true);
            changeServerNameButton.setVisible(true);
            changeServerNameTitle.setVisible(true);
        }
        else{
            newServerName.setVisible(false);
            changeServerNameButton.setVisible(false);
            changeServerNameTitle.setVisible(false);
            serverNameWarning.setVisible(false);
        }

        // ability to delete server
        if (role.getValues().charAt(8) == '1')
            deleteServerButton.setVisible(true);
        else
            deleteServerButton.setVisible(false);
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
    String currentChannel;

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

                ssc.channels3.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number number1, Number number2) {
                        currentChannel = (String) ssc.channels3.getItems().get((Integer) number2);

                        try {
                            ssc.out.writeObject(Command.getChannelMembers(ssc.currentUser, ssc.currentServer, currentChannel));
                            Data data2 = (Data) ssc.in.readObject();
                            ssc.channelMembers = (ArrayList<UserShort>) data2.getPrimary();

                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        ssc.channelMembers1.getItems().clear();
                        for (UserShort user : ssc.channelMembers) {
                            ssc.channels3.getItems().add(user.getUsername());
                        }

                    }
                });

                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        ssc.channels3.getItems().clear();
        ssc.channels3.getItems().addAll(ssc.channels);

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
    HashMap<String , Role> membersAlreadyHere;

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

                ssc.out.writeObject(Command.getServerMembers(ssc.currentUser, ssc.currentServer));
                data = (Data) ssc.in.readObject();
                membersAlreadyHere = (HashMap<String, Role>) data.getPrimary();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        ssc.friends2.getItems().clear();
        for (UserShort user : ssc.friends) {
            if (!membersAlreadyHere.containsKey(user.getUsername()))
                ssc.friends2.getItems().add(user.getUsername());
        }
    }
}

// for delete channel and leave channel
class AddChannels extends Service<Void> {
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
                ssc.channels = (ArrayList<String>) data.getPrimary();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        channels.getItems().clear();
        channels.getItems().addAll(ssc.channels);
        ssc.channels2.getItems().clear();
        ssc.channels2.getItems().addAll(ssc.channels);
        ssc.channels_list_leave.getItems().clear();
        for(String channel : ssc.channels){
            if(!channel.equals("general")){
                ssc.channels_list_leave.getItems().add(channel);
            }
        }
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


class AddMessages extends Service<Void>{
    ServerSettingController cc;
    ArrayList<Message> msgs;
    ArrayList<UserShort> pics;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm:ss");
    public AddMessages(ServerSettingController cc){
        this.cc=cc;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                Command cmd = Command.getChannelMsgs(cc.currentUser,cc.currentServer,cc.history_list.getValue(),Integer.parseInt(cc.history_num.getText()));
                cc.out.writeObject(cmd);
                Data dt =(Data) cc.in.readObject();
                msgs = (ArrayList<Message>) dt.getPrimary();
                pics = (ArrayList<UserShort>) dt.getSecondary();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        cc.messages_grid.getChildren().clear();
        cc.messages_grid.setVgap(5);
        for (Message message : msgs){
            TextFlow textFlow = new TextFlow(new Text(message.getSourceInfo().get(0)+" : ("+message.getDateTime().format(dateTimeFormatter)+")\n"+message.getText()+"\n"));
            ToggleGroup group = new ToggleGroup();
            RadioButton like = new RadioButton("like: "+message.getLikes()+"      ");
            RadioButton dislike = new RadioButton("dislike: "+message.getDislikes()+"      ");
            RadioButton laugh = new RadioButton("laugh: "+message.getLaughs()+"       ");
            group.getToggles().addAll(like,dislike,laugh);
            textFlow.getChildren().addAll(like,dislike,laugh);
            textFlow.setStyle("-fx-background-color: rgb(176,223,255); -fx-border-radius: 5px;");
            textFlow.setPrefWidth(430);
            textFlow.setPadding(new Insets(5));
            textFlow.setBorder(Border.stroke(Color.BLACK));
            cc.messages_grid.addColumn(1,textFlow);
        }
    }

    @Override
    protected void failed() {
        System.out.println("failed");
    }
}
