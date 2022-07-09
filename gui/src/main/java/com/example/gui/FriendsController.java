package com.example.gui;

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
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.example.mutual.*;

public class FriendsController {
    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    protected ObjectOutputStream fout;
    protected ObjectInputStream fin;
    protected String currentUser;
    protected ArrayList<String> servers = new ArrayList<>();
    protected ArrayList<UserShort> directChats = new ArrayList<>();
    protected ArrayList<UserShort> allFriends = new ArrayList<>();
    protected ArrayList<UserShort> allblocks = new ArrayList<>();
    protected HashMap<UserShort,Boolean> allrequests = new HashMap<>();
    @FXML
    protected GridPane directs_grid;
    @FXML
    protected GridPane servers_grid;

    @FXML
    protected GridPane online_grid;

    @FXML
    protected GridPane block_grid;

    @FXML
    protected GridPane all_grid;

    public FriendsController(ObjectInputStream in, ObjectOutputStream out,ObjectInputStream fin,ObjectOutputStream fout,String username){
        this.out = out;
        this.in = in;
        this.fin= fin;
        this.fout = fout;
        currentUser = username;
    }

    @FXML
    public void initialize(){
        new GetServers(this).restart();
    }

     void addDirects(){
      directs_grid.getChildren().clear();
        directs_grid.setAlignment(Pos.CENTER);
        for(UserShort user : directChats) {
            Node pic = user.profileStatus(25.0);
            directs_grid.addColumn(0, pic);
            Text name = new Text(user.getUsername());
            name.setTextAlignment(TextAlignment.LEFT);
            directs_grid.addColumn(1, name);
        }
    }

    public void addServers(){
        servers_grid.getChildren().clear();
        servers_grid.setVgap(5);
        Button btn = new Button("Discord");
        FriendsController fc = this;
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               new  GetServers(fc).restart();
            }
        });
                //add eventhandler
        btn.setPrefHeight(40);
        btn.setPrefWidth(servers_grid.getPrefWidth());
        servers_grid.addColumn(0,btn);
        for(String s : servers){
            btn = new Button(s);
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("channel-view.fxml"));
                    ChannelController channelController = new ChannelController(in,out,fin,fout,currentUser,((Button)event.getSource()).getText(),"general");
                    fxmlLoader.setController(channelController);
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

    public void showalllist(Event e){
        Tab tab = (Tab) e.getSource();
        if(!tab.isSelected()){
            return;
        }
        new AddAllFriends(this).restart();
    }

    public void showblocklist(Event e){
        Tab tab = (Tab) e.getSource();
        if(!tab.isSelected()){
            return;
        }
        new AddBlockeds(this).restart();
    }

    public void showonlinelist(Event e){
        Tab tab = (Tab) e.getSource();
        if(!tab.isSelected()){
            return;
        }
        new AddOnlineFriends(this).restart();
    }
}

class AddAllFriends extends Service<Void>{
    FriendsController fc;

    public AddAllFriends(FriendsController fc){
        this.fc=fc;
    }
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Command cmd = Command.getFriends(fc.currentUser);
                fc.out.writeObject(cmd);
                Data dt =(Data) fc.in.readObject();
                System.out.println(dt.getKeyword());
                fc.allFriends=(ArrayList<UserShort>) dt.getPrimary();
                return null;
            }
        };

    }

    @Override
    protected void succeeded() {
        fc.all_grid.getChildren().clear();
        fc.all_grid.setVgap(5);
        for(UserShort s : fc.allFriends){
            fc.all_grid.addColumn(1,s.profileStatus(25.0));
            fc.all_grid.addColumn(2,new Text(s.getUsername()));
        }
    }
}

class AddOnlineFriends extends Service<Void>{
    FriendsController fc;

    public AddOnlineFriends(FriendsController fc){
        this.fc=fc;
    }
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Command cmd = Command.getFriends(fc.currentUser);
                fc.out.writeObject(cmd);
                Data dt =(Data) fc.in.readObject();
                System.out.println(dt.getKeyword());
                fc.allFriends=(ArrayList<UserShort>) dt.getPrimary();
                return null;
            }
        };

    }

    @Override
    protected void succeeded() {
        fc.online_grid.getChildren().clear();
        fc.online_grid.setVgap(5);
        for(UserShort s : fc.allFriends){
            if(s.getStatus()==Status.online || s.getStatus()==Status.idle) {
                fc.online_grid.addColumn(1, s.profileStatus(25.0));
                fc.online_grid.addColumn(2, new Text(s.getUsername()));
            }
        }
    }
}

class AddBlockeds extends Service<Void>{
    FriendsController fc;

    public AddBlockeds(FriendsController fc){
        this.fc=fc;
    }
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Command cmd = Command.getBlockList(fc.currentUser);
                fc.out.writeObject(cmd);
                Data dt =(Data) fc.in.readObject();
                System.out.println(dt.getKeyword());
                fc.allblocks=(ArrayList<UserShort>) dt.getPrimary();
                return null;
            }
        };

    }

    @Override
    protected void succeeded() {
        fc.block_grid.getChildren().clear();
        fc.block_grid.setVgap(5);
        for(UserShort s : fc.allFriends){
                fc.block_grid.addColumn(1, s.profileStatus(25.0));
                fc.block_grid.addColumn(2, new Text(s.getUsername()));

        }
    }
}


class GetDirectList extends Service<Void>{
    FriendsController fc;

    public GetDirectList(FriendsController fc){

        this.fc=fc;
    }
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Command cmd = Command.getDirectChats(fc.currentUser);
                fc.out.writeObject(cmd);
                Data dt =(Data) fc.in.readObject();
                System.out.println(dt.getKeyword());
                fc.directChats=(ArrayList<UserShort>) dt.getPrimary();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        fc.addDirects();
        new AddAllFriends(fc).restart();

    }
}


class GetServers extends Service<Void>{
    FriendsController fc;

    public GetServers(FriendsController fc){
        this.fc=fc;
    }
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Command cmd = Command.userServers(fc.currentUser);
                fc.out.writeObject(cmd);
                Data dt =(Data) fc.in.readObject();
                System.out.println(dt.getKeyword());
                fc.servers=(ArrayList<String>) dt.getPrimary();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        fc.addServers();
        new GetDirectList(fc).restart();
        super.succeeded();
    }
}