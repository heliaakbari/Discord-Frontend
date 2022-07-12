package com.example.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.example.mutual.*;
/**
 * handles every thing related to server including communicating with database and clients, delivering messages and command and data and ...
 */
public class ServerSide {

    private ServerSocket serverSocket;
    private ServerSocket fileSocket;
    private HashMap<String, ClientHandler> clientHandlers;
    private Socket client;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ObjectOutputStream fout;
    private ObjectInputStream fin;
    private DatabaseManager dbm = new DatabaseManager(this);
    //first one is the person and the other is their friend
    private HashMap<String, String> activePvs = new HashMap<>();
    //first one is the user and the other is array [server,channel] form
    private HashMap<String, ArrayList<String>> activeChannels = new HashMap<>();

    public ServerSide() {
        try {
            serverSocket = new ServerSocket(8644);
            fileSocket = new ServerSocket(8999);
            clientHandlers = new HashMap<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, ClientHandler> getClientHandlers() {
        return clientHandlers;
    }

    /**
     * starts the server nad database manager, sets the object input and output stream for the first and second port.
     * accepts clients and opens a new thread for after accepting each
     */
    public void startServer() {
        dbm.start();

        while (true) {

            // making connection with client
            System.out.println("waiting for connection");
            try {
                client = serverSocket.accept();
                out = new ObjectOutputStream(client.getOutputStream());
                in = new ObjectInputStream(client.getInputStream());
                client = fileSocket.accept();
                fout = new ObjectOutputStream(client.getOutputStream());
                fin = new ObjectInputStream(client.getInputStream());

            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("new client connected successfully");

            // creating new thread for handling new connection
            ClientHandler clientHandler;
            try {
                clientHandler = new ClientHandler(client, in, out, fin, fout, this);
                Thread thread = new Thread(clientHandler);
                thread.start();
            } catch (SocketException e) {
                System.out.println("client disconnected successfully");
            }
        }
    }

    /**
     * adds a new clientHandler to the list of threads
     * @param username username of the client or the specific id which the clientHandler will be recognized with
     * @param clientHandler new handler thread to be added to list
     */
    public void addClientHandler(String username, ClientHandler clientHandler) {
        clientHandlers.put(username, clientHandler);
        System.out.println(username + " was added to handlers");
        printClientHandlers();
    }

    /**
     * receives command from client handler and do operations based on its keyword.
     * sends the command to cmdManager to be processed and receives data object in answer and transfers it back to client handler
     * @param cmd
     * @param clientHandler
     * @return
     */
    public Data moveCmd(Command cmd, ClientHandler clientHandler) {
        Data dt = dbm.cmdManager.process(cmd);
        switch (dt.getKeyword()) {
            case "checkSignUp":
            case "checkLogin":
                if (((boolean) dt.getPrimary())) {
                    addClientHandler(dt.getUser(), clientHandler);
                }
                break;
            case "checkChangeUsername":
                if (((boolean) dt.getPrimary())) {
                    if (clientHandlers.containsKey(dt.getUser())) {
                        ClientHandler ch = clientHandlers.get(dt.getUser());
                        clientHandlers.remove(dt.getUser());
                        clientHandlers.put((String) dt.getSecondary(), ch);
                    }

                    if (activePvs.containsKey(dt.getUser())) {
                        String theOther = activePvs.get(dt.getUser());
                        activePvs.remove(dt.getUser());
                        activePvs.put((String) dt.getSecondary(), theOther);
                    } else if (activeChannels.containsKey(dt.getUser())) {
                        ArrayList<String> place = activeChannels.get(dt.getUser());
                        activeChannels.remove(dt.getUser());
                        activeChannels.put((String) dt.getSecondary(), place);
                    }
                }
                break;
            case "checkChangeServerName":
                for (HashMap.Entry<String, ArrayList<String>> set :
                        activeChannels.entrySet()) {
                    if (set.getValue().get(0).equals(dt.getServer())) {
                        ArrayList<String> newPlace = new ArrayList<>();
                        newPlace.add((String) dt.getSecondary());
                        newPlace.add(set.getValue().get(1));
                        activeChannels.replace(set.getKey(), newPlace);
                    }
                }
                break;

            case "checkDeleteChannel":
                if (((boolean) dt.getPrimary()) == true) {
                    for (HashMap.Entry<String, ArrayList<String>> set :
                            activeChannels.entrySet()) {
                        if (set.getValue().get(0).equals(dt.getServer()) && set.getValue().get(1).equals(dt.getChannel())) {
                            activeChannels.remove(set);
                        }
                    }
                }
                break;

            case "checkDeleteServer":
                if (((boolean) dt.getPrimary()) == true) {
                    for (HashMap.Entry<String, ArrayList<String>> set :
                            activeChannels.entrySet()) {
                        if (set.getValue().get(0).equals(dt.getServer())) {
                            activeChannels.remove(set);
                        }
                    }
                }
                break;
        }

        switch (cmd.getKeyword()) {
            case "exit":
                System.out.println("got exit command");
                String person = null;
                for (HashMap.Entry<String, ClientHandler> pair : clientHandlers.entrySet()) {
                    if (pair.getValue().equals(clientHandler)) {
                        person = pair.getKey();
                        break;
                    }
                }
                if (activePvs.containsKey(person)) {
                    dbm.cmdManager.process(Command.lastseenPv(person, activePvs.get(person)));
                }
                if (activeChannels.containsKey(person)) {
                    dbm.cmdManager.process(Command.lastseenChannel(person, activeChannels.get(person).get(0), activeChannels.get(person).get(1)));
                }
                clientHandlers.remove(person);
                break;
            case "lastseenPv":
                activePvs.remove(cmd.getUser());
                break;

            case "lastseenChannel":
                activeChannels.remove(cmd.getUser());
                System.out.println(cmd.getServer()+"/"+cmd.getChannel());
                break;

            case "tellPv":
                activePvs.put(cmd.getUser(), (String) cmd.getPrimary());
                break;

            case "tellChannel":
                ArrayList<String> place = new ArrayList<>(Arrays.asList(cmd.getServer(), cmd.getChannel()));
                activeChannels.put(cmd.getUser(), place);
                System.out.println(cmd.getServer()+"/"+cmd.getChannel());
                break;

            case "newPvMsg":
                instantPvMsg(cmd);
                break;

            case "newChannelMsg":
                instantChannelMsg(cmd);
                break;
        }
        return dt;
    }

    /**
     * if the command is newPvMsg, delivers it to the other client handler in private chat
     * @param cmd
     */
    public void instantPvMsg(Command cmd) {
        if (cmd.getKeyword().equals("newPvMsg")) {
            printClientHandlers();
            String receiver = (String) cmd.getSecondary();
            if (activePvs.containsKey(receiver)) {
                String sender = activePvs.get(receiver);
                if (sender.equals(cmd.getUser())) {
                    Data dt = Data.newPvMsg(receiver, (Message) cmd.getPrimary());
                    clientHandlers.get(receiver).sendInstantMessage(dt);
                }
            }
            if (activePvs.containsKey(cmd.getUser())) {
                    Data dt = Data.newPvMsg(cmd.getUser(), (Message) cmd.getPrimary());
                    clientHandlers.get(cmd.getUser()).sendInstantMessage(dt);

            }
        }
    }

    /**
     * if the command is newChannelMsg, delivers it to all other client handlers who are currently is channel chat
     * @param cmd
     */
    public void instantChannelMsg(Command cmd) {
        if (cmd.getKeyword().equals("newChannelMsg")) {
            printClientHandlers();
            ArrayList<String> onlinePeople = new ArrayList<>();
            for (HashMap.Entry<String, ArrayList<String>> set :
                    activeChannels.entrySet()) {
                if (set.getValue().get(0).equals(cmd.getServer()) && set.getValue().get(1).equals(cmd.getChannel())) {
                    onlinePeople.add(set.getKey());
                }
            }
            Data dt = Data.newChannelMsg(cmd.getUser(), cmd.getServer(), cmd.getChannel(), (Message) cmd.getPrimary());
            for (String person : onlinePeople) {
                clientHandlers.get(person).sendInstantMessage(dt);
            }
        }
    }

    public void printClientHandlers() {
        System.out.println("!!!!!!!!!!!!!!!!!!!");
        for (HashMap.Entry<String, ClientHandler> set :
                clientHandlers.entrySet()) {

            // Printing all elements of a Map
            System.out.println(set.getKey());
        }
        System.out.println("!!!!!!!!!!!!!!!!!!");
    }

}
