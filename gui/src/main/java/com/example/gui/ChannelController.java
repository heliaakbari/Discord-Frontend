package com.example.gui;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ChannelController {

    @FXML
    private TextFlow textflow;

    public void addTexts(){
        textflow.getChildren().add(new Text("i aedoija rhinklowe\n" +
                "lknel wekjliwk\n" +
                " wefjnwlefl eifjlweijf fijelwiej\n" +
                "i aedoija rhinklowe\n" +
                "lknel wekjliwk\n" +
                " wefjnwlefl eifjlweijf fijelwiej\n" +
                "Defines the decoration applied where path segments meet. The value must have one of the following values: StrokeLineJoin.MITER, StrokeLineJoin.BEVEL, and StrokeLineJoin.ROUND. The image shows a shape using the values in the mentioned order.\n"));
    }
}
