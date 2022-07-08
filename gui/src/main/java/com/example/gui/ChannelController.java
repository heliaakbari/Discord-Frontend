package com.example.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;

public class ChannelController {

    ArrayList<String> servers;
    ArrayList<String> channels;
    @FXML
    private GridPane channels_grid;
    @FXML
    private TextFlow textflow;

    @FXML
    private GridPane servers_grid;

    public ChannelController(){
        servers = new ArrayList<>();
        servers.add("s1");
        servers.add("s2");
    }

    public void addTexts(){
        textflow.getChildren().add(new Text("i aedoija rhinklowe\n" +
                "lknel wekjliwk\n" +
                " wefjnwlefl eifjlweijf fijelwiej\n" +
                "i aedoija rhinklowe\n" +
                "lknel wekjliwk\n" +
                " wefjnwlefl eifjlweijf fijelwiej\n" +
                "Defines the decoration applied where path segments meet. The value must have one of the following values: StrokeLineJoin.MITER, StrokeLineJoin.BEVEL, and StrokeLineJoin.ROUND. The image shows a shape using the values in the mentioned order.\n"));
    }

    public void addServers(){
        servers_grid.setVgap(5);

        for(String s : servers){
            Button btn = new Button(s);
            btn.setPrefHeight(40);
            btn.setPrefWidth(servers_grid.getPrefWidth());
            servers_grid.addColumn(0,btn);
        }
    }

    public void addChannels(ArrayList<String> channels){
        channels_grid.setVgap(5);

        for(String s : channels){
            Button btn = new Button(s);
            btn.setPrefHeight(40);
            btn.setPrefWidth(channels_grid.getPrefWidth());
            channels_grid.addColumn(0,btn);
        }
    }

    public void addMessage(){
        ;
    }


}


