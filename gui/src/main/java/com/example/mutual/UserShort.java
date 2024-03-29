/**
 * this is used to give a prepared node of users' picture from server to user
 */
package com.example.mutual;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.ByteArrayInputStream;
import java.io.Serializable;


public class UserShort implements Serializable {
    private static final long serialVersionUID = 838923986992328L;
    /**
     * username of user as string
     */
    private String username;
    /**
     * user's image in bytes
     */
    private byte[] bytes;
    /**
     * user's status
     */
    private Status status;

    public Status getStatus() {
        return status;
    }

    /**
     * the constructor
     * @param username
     * @param bytes
     * @param status
     */
    public UserShort(String username, byte[] bytes, Status status){
        this.bytes = bytes;
        this.username = username;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    /**
     * combines status and changes image from bytes to image and then
     * makes a pane from image and status which can be shown in javafx
     * alse the status is change from status to a circle
     * black:offline grey:invisible yellow:idle online:green do_not_disturb:red
     * @param radius the redius of profile picture
     * @return the pane that is showable in javafx
     */
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
        pane.getChildren().get(1).setLayoutX(2*radius - 10);
        pane.getChildren().get(1).setLayoutY(2*radius - 10);
        return pane;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
