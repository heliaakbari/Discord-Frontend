/**
 * this is shown when user is in a pv
 * it correspondes to pv-view.fxml
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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
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

public class PvController {

    protected String otherPerson;
    protected String currentUser;
    /**
     * current user's usershort
     */
    protected UserShort meShort;
    /**
     * the other person's usershort
     */
    protected UserShort youShort;
    /**
     * all friends of the current user
     */
    protected ArrayList<UserShort> allFriends = new ArrayList<>();
    /**
     * all friend with a chat history with user
     */
    protected ArrayList<UserShort> directChats = new ArrayList<>();
    /**
     * servers the user is in
     */
    protected ArrayList<String> servers= new ArrayList<>();
    /**
     * messages between the two
     */
    protected ArrayList<Message> messages= new ArrayList<>();
    protected ObjectOutputStream out;
    protected ObjectInputStream in;
    protected ObjectOutputStream fout;
    protected ObjectInputStream fin;
    /**
     * is message reader thread working or not
     */
    protected Boolean isMessageReader=false;

    @FXML
    protected GridPane messages_grid;

    @FXML
    protected GridPane title_grid;

    @FXML
    protected ScrollPane messages_scroll;

    @FXML
    protected TextField search_text;

    @FXML
    protected Button search_button;

    @FXML
    protected GridPane directs_grid;

    @FXML
    protected TextArea message_textField;

    @FXML
    protected Button send_button;

    @FXML
    protected Button file_button;

    @FXML
    protected GridPane servers_grid;

    @FXML
    protected Label pv_name;

    @FXML
    protected Label server_name;

    public PvController(ObjectInputStream in,ObjectOutputStream out,ObjectInputStream fin,ObjectOutputStream fout,String currentUser,String otherPerson){
        this.out = out;
        this.in = in;
        this.fin = fin;
        this.fout = fout;
        this.currentUser = currentUser;
        this.otherPerson = otherPerson;
    }

    @FXML
    public void initialize() {
        new GoToPv(this).restart();
    }

    /**
     * opens a new direct from search tab
     * @param event
     */
    public void newDirectFromSearch(Event event){
        String text = search_text.getText();
        search_text.clear();

        for(UserShort friend : allFriends){
            if(friend.getUsername().equals(text)){
                if(isMessageReader) {
                    try {
                        out.writeObject(Command.lastseenPv(currentUser,otherPerson));
                        Thread.sleep(100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
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

    /**
     * add the directs from field to the Gui
     */
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
                    if(isMessageReader) {
                        try {
                            out.writeObject(Command.lastseenPv(currentUser,otherPerson));
                            Thread.sleep(100);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
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

    /**
     * adds servers from field to GUI
     */
    public void addServers(){
        servers_grid.getChildren().clear();
        servers_grid.setVgap(5);
        Button btn = new Button("Discord");
        PvController pcOld = this;
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(isMessageReader) {
                    try {
                        out.writeObject(Command.lastseenPv(currentUser,otherPerson));
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
        PvController pc = this;
        for(String s : servers){
            btn = new Button(s);
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(isMessageReader) {
                        try {
                            out.writeObject(Command.lastseenPv(currentUser,otherPerson));
                            Thread.sleep(100);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    }
                    FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("channel-view.fxml"));
                    ChannelController channelController = new ChannelController(in,out,fin,fout,currentUser,((Button)(event.getSource())).getText(),"general");
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

        btn.setOnAction((ActionEvent event) -> {
            if(isMessageReader) {
                try {
                    out.writeObject(Command.lastseenPv(currentUser,otherPerson));
                    Thread.sleep(100);
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

            System.out.println("clicked");

            Dialog<Boolean> dialog = new Dialog<>();
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
            text.setFont(Font.font(17));
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
                            changeToServerSetting(serverName.getText(), event);
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
     * when the server setting button is pressed, this method is called
     * @param serverName
     * @param event
     */
    public void changeToServerSetting(String serverName, Event event){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("server-setting-view.fxml"));
        Stage stage = (Stage)(((Node) event.getSource()).getScene().getWindow());
        fxmlLoader.setController(new ServerSettingController(null, currentUser, serverName, out, in, fout, fin, stage));
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
     * if discord button is pressed, this method will be called
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
     * adds messages from field to GUI
     */
    public void addMessages(){
        messages_grid.getChildren().clear();
        messages_grid.setVgap(5);
        for (Message message : messages){
            TextFlow textFlow = new TextFlow();
            textFlow.getChildren().add(new Text(message.getSourceInfo().get(0)+" :\n"+message.getText()+"\n"));
            if(message.getSourceInfo().get(0).equals(currentUser)){
                textFlow.setStyle("-fx-background-color: rgb(254,220,235); -fx-border-radius: 5px;");
                messages_grid.addColumn(0,new Text(""));
                messages_grid.addColumn(2, meShort.profileStatus(25.0));
            }
            else {
                textFlow.setStyle("-fx-background-color: rgb(176,223,255); -fx-border-radius: 5px;");
                messages_grid.addColumn(0, youShort.profileStatus(25.0));
                messages_grid.addColumn(2, new Text(""));
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

            textFlow.setPadding(new Insets(5));
            textFlow.setBorder(Border.stroke(Color.BLACK));
            textFlow.setPrefWidth(430);
            messages_grid.addColumn(1, textFlow);
        }
        messages_scroll.setVvalue(1.0);
    }

    /**
     * adds the new instant message to GUI
     * @param message
     */
    public void addNewMessage(Message message){
        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().add(new Text(message.getSourceInfo().get(0)+" :\n"+message.getText()+"\n"));
        if(message.getSourceInfo().get(0).equals(currentUser)){
            textFlow.setStyle("-fx-background-color: rgb(254,220,235); -fx-border-radius: 5px;");
            messages_grid.addColumn(0,new Text(""));
            messages_grid.addColumn(2, meShort.profileStatus(25.0));
        }
        else {
            textFlow.setStyle("-fx-background-color: rgb(176,223,255); -fx-border-radius: 5px;");
            messages_grid.addColumn(0, youShort.profileStatus(25.0));
            messages_grid.addColumn(2, new Text(""));
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

        textFlow.setPadding(new Insets(5));
        textFlow.setBorder(Border.stroke(Color.BLACK));
        textFlow.setPrefWidth(430);
        messages_grid.addColumn(1, textFlow);
        messages_scroll.setVvalue(1.0);
    }

    /**
     * when the send button is pressed, this method is called
     */
    public void sendTextMessage(){
        new SendTextPv(this,message_textField.getText()).restart();
        message_textField.clear();
    }

    /**
     * when the file button is pressed, this method will be called
     */
    public void sendFileMessage(){
        Command cmd = null;
        cmd = Command.upload(otherPerson,null,null,false);
        try {
            out.writeObject(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> senderInfo = new ArrayList<>(Arrays.asList(currentUser));
        FileUploader fileUploader = new FileUploader(fout, senderInfo);
        fileUploader.start();
    }

    /**
     * when download is pressed, this method is called
     * @param filename
     */
    private void downloadFile(String filename) {
        try {
            Command  cmd = Command.download(currentUser,otherPerson ,filename, false);

            out.writeObject(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileDownloader fileDownloader = new FileDownloader(fin);
        fileDownloader.start();
    }
}

/**
 * this class extends service and is called for sending a message to server
 */
class SendTextPv extends Service<Void>{
    private PvController pc;
    private String body;

    public SendTextPv(PvController pc,String body){
        this.pc= pc;
        this.body = body;
    }

    /**
     * this method is ran in another thread than main
     * @return
     */
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                TextMessage msg = new TextMessage(pc.currentUser,body,LocalDateTime.now());
                Command cmd = Command.newPvMsg(pc.currentUser,pc.otherPerson,msg);
                pc.out.writeObject(cmd);
                return null;
            }
        };
    }
}

/**
 * this is called when user goes to another pv
 * or when we want to update infos
 */
class GoToPv extends Service<Void>{

    private PvController pc;

    public GoToPv(PvController pc){
        this.pc = pc;
    }

    /**
     * gets infos from server
     * @return
     */
    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Command cmd = Command.getDirectChats(pc.currentUser);
                pc.out.writeObject(cmd);
                Data dt = (Data) pc.in.readObject();
                System.out.println(dt.getKeyword());
                pc.directChats = (ArrayList<UserShort>) dt.getPrimary();
                pc.out.writeObject(Command.userServers(pc.currentUser));
                dt = (Data) pc.in.readObject();
                pc.servers = (ArrayList<String>)dt.getPrimary();
                cmd = Command.getFriends(pc.currentUser);
                pc.out.writeObject(cmd);
                dt = (Data) pc.in.readObject();
                System.out.println(dt.getKeyword());
                pc.allFriends = (ArrayList<UserShort>) dt.getPrimary();
                pc.out.writeObject(Command.getUser(pc.currentUser));
                dt = (Data) pc.in.readObject();
                System.out.println(dt.getKeyword()+"!");
                pc.meShort =(UserShort) dt.getSecondary();
                pc.out.writeObject(Command.getUser(pc.otherPerson));
                dt =(Data) pc.in.readObject();
                pc.youShort = (UserShort) dt.getSecondary();
                pc.out.writeObject(Command.getPvMsgs(pc.currentUser,pc.otherPerson,5));
                dt =(Data) pc.in.readObject();
                pc.messages = (ArrayList<Message>) dt.getPrimary();
                pc.out.writeObject(Command.tellPv(pc.currentUser,pc.otherPerson));
                pc.in.readObject();
                return null;
            }
        };
    }

    /**
     * shows info in gui
     */
    @Override
    protected void succeeded() {
        pc.pv_name.setText(pc.otherPerson);
        pc.title_grid.addColumn(0,pc.youShort.profileStatus(25.0));
        pc.addServers();
        pc.addMessages();
        pc.addDirects();
        MessageReaderPv mr = new MessageReaderPv(pc);
        mr.setDaemon(true);
        mr.start();
    }
}

/**
 * reads instant messages
 * also if an exit data is sent, it will stop
 */
class MessageReaderPv extends Thread {

    private PvController pc;

    public MessageReaderPv(PvController pc) {
        this.pc = pc;
    }

    @Override
    public void run() {
        System.out.println("message reader started");;
        pc.isMessageReader=true;
        Message message = null;
        Data data= null ;

        while (true) {
            System.out.print(" ");
            try {
                Object obj = pc.in.readObject();
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
                pc.isMessageReader=false;
                break;
            }
            if (data.getKeyword().equals("exitChat")) {
                System.out.println("got the exit chat");
                pc.isMessageReader=false;
                return;
            }
            if(data.getKeyword().equals("newPvMsg")){
                message = (Message) data.getPrimary();
                pc.messages.add(message);
                final Message msg = message;
                Platform.runLater(()->{
                    pc.addNewMessage(msg);
                });
            }

        }

    }

}
