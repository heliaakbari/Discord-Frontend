package com.example.gui;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.css.Style;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.mutual.*;
import javafx.stage.WindowEvent;

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
    protected HashMap<UserShort, Boolean> allrequests = new HashMap<>();

    protected HashMap<String, Button> acceptButtons = new HashMap<>();
    protected HashMap<String, Button> rejectButtons = new HashMap<>();
    protected HashMap<String, Button> cancelButtons = new HashMap<>();
    protected HashMap<String, Button> unblockButtons = new HashMap<>();
    protected HashMap<String, Button> blockButtons = new HashMap<>();

    @FXML
    protected GridPane directs_grid;
    
    @FXML
    protected GridPane servers_grid;

    @FXML
    protected GridPane online_grid;

    @FXML
    protected TextField search_text;

    @FXML
    protected Button search_button;

    @FXML
    protected GridPane pending_grid;

    @FXML
    protected GridPane block_grid;

    @FXML
    protected GridPane all_grid;

    @FXML
    protected TextField sendrequest_textfield;

    @FXML
    protected Text warning;

    public FriendsController(ObjectInputStream in, ObjectOutputStream out, ObjectInputStream fin, ObjectOutputStream fout, String username) {
        this.out = out;
        this.in = in;
        this.fin = fin;
        this.fout = fout;
        currentUser = username;
    }

    @FXML
    public void initialize() {
        new GetServers(this).restart();
    }

    public void newDirectFromSearch(Event event){
        String text = search_text.getText();
        search_text.clear();

        for(UserShort friend : allFriends){
            if(friend.getUsername().equals(text)){
                FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("pv-view.fxml"));
                PvController pvController = new PvController(in,out,fin,fout,currentUser,text);
                fxmlLoader.setController(pvController);
                Stage stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load(), 1000, 600);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setScene(scene);
                stage.show();
                break;
            }
        }
    }

    void addDirects() {
        directs_grid.getChildren().clear();
        directs_grid.setVgap(3);
        directs_grid.setAlignment(Pos.CENTER);
        for (UserShort user : directChats) {
            Node pic = user.profileStatus(25.0);
            directs_grid.addColumn(0, pic);
            Button name = new Button(user.getUsername());
            name.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("pv-view.fxml"));
                    PvController pvController = new PvController(in,out,fin,fout,currentUser,((Button)event.getSource()).getText());
                    fxmlLoader.setController(pvController);
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
            name.setPrefSize(150,40);
            directs_grid.addColumn(1, name);
        }
    }

    public void addServers() {
        servers_grid.getChildren().clear();
        servers_grid.setVgap(5);
        Button btn = new Button("Discord");
        FriendsController fc = this;
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                new GetServers(fc).restart();
            }
        });
        //add eventhandler
        btn.setPrefHeight(40);
        btn.setPrefWidth(servers_grid.getPrefWidth());
        servers_grid.addColumn(0, btn);
        for (String s : servers) {
            btn = new Button(s);
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("channel-view.fxml"));
                    ChannelController channelController = new ChannelController(in, out, fin, fout, currentUser, ((Button) event.getSource()).getText(), "general");
                    fxmlLoader.setController(channelController);
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
            });

            btn.setPrefHeight(40);
            btn.setPrefWidth(servers_grid.getPrefWidth());
            servers_grid.addColumn(0, btn);
        }

        btn = new Button("add server");
        //add eventhandler
        btn.setPrefHeight(40);
        btn.setPrefWidth(servers_grid.getPrefWidth());
        servers_grid.addColumn(0, btn);

    }

    protected Boolean firsttime = true;
    public void showalllist(Event e) {
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected()) {
            return;
        }
        if (!firsttime){
            new AddAllFriends(this).start();
        }
        firsttime= false;
    }

    public void showblocklist(Event e) {
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected()) {
            return;
        }
        new AddBlockeds(this).restart();
    }

    public void showonlinelist(Event e) {
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected()) {
            return;
        }
        new AddOnlineFriends(this).restart();
    }

    public void showpendinglist(Event e) {
        Tab tab = (Tab) e.getSource();
        if (!tab.isSelected()) {
            return;
        }
        new AddPending(this).restart();
    }

    @FXML
    public void sendRequestOnButton(Event e) {
        try {
            out.writeObject(Command.newRelation(Relationship.Friend_pending, currentUser, sendrequest_textfield.getText()));
            Data data = (Data) in.readObject();
            if (!(boolean)data.getPrimary()) {
                warning.setText("there is no user with this username");
                warning.setFill(Color.RED);

            }
            else {
                warning.setText("request sent to " + sendrequest_textfield.getText() + "!");
                warning.setFill(Color.GREEN);
                sendrequest_textfield.clear();
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

    }

    public void settingOnButton(Event e){
        Data data = null;
        try {
            out.writeObject(Command.getUser(currentUser));
            data = (Data) in.readObject();
        } catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
        }
        Stage stage = (Stage)(((Node) e.getSource()).getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("setting-view.fxml"));
        fxmlLoader.setController(new SettingController(out, in, fout,fin,(User) data.getPrimary(), (UserShort) data.getSecondary(),stage));
        stage.setHeight(450);
        stage.setWidth(600);
        stage.centerOnScreen();
        stage.setResizable(false);
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 600, 400);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        stage.setScene(scene);
        stage.show();
    }
}

class AddAllFriends extends Thread {
    FriendsController fc;

    public AddAllFriends(FriendsController fc) {
        this.fc = fc;
    }

    @Override
    public void run() {
        Command cmd = Command.getFriends(fc.currentUser);
        try {
            fc.out.writeObject(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Data dt = null;
        try {
            dt = (Data) fc.in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(dt.getKeyword());
        fc.allFriends = (ArrayList<UserShort>) dt.getPrimary();

        Platform.runLater(()->{
            int rowNum = 1;
            fc.all_grid.getChildren().clear();
            fc.all_grid.setVgap(5);
            for (UserShort s : fc.allFriends) {
                fc.all_grid.addColumn(1, s.profileStatus(25.0));
                fc.all_grid.addColumn(2, new Text(s.getUsername()));
                Button block = new Button("block");
                fc.blockButtons.put(rowNum + s.getUsername(), block);
                fc.all_grid.addColumn(3, block);
                rowNum++;
            }

            for (Map.Entry<String, Button> entry: fc.blockButtons.entrySet()) {
                int row = entry.getKey().charAt(0) - 48;
                String otherUser = entry.getKey().substring(1);
                entry.getValue().setOnAction((ActionEvent e) -> {
                    try {
                        fc.out.writeObject(Command.newRelation(Relationship.Block, fc.currentUser, otherUser));
                        fc.in.readObject();
                        new AddAllFriends(fc).start();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                });
            }
        });
    }

}

class AddPending extends Service<Void> {
    FriendsController fc;

    public AddPending(FriendsController fc) {
        this.fc = fc;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Command cmd = Command.getRequests(fc.currentUser);
                fc.out.writeObject(cmd);
                Data dt = (Data) fc.in.readObject();
                System.out.println(dt.getKeyword());
                fc.allrequests = (HashMap<UserShort, Boolean>) dt.getPrimary();
                return null;
            }
        };

    }

    @Override
    protected void succeeded() {
        fc.pending_grid.getChildren().clear();
        fc.pending_grid.setVgap(5);

        int rowNum = 1;
        for (HashMap.Entry<UserShort, Boolean> entry : fc.allrequests.entrySet()) {
            Boolean value = entry.getValue();
            if (value) {
                fc.pending_grid.addColumn(1, entry.getKey().profileStatus(25.0));
                fc.pending_grid.addColumn(2, new Text(entry.getKey().getUsername()));
                fc.pending_grid.addColumn(3, new Text("incoming"));

                Button accept = new Button("accept");
                fc.acceptButtons.put(rowNum + entry.getKey().getUsername(), accept);
                fc.pending_grid.addColumn(4, accept);

                Button reject = new Button("reject");
                fc.rejectButtons.put(rowNum + entry.getKey().getUsername(), reject);
                fc.pending_grid.addColumn(5, reject);

                rowNum++;
            } else {
                fc.pending_grid.addColumn(1, entry.getKey().profileStatus(25.0));
                fc.pending_grid.addColumn(2, new Text(entry.getKey().getUsername()));
                fc.pending_grid.addColumn(3, new Text("outgoing"));
                fc.pending_grid.addColumn(4, new Text(""));

                Button cancel = new Button("cancel");
                fc.cancelButtons.put(rowNum + entry.getKey().getUsername(), cancel);
                fc.pending_grid.addColumn(5, cancel);

                rowNum++;
            }
        }

        for (Map.Entry<String, Button> entry : fc.acceptButtons.entrySet()) {
            int row = entry.getKey().charAt(0) - 48;
            String otherUser = entry.getKey().substring(1);
            entry.getValue().setOnAction((ActionEvent e) -> {
                try {
                    fc.out.writeObject(Command.newRelation(Relationship.Friend, fc.currentUser, otherUser));
                    try {
                        fc.in.readObject();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    new AddPending(fc).restart();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            // remove the row
        }

        for (Map.Entry<String, Button> entry : fc.rejectButtons.entrySet()) {
            int row = entry.getKey().charAt(0) - 48;
            String otherUser = entry.getKey().substring(1);
            entry.getValue().setOnAction((ActionEvent e) -> {
                try {
                    fc.out.writeObject(Command.newRelation(Relationship.Rejected, fc.currentUser, otherUser));
                    try {
                        fc.in.readObject();
                        new AddPending(fc);
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }

        for (Map.Entry<String, Button> entry : fc.cancelButtons.entrySet()) {
            int row = entry.getKey().charAt(0) - 48;
            String otherUser = entry.getKey().substring(1);
            entry.getValue().setOnAction((ActionEvent e) -> {
                try {
                    fc.out.writeObject(Command.newRelation(Relationship.Rejected, fc.currentUser, otherUser));
                    try {
                        fc.in.readObject();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    new AddPending(fc).restart();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }


    }
}

class AddOnlineFriends extends Service<Void> {
    FriendsController fc;

    public AddOnlineFriends(FriendsController fc) {
        this.fc = fc;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Command cmd = Command.getFriends(fc.currentUser);
                fc.out.writeObject(cmd);
                Data dt = (Data) fc.in.readObject();
                System.out.println(dt.getKeyword());
                fc.allFriends = (ArrayList<UserShort>) dt.getPrimary();
                return null;
            }
        };

    }

    @Override
    protected void succeeded() {
        fc.online_grid.getChildren().clear();
        fc.online_grid.setVgap(5);
        for (UserShort s : fc.allFriends) {
            if (s.getStatus() == Status.online || s.getStatus() == Status.idle) {
                fc.online_grid.addColumn(1, s.profileStatus(25.0));
                fc.online_grid.addColumn(2, new Text(s.getUsername()));
            }
        }
    }
 }

class AddBlockeds extends Service<Void> {
    FriendsController fc;

    public AddBlockeds(FriendsController fc) {
        this.fc = fc;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Command cmd = Command.getBlockList(fc.currentUser);
                fc.out.writeObject(cmd);
                Data dt = (Data) fc.in.readObject();
                System.out.println(dt.getKeyword());
                fc.allblocks = (ArrayList<UserShort>) dt.getPrimary();
                return null;
            }
        };

    }

    @Override
    protected void succeeded() {
        int rowNum = 1;
        fc.block_grid.getChildren().clear();
        fc.block_grid.setVgap(5);
        for (UserShort s : fc.allblocks) {
            fc.block_grid.addColumn(1, s.profileStatus(25.0));
            fc.block_grid.addColumn(2, new Text(s.getUsername()));
            Button unblock = new Button("unblock");
            fc.unblockButtons.put(rowNum + s.getUsername(), unblock);
            fc.block_grid.addColumn(5,unblock);
            rowNum++;
        }

        for (Map.Entry<String, Button> entry: fc.unblockButtons.entrySet()) {
            int row = entry.getKey().charAt(0) - 48;
            String otherUser = entry.getKey().substring(1);
            entry.getValue().setOnAction((ActionEvent e) -> {
                try {
                    fc.out.writeObject(Command.newRelation(Relationship.Rejected, fc.currentUser, otherUser));
                    fc.in.readObject();
                    new AddBlockeds(fc).restart();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            });
            // remove row
        }
    }
}

class GetDirectList extends Service<Void> {
    FriendsController fc;

    public GetDirectList(FriendsController fc) {

        this.fc = fc;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Command cmd = Command.getDirectChats(fc.currentUser);
                fc.out.writeObject(cmd);
                Data dt = (Data) fc.in.readObject();
                System.out.println(dt.getKeyword());
                fc.directChats = (ArrayList<UserShort>) dt.getPrimary();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        fc.addDirects();

    }
}

class GetServers extends Service<Void> {
    FriendsController fc;

    public GetServers(FriendsController fc) {
        this.fc = fc;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Command cmd = Command.userServers(fc.currentUser);
                fc.out.writeObject(cmd);
                Data dt = (Data) fc.in.readObject();
                System.out.println(dt.getKeyword());
                fc.servers = (ArrayList<String>) dt.getPrimary();
                cmd = Command.getDirectChats(fc.currentUser);
                fc.out.writeObject(cmd);
                dt = (Data) fc.in.readObject();
                System.out.println(dt.getKeyword());
                fc.directChats = (ArrayList<UserShort>) dt.getPrimary();
                cmd = Command.getFriends(fc.currentUser);
                fc.out.writeObject(cmd);
                dt = (Data) fc.in.readObject();
                System.out.println(dt.getKeyword());
                fc.allFriends = (ArrayList<UserShort>) dt.getPrimary();
                return null;
            }
        };
    }

    @Override
    protected void succeeded() {
        fc.addServers();
        fc.addDirects();
        fc.all_grid.getChildren().clear();
        fc.all_grid.setVgap(5);
        for (UserShort s : fc.allFriends) {
            fc.all_grid.addColumn(1, s.profileStatus(25.0));
            fc.all_grid.addColumn(2, new Text(s.getUsername()));
        }
        super.succeeded();
    }

}
