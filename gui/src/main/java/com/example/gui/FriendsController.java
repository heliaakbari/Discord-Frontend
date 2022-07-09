package com.example.gui;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.example.mutual.*;

public class FriendsController {
    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    protected ObjectOutputStream fout;
    protected ObjectInputStream fin;
    protected String currentUser;
    protected ArrayList<String> servers = new ArrayList<>();
    protected ArrayList<UserShort> directChats = new ArrayList<>();

    @FXML
    protected GridPane directs_grid;
    @FXML
    protected GridPane servers_grid;

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
        super.succeeded();
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