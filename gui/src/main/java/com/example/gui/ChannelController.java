package com.example.gui;

import com.example.mutual.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ChannelController {

    protected String currentChannel;
    protected String currentServer;
    protected String currentUser;
    protected ArrayList<String> servers= new ArrayList<>();
    protected ArrayList<String> channels = new ArrayList<>();
    protected ArrayList<Message> messages= new ArrayList<>();
    protected ArrayList<UserShort> members= new ArrayList<>();
    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    protected ObjectOutputStream fout;
    protected ObjectInputStream fin;
    protected Boolean isMessageReader=false;
    @FXML
    protected Label channel_name;

    @FXML
    protected GridPane messages_grid;

    @FXML
    protected ScrollPane messages_scroll;

    @FXML
    protected TextArea message_textField;

    @FXML
    protected Button send_button;

    @FXML
    protected Button file_button;


    @FXML
    protected GridPane channels_grid;

    @FXML
    protected TextFlow textflow;

    @FXML
    protected GridPane servers_grid;

    @FXML
    protected GridPane members_grid;

    @FXML
    protected Label server_name;

    public ChannelController(ObjectInputStream in,ObjectOutputStream out,ObjectInputStream fin,ObjectOutputStream fout,String currentUser,String currentServer,String currentChannel){
        this.out = out;
        this.in = in;
        this.fin = fin;
        this.fout = fout;
        this.currentUser = currentUser;
        this.currentServer = currentServer;
        this.currentChannel= currentChannel;
        this.currentServer = currentServer;
    }

    @FXML
    public void initialize() {
        new GoToServer(this,currentServer).restart();
    }

    public void addnewServer(){
        new GoToServer(this,currentServer).restart();
    }

    public void addMembers(){
        members_grid.getChildren().clear();
        members_grid.setAlignment(Pos.CENTER);
        for(UserShort user : members){
            Node pic = user.profileStatus(25.0);
            members_grid.addColumn(0,pic);
            Text name = new Text(user.getUsername());
            name.setTextAlignment(TextAlignment.LEFT);
            members_grid.addColumn(1,name);
        }
    }

    public void addServers(){
        servers_grid.getChildren().clear();
        servers_grid.setVgap(5);
        Button btn = new Button("Discord");
        ChannelController ccOld = this;
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(isMessageReader) {
                    try {
                        out.writeObject(Command.lastseenChannel(currentUser,currentServer,currentChannel));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("friends-view.fxml"));
                FriendsController friendsController = new FriendsController(in,out,fin,fout,currentUser);
                fxmlLoader.setController(friendsController);
                Stage stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load(), 1000, 600);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setScene(scene);
                stage.show();
            }
        });

        //add eventhandler
        btn.setPrefHeight(40);
        btn.setPrefWidth(servers_grid.getPrefWidth());
        servers_grid.addColumn(0,btn);
        ChannelController cc = this;
        for(String s : servers){
            btn = new Button(s);
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(isMessageReader) {
                        try {
                            out.writeObject(Command.lastseenChannel(currentUser,currentServer,currentChannel));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    new GoToServer(cc, ((Button) event.getSource()).getText()).restart();

                }
            });

            btn.setPrefHeight(40);
            btn.setPrefWidth(servers_grid.getPrefWidth());
            servers_grid.addColumn(0,btn);
        }

        btn = new Button("add server");
        //add eventhandler
        btn.setPrefHeight(40);
        btn.setPrefWidth(servers_grid.getPrefWidth());
        servers_grid.addColumn(0,btn);

    }

    public void addChannels(){
        channels_grid.getChildren().clear();
        channels_grid.setVgap(5);
        ChannelController cc = this;
        for(String s : channels){
            Button btn = new Button(s);
            btn.setPrefHeight(40);
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(isMessageReader) {
                        try {
                            out.writeObject(Command.lastseenChannel(currentUser,currentServer,currentChannel));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    new GoToChannel(cc, ((Button) event.getSource()).getText()).restart();

                }
            });
            btn.setPrefWidth(channels_grid.getPrefWidth());
            channels_grid.addColumn(0,btn);
        }
    }

    public void addMessages(){
        messages_grid.getChildren().clear();
        messages_grid.setVgap(5);


        for (Message message : messages){
            for(UserShort userShort : members){
                if(message.getSourceInfo().get(0).equals(userShort.getUsername())){
                    if(message.getSourceInfo().get(0).equals(currentUser)){
                        messages_grid.addColumn(0,new Text(""));
                        messages_grid.addColumn(2, userShort.profileStatus(25.0));
                    }
                    else {
                        messages_grid.addColumn(0, userShort.profileStatus(25.0));
                        messages_grid.addColumn(2,new Text(""));
                    }
                    break;
                }
            }
            TextFlow textFlow = new TextFlow(new Text(message.toString()));

            if(message.getSourceInfo().get(0).equals(currentUser)){
                textFlow.setStyle("-fx-background-color: #665555");
            }
            else{
                textFlow.setStyle("-fx-background-color: #555566");
            }
            textFlow.setPrefWidth(430);
            messages_grid.addColumn(1,textFlow);
        }
        messages_scroll.setVvalue(1.0);
    }

    public void addNewMessage(Message message){
        for(UserShort userShort : members){
            if(message.getSourceInfo().get(0).equals(userShort.getUsername())){
                if(message.getSourceInfo().get(0).equals(currentUser)){
                    messages_grid.addColumn(0,new Text(""));
                    messages_grid.addColumn(2, userShort.profileStatus(25.0));
                }
                else {
                    messages_grid.addColumn(0, userShort.profileStatus(25.0));
                    messages_grid.addColumn(2,new Text(""));
                }
                break;
            }
        }
        TextFlow textFlow = new TextFlow(new Text(message.toString()));

        if(message.getSourceInfo().get(0).equals(currentUser)){
            textFlow.setStyle("-fx-background-color: #665555");
        }
        else{
            textFlow.setStyle("-fx-background-color: #555566");
        }
        textFlow.setPrefWidth(430);
        messages_grid.addColumn(1,textFlow);
        messages_scroll.setVvalue(1.0);
    }

    public void sendTextMessage(Event event){
        new SendText(this,message_textField.getText()).restart();
        message_textField.clear();
    }

    public void sendFileMessage(){
        ;
    }
}


class SendText extends Service<Void>{
    private ChannelController cc;
    private String body;

    public SendText(ChannelController cc,String body){
        this.cc= cc;
        this.body = body;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                TextMessage msg = new TextMessage(cc.currentUser,cc.currentServer,cc.currentChannel,body,LocalDateTime.now());
                Command cmd = Command.newChannelMsg(cc.currentUser,cc.currentServer,cc.currentChannel,msg);
                cc.out.writeObject(cmd);
                return null;
            }
        };
    }
}

class GoToServer extends Service<Void>{

    private ChannelController cc;
    private String servername;
    public GoToServer(ChannelController cc,String servername){
        this.cc = cc;
        this.servername = servername;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                cc.currentServer=servername;
                cc.out.writeObject(Command.userServers(cc.currentUser));
                System.out.println("got the servers");
                Data dt = (Data) cc.in.readObject();
                cc.servers =(ArrayList<String>) dt.getPrimary();
                cc.out.writeObject(Command.userChannels(cc.currentUser,servername));
                dt =(Data) cc.in.readObject();
                System.out.println(dt.getKeyword() +" instead of user channels");
                cc.channels = (ArrayList<String>) dt.getPrimary();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        cc.server_name.setText(servername);
        cc.addServers();
        cc.addChannels();
        new GoToChannel(cc,"general").restart();
    }
}


class GoToChannel extends Service<Void>{
    private ChannelController cc;
    private String channelName;

    public GoToChannel(ChannelController cc,String newChannel){
        this.cc = cc;
        this.channelName = newChannel;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                cc.currentChannel = channelName;
                cc.out.writeObject(Command.getChannelMsgs(cc.currentUser,cc.currentServer,cc.currentChannel,5));
                Data dt =(Data) cc.in.readObject();
                System.out.println(dt.getKeyword());
                cc.messages = (ArrayList<Message>) dt.getPrimary();
                cc.out.writeObject(Command.getChannelMembers(cc.currentUser,cc.currentServer,cc.currentChannel));
                dt =(Data) cc.in.readObject();
                System.out.println(dt.getKeyword());
                cc.members = (ArrayList<UserShort>) dt.getPrimary();
                cc.out.writeObject(Command.tellChannel(cc.currentUser,cc.currentServer,channelName));
                cc.in.readObject();
                return null;
            }

            @Override
            protected void succeeded() {
                cc.channel_name.setText(channelName);
                cc.addMembers();
                cc.addMessages();
                new MessageReader(cc).start();
            }
        };


    }
}

class MessageReader extends Thread {

    private ChannelController cc;

    public MessageReader(ChannelController cc) {
        this.cc = cc;
    }

    @Override
    public void run() {
        cc.isMessageReader=true;
        Message message = null;
        Data data= null ;

        while (true) {
            System.out.print(" ");
            try {
                Object obj = cc.in.readObject();
                if(  obj instanceof String){
                    System.out.println("data is string "+obj);
                }
                else {
                    data = (Data) obj;
                    System.out.println(data.getKeyword() + " message reader");
                }
                if(data.getKeyword().equals("fake")){
                    continue;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                cc.isMessageReader=false;
                break;
            }
            if (data.getKeyword().equals("exitChat")) {
                System.out.println("got the exit chat");
                cc.isMessageReader=false;
                return;
            }
            if(data.getKeyword().equals("newChannelMsg")){
                message = (Message) data.getPrimary();
                cc.messages.add(message);
                final Message msg = message;
                Platform.runLater(()->{
                    cc.addNewMessage(msg);
                });
            }

        }

    }

}

