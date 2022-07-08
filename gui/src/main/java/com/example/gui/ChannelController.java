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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        new GoToChannel(this,currentChannel);
    }

    public void changeChannelFromGUI(Event event){
        String channelName = ((Button)event.getSource()).getText();
        new GoToChannel(this,channelName).restart();
    }


    public void changeServerFromGUI(Event event){
        currentServer = ((Button)event.getSource()).getText();
        new GoToServer(this,currentServer).restart();
        new GoToChannel(this,"general");
    }

    public void addnewServer(){
        new GoToServer(this,currentServer).restart();
        new GoToChannel(this,"general");
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
                    new GoToServer(cc,((Button)event.getSource()).getText()).restart();
                    new GoToChannel(cc,"general");
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
                    new GoToChannel(cc,((Button)event.getSource()).getText()).restart();
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

class TellChannel extends Service<Void>{
    private ChannelController cc;
    private String newChannel;
    private String newServer;

    public TellChannel(ChannelController cc, String newServer,String newChannel){
        this.cc =cc;
        this.newChannel = newChannel;
        this.newServer = newServer;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                cc.out.writeObject(Command.tellChannel(cc.currentUser,newServer,newChannel));
                cc.in.readObject();
                return null;
            }
        };
    }
}


class LastseenChannel extends Service<Void>{
    private ChannelController cc;
    private String oldChannel;
    private String oldServer;

    public LastseenChannel(ChannelController cc, String oldServer,String oldChannel){
        this.cc =cc;
        this.oldChannel = oldChannel;
        this.oldServer = oldServer;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                cc.out.writeObject(Command.lastseenChannel(cc.currentUser,oldServer,oldChannel));
                cc.in.readObject();
                return null;
            }
        };
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
                cc.server_name.setText(servername);
                cc.out.writeObject(Command.userChannels(cc.currentUser,servername));
                Data dt =(Data) cc.in.readObject();
                cc.channels = (ArrayList<String>) dt.getPrimary();
                cc.addChannels();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        new TellChannel(cc,cc.currentServer,cc.currentChannel).restart();
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
        Message message = null;
        Data data ;

        while (true) {
            System.out.print(" ");
            try {
                data = (Data) cc.in.readObject();
                System.out.println( data.getKeyword());
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
            if (data.getKeyword().equals("exitChat")) {
                return;
            }
            message = (Message) data.getPrimary();
            cc.messages.add(message);
            final Message msg = message;
            Platform.runLater(()->{
                cc.addNewMessage(msg);
            });


        }
    }
}

