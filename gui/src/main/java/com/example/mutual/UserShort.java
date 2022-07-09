package com.example.mutual;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

public class UserShort implements Serializable {
    private static final long serialVersionUID = 838923986992328L;
    private String username;
    private byte[] bytes;
    private Status status;

    public Status getStatus() {
        return status;
    }

    public UserShort(String username, byte[] bytes, Status status){
        this.bytes = bytes;
        this.username = username;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public Node profileStatus(Double radius){
        Image profilePhoto = new Image(new ByteArrayInputStream(bytes));
        ImageView imageView = new ImageView(profilePhoto);
        imageView.setFitHeight(radius*2-10);
        imageView.setFitWidth(radius*2-10);
        imageView.setX((-1)*radius+5);
        imageView.setY((-1)*radius+5);
        imageView.setPickOnBounds(true);
        imageView.setClip(new Circle(radius-5));
        Circle circle = new Circle(5);

        if(status==Status.online){
            circle.setFill(Color.GREEN);
            circle.setStroke(Color.GREEN);
        }
        else if(status==Status.idle){
            circle.setStroke(Color.YELLOW);
            circle.setFill(Color.YELLOW);
        }
        else if(status==Status.invisible){
            circle.setStroke(Color.LIGHTGRAY);
            circle.setFill(Color.LIGHTGRAY);
        }
        else if(status==Status.do_not_disturb){
            circle.setStroke(Color.RED);
            circle.setFill(Color.RED);
        }
        else if(status==Status.offline){
            circle.setFill(Color.BLACK);
            circle.setStroke(Color.BLACK);
        }
        Pane pane = new Pane();
        pane.setPrefSize(50,50);
        pane.setMinSize(50,50);
        pane.setMaxSize(50,50);
        imageView.setPreserveRatio(false);
        pane.getChildren().add(imageView);
        pane.getChildren().add(circle);
        pane.getChildren().get(0).setLayoutX(radius);
        pane.getChildren().get(0).setLayoutY(radius);
        pane.getChildren().get(1).setLayoutX(39);
        pane.getChildren().get(1).setLayoutY(39);
        return pane;
    }

}
