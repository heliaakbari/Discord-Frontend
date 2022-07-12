/**
 * this controller is linked to channel-view.fxml
 * it is for when user enters a channel chat
 */
package com.example.gui;

import com.example.mutual.*;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;


public class ChannelController {

    protected String currentChannel;
    protected String currentServer;
    protected String currentUser;
    /**
     * arraylist of servers that the person is a member of
     */
    protected ArrayList<String> servers= new ArrayList<>();
    /**
     * array list of channels from the current server
     * that user is a part of
     */
    protected ArrayList<String> channels = new ArrayList<>();
    /**
     * array of messages in the channel
     */
    protected ArrayList<Message> messages= new ArrayList<>();
    /**
     * arrays of Usershorts corresponding to messages list
     */
    protected ArrayList<UserShort> messagesPic= new ArrayList<>();
    /**
     * array of members of the current channel
     */
    protected ArrayList<UserShort> members= new ArrayList<>();

    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    protected ObjectOutputStream fout;
    protected ObjectInputStream fin;
    /**
     * the role of person in this channel and their capabilities
     */
    protected Role currentRole = null;
    /**
     * says if the message reader thread is running
     */
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

    /**
     * when this controller is instantiated, this method will be called
     */
    @FXML
    public void initialize() {
        new GoToServer(this,currentServer).restart();
    }

    /**
     * adds members from members field to the GUI
     */
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

    /**
     * when the pinned messages button is pressed this method will be called
     * it will disable message reader
     * and call the thread that gets pinned messages from server
     * and shows them
     */
    public void openPinnedmsgs(){
        message_textField.setVisible(false);
        send_button.setVisible(false);
        file_button.setVisible(false);
        if(isMessageReader) {
            try {
                out.writeObject(Command.lastseenChannel(currentUser,currentServer,currentChannel));
                Thread.sleep(100);
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        new AddPinnedMessages(this).restart();
    }

    /**
     * when the setting is pressed this method will be called
     *  and opens the server setting scene
     * @param event
     */
    public void goToserverSetting(Event event){
        if(isMessageReader) {
            try {
                out.writeObject(Command.lastseenChannel(currentUser,currentServer,currentChannel));
                Thread.sleep(100);
            } catch (IOException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("server-setting-view.fxml"));
        Stage stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
        ServerSettingController serverSettingController = new ServerSettingController(currentRole,currentUser,currentServer,out,in,fout,fin, stage);
        fxmlLoader.setController(serverSettingController);
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 1000, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(scene);
        stage.show();
    }

    /**
     * adds servers from field to GUI
     */
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
                        Thread.sleep(100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    catch (InterruptedException e){
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
                            Thread.sleep(100);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }catch (InterruptedException e){
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

        btn.setOnAction((ActionEvent event) -> {
            if(isMessageReader) {
                try {
                    out.writeObject(Command.lastseenChannel(currentUser,currentServer,currentChannel));
                    Thread.sleep(100);
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            System.out.println("clicked");

            Dialog<Boolean> dialog = new Dialog<Boolean>();
            dialog.setTitle("CREATING SERVER");
            dialog.setHeight(240);
            dialog.setWidth(440);

            DialogPane dialogPane = new DialogPane();
            dialogPane.setPrefHeight(240);
            dialogPane.setPrefWidth(440);

            Pane pane = new Pane();
            pane.setPrefHeight(240);
            pane.setPrefWidth(440);

            Text text = new Text("type the name of the server in the field below");
            text.setWrappingWidth(360);
            text.setFont(javafx.scene.text.Font.font(17));
            text.setLayoutX(20);
            text.setLayoutY(70);
            text.setTextAlignment(TextAlignment.CENTER);

            TextField serverName = new TextField();
            serverName.setLayoutX(35);
            serverName.setLayoutY(110);
            serverName.setPrefHeight(25);
            serverName.setPrefWidth(280);

            Text warning = new Text();
            warning.setWrappingWidth(360);
            warning.setFont(Font.font(14));
            warning.setFill(Color.RED);
            warning.setLayoutX(40);
            warning.setLayoutY(160);
            text.setTextAlignment(TextAlignment.CENTER);

            Button ok= new Button("ok");
            ok.setOnAction((ActionEvent event2) -> {
                try {
                    if (serverName.getText().equals("")){
                        warning.setText("type a name first!");
                    }
                    else {
                        out.writeObject(Command.newServer(currentUser, serverName.getText()));
                        Data data = (Data) in.readObject();
                        if (!(boolean) data.getPrimary())
                            warning.setText("this name is already taken!");
                        else {
                            dialog.setResult(Boolean.TRUE);
                            dialog.close();
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
                    }
                } catch (IOException | ClassNotFoundException ex){
                    ex.printStackTrace();
                }

            });
            ok.setPrefHeight(25);
            ok.setPrefWidth(50);
            ok.setLayoutX(320);
            ok.setLayoutY(110);

            Button cancel = new Button("cancel");
            cancel.setOnAction((ActionEvent cancelEvent) -> {
                dialog.setResult(Boolean.TRUE);
                dialog.close();
                changeToFriendsView(cancelEvent);
            });
            cancel.setPrefHeight(25);
            cancel.setPrefWidth(50);
            cancel.setLayoutX(375);
            cancel.setLayoutY(110);

            pane.getChildren().addAll(new ArrayList<>(Arrays.asList(text, serverName, ok, warning, cancel)));
            dialogPane.setContent(pane);
            dialog.setDialogPane(dialogPane);
            dialog.show();
        });

        btn.setPrefHeight(40);
        btn.setPrefWidth(servers_grid.getPrefWidth());
        servers_grid.addColumn(0,btn);

    }

    /**
     * when the discord button is pressed, this will be activated
     * @param event
     */
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

    /**
     * adds channels of server from field to GUI
     * and gives them eventhandler
     */
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
                            Thread.sleep(100);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }catch (InterruptedException e){
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

    /**
     * adds all the messages and messages pic from field to GUI
     */
    public void addMessages(){
        messages_grid.getChildren().clear();
        messages_grid.setVgap(5);

        for(int i =0;i<messages.size();i++){
            Message message = messages.get(i);
            UserShort userShort = messagesPic.get(i);
            if(message.getSourceInfo().get(0).equals(currentUser)){
                messages_grid.addColumn(0,new Text(""));
                messages_grid.addColumn(2, userShort.profileStatus(25.0));
            }
            else {
                messages_grid.addColumn(0, userShort.profileStatus(25.0));
                messages_grid.addColumn(2,new Text(""));
            }


            TextFlow textFlow = new TextFlow(new Text(message.getSourceInfo().get(0)+" :\n"+message.getText()+"\n"));
            ToggleGroup group = new ToggleGroup();
            RadioButton like = new RadioButton("like: "+message.getLikes()+"      ");
            RadioButton dislike = new RadioButton("dislike: "+message.getDislikes()+"      ");
            RadioButton laugh = new RadioButton("laugh: "+message.getLaughs()+"       ");
            like.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Command cmd = Command.newReaction(currentUser,message,"like");
                    try {
                        out.writeObject(cmd);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            dislike.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Command cmd = Command.newReaction(currentUser,message,"dislike");
                    try {
                        out.writeObject(cmd);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            laugh.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Command cmd = Command.newReaction(currentUser,message,"laugh");
                    try {
                        out.writeObject(cmd);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            group.getToggles().addAll(like,dislike,laugh);
            textFlow.getChildren().addAll(like,dislike,laugh);
            if(currentRole.getValues().charAt(7)=='1'){
                RadioButton pin = new RadioButton("pin");
                pin.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Command cmd = Command.pinMsg(currentUser,message);
                        try {
                            out.writeObject(cmd);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                textFlow.getChildren().add(pin);
            }
            if(message instanceof FileMessage){
                RadioButton download = new RadioButton("download");
                download.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        ((RadioButton) event.getSource()).setDisable(true);
                        downloadFile(message.getText());
                    }
                });
                textFlow.getChildren().add(download);
            }
            if(message.getSourceInfo().get(0).equals(currentUser)){
                textFlow.setStyle("-fx-background-color: rgb(254,220,235); -fx-border-radius: 5px; ");
            }
            else{
                textFlow.setStyle("-fx-background-color: rgb(176,223,255); -fx-border-radius: 5px;");
            }
            textFlow.setPrefWidth(430);
            textFlow.setPadding(new Insets(5));
            textFlow.setBorder(Border.stroke(Color.BLACK));
            messages_grid.addColumn(1,textFlow);
        }
        messages_scroll.setVvalue(1.0);
    }

    /**
     * if a new message comes, this will show it in GUI
     * @param message
     */
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
        TextFlow textFlow = new TextFlow(new Text(message.getSourceInfo().get(0)+" :\n"+message.getText()+"\n"));
        ToggleGroup group = new ToggleGroup();
        RadioButton like = new RadioButton("like: "+message.getLikes()+"      ");
        RadioButton dislike = new RadioButton("dislike: "+message.getDislikes()+"      ");
        RadioButton laugh = new RadioButton("laugh: "+message.getLaughs()+"      ");
        like.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Command cmd = Command.newReaction(currentUser,message,"like");
                try {
                    out.writeObject(cmd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        dislike.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Command cmd = Command.newReaction(currentUser,message,"dislike");
                try {
                    out.writeObject(cmd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        laugh.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Command cmd = Command.newReaction(currentUser,message,"laugh");
                try {
                    out.writeObject(cmd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        group.getToggles().addAll(like,dislike,laugh);
        textFlow.getChildren().addAll(like,dislike,laugh);
        if(currentRole.getValues().charAt(7)=='1'){
            RadioButton pin = new RadioButton("pin");
            pin.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Command cmd = Command.pinMsg(currentUser,message);
                    try {
                        out.writeObject(cmd);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            textFlow.getChildren().add(pin);
        }
        if(message instanceof FileMessage){
            RadioButton download = new RadioButton("download");
            download.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    ((RadioButton) event.getSource()).setDisable(true);
                    downloadFile(message.getText());
                }
            });
            textFlow.getChildren().add(download);
        }

        if(message.getSourceInfo().get(0).equals(currentUser)){
            textFlow.setStyle("-fx-background-color: rgb(254,220,235); -fx-border-radius: 5px;");
        }
        else {
            textFlow.setStyle("-fx-background-color: rgb(176,223,255); -fx-border-radius: 5px;");
        }
        textFlow.setPadding(new Insets(5));
        textFlow.setBorder(Border.stroke(Color.BLACK));
        textFlow.setPrefWidth(430);
        messages_grid.addColumn(1, textFlow);
        messages_scroll.setVvalue(1.0);
    }

    /**
     * when the send button is pressed, this will
     * get the text areas text and calls send text thread
     */
    public void sendTextMessage(){
        new SendText(this,message_textField.getText()).restart();
        message_textField.clear();
    }

    /**
     * when the file button is pressed, this will
     * call the server and server calls message reader to upload
     */
    public void sendFileMessage(){
        Command cmd = null;
            cmd = Command.upload(null,currentServer,currentChannel, true);
        try {
            out.writeObject(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> senderInfo = new ArrayList<>(Arrays.asList(currentUser, currentChannel, currentServer));
        FileUploader fileUploader = new FileUploader(fout, senderInfo);
        fileUploader.start();
    }

    /**
     * this is called when user presses download
     * @param filename
     */
    public void downloadFile(String filename) {
        try {
              Command  cmd = Command.download(currentServer, currentChannel,filename, true);

            out.writeObject(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileDownloader fileDownloader = new FileDownloader(fin);
        fileDownloader.start();
    }

}

/**
 * this thread is for when user wants to send a file
 */
class SendText extends Service<Void>{
    private ChannelController cc;
    /**
     * text of message
     */
    private String body;


    public SendText(ChannelController cc,String body){
        this.cc= cc;
        this.body = body;
    }

    /**
     * sends the message to server ina different thread that the main one
     * @return
     */
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

/**
 * this thread kind of updates all the infos in the GUI
 * also is called when user goes to another server
 */
class GoToServer extends Service<Void>{

    private ChannelController cc;
    /**
     * the server the user wants the information of to be shown
     */
    private String servername;
    public GoToServer(ChannelController cc,String servername){
        this.cc = cc;
        this.servername = servername;
    }

    /**
     * gets infos for servers,channels,messages,messages pic fields
     * it goes to general channel which all servers have
     * @return
     */
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                cc.currentServer=servername;
                cc.out.writeObject(Command.userServers(cc.currentUser));
                System.out.println("got the servers");
                Data dt = (Data) cc.in.readObject();
                System.out.println(dt.getKeyword()+"!");
                cc.servers =(ArrayList<String>) dt.getPrimary();
                cc.out.writeObject(Command.userChannels(cc.currentUser,servername));
                dt =(Data) cc.in.readObject();
                System.out.println(dt.getKeyword() +" instead of user channels");
                cc.channels = (ArrayList<String>) dt.getPrimary();
                cc.currentChannel = "general";
                cc.out.writeObject(Command.getChannelMsgs(cc.currentUser,cc.currentServer,cc.currentChannel,5));
                dt =(Data) cc.in.readObject();
                System.out.println(dt.getKeyword());
                cc.messages = (ArrayList<Message>) dt.getPrimary();
                cc.messagesPic= (ArrayList<UserShort>)dt.getSecondary();
                cc.out.writeObject(Command.getRole(cc.currentUser,servername));
                dt =(Data) cc.in.readObject();
                System.out.println(dt.getKeyword());
                cc.currentRole = (Role) dt.getPrimary();
                cc.out.writeObject(Command.getChannelMembers(cc.currentUser,cc.currentServer,cc.currentChannel));
                dt =(Data) cc.in.readObject();
                System.out.println(dt.getKeyword());
                cc.members = (ArrayList<UserShort>) dt.getPrimary();
                cc.out.writeObject(Command.tellChannel(cc.currentUser,cc.currentServer,"general"));
                cc.in.readObject();
                return null;
            }
        };
    }

    /**
     * this method runs in main thread
     * and updates the gui with the new infos
     */
    @Override
    protected void succeeded() {
        cc.server_name.setText(servername);
        cc.addServers();
        cc.addChannels();
        cc.channel_name.setText("general");
        cc.addMembers();
        cc.addMessages();
        MessageReader mr = new MessageReader(cc);
        mr.setDaemon(true);
        mr.start();
    }
}

/**
 * this thread is for when user goes from one channel to the other
 * in the same server
 */
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
                cc.messagesPic= (ArrayList<UserShort>)dt.getSecondary();
                cc.out.writeObject(Command.getRole(cc.currentUser,cc.currentServer));
                dt =(Data) cc.in.readObject();
                System.out.println(dt.getKeyword());
                cc.currentRole = (Role) dt.getPrimary();

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
                MessageReader mr = new MessageReader(cc);
                mr.setDaemon(true);
                mr.start();
            }
        };


    }
}

/**
 * this thread gets the pinned messages from server
 * and shows them to user
 */
class AddPinnedMessages extends Service<Void>{
    ChannelController cc;
    ArrayList<Message> pinnedmsgs;
    ArrayList<UserShort> pics;
    public AddPinnedMessages(ChannelController cc){
        this.cc=cc;
    }

    /**
     * this thread is ran in another thread
     * this will get the pinned messages from server
     * @return
     */
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Command cmd = Command.getPinnedMsgs(cc.currentUser,cc.currentServer,cc.currentChannel);
                cc.out.writeObject(cmd);
                Data dt =(Data) cc.in.readObject();
                pinnedmsgs = (ArrayList<Message>) dt.getPrimary();
                pics = (ArrayList<UserShort>) dt.getSecondary();
                return null;
            }
        };
    }

    /**
     * this is ran in the main thread. this will show the pinned messages
     */
    @Override
    protected void succeeded() {
        cc.messages_grid.getChildren().clear();
        cc.messages_grid.setVgap(5);
        for (Message message : pinnedmsgs){
            TextFlow textFlow = new TextFlow(new Text(message.getSourceInfo().get(0)+" :\n"+message.getText()+"\n"));
            ToggleGroup group = new ToggleGroup();
            RadioButton like = new RadioButton("like: "+message.getLikes()+"      ");
            RadioButton dislike = new RadioButton("dislike: "+message.getDislikes()+"      ");
            RadioButton laugh = new RadioButton("laugh: "+message.getLaughs()+"       ");
            like.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Command cmd = Command.newReaction(cc.currentUser,message,"like");
                    try {
                        cc.out.writeObject(cmd);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            dislike.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Command cmd = Command.newReaction(cc.currentUser,message,"dislike");
                    try {
                        cc.out.writeObject(cmd);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            laugh.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Command cmd = Command.newReaction(cc.currentUser,message,"laugh");
                    try {
                        cc.out.writeObject(cmd);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            group.getToggles().addAll(like,dislike,laugh);
            textFlow.getChildren().addAll(like,dislike,laugh);
           
            if(message instanceof FileMessage){
                RadioButton download = new RadioButton("download");
                download.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        ((RadioButton) event.getSource()).setDisable(true);
                        cc.downloadFile(message.getText());
                    }
                });
                textFlow.getChildren().add(download);
            }
                textFlow.setStyle("-fx-background-color: rgb(176,223,255); -fx-border-radius: 5px;");
            textFlow.setPrefWidth(430);
            textFlow.setPadding(new Insets(5));
            textFlow.setBorder(Border.stroke(Color.BLACK));
            cc.messages_grid.addColumn(1,textFlow);
        }
        cc.messages_scroll.setVvalue(1.0);
    }
}

/**
 * this gets the new messages in real time
 * and if the data it gets is exit, it will stop
 */
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
