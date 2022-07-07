package com.example.server;

import com.example.mutual.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

import static java.nio.file.Files.readAllBytes;

/**
 * a seperated thread to handle each client's command and data transfer
 */
public class ClientHandler extends Thread {
    private Socket client;
    private ServerSide serverSide;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ObjectInputStream fin;
    private ObjectOutputStream fout;
    private DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyy-mm-dd hh-mm-ss");

    public ClientHandler(Socket client, ObjectInputStream in, ObjectOutputStream out, ObjectInputStream fin, ObjectOutputStream fout, ServerSide serverSide) throws SocketException {
        this.client = client;
        this.in = in;
        this.out = out;
        this.fin = fin;
        this.fout = fout;
        this.serverSide = serverSide;
    }

    @Override
    public void run() {
        // starting the listener thread to receive commands
        Listener listener = new Listener(this, in);
        listener.start();
        try {
            listener.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * this method s called inside the listener thread and is used to transfer commands from listener thread to clientHandler thread and then to server
     * @param cmd the command to be transferred
     */
    public void getCommandFromListener(Command cmd) {

        // receiving data from server
        Data dt = serverSide.moveCmd(cmd, this);

        if (cmd.getKeyword().equals("exit")) {
            return;
        }

        if (!cmd.getKeyword().equals("download") && !cmd.getKeyword().equals("upload") && !cmd.getKeyword().equals("newPvMsg") && !cmd.getKeyword().equals("newChannelMsg")) {
            try {
                out.writeObject(dt);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (cmd.getKeyword().equals("upload")) {
            FileBytes fb = null;
            try {
                try {
                    fb = (FileBytes) fin.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String address = "C:\\DiscordFiles\\" + fb.getFileMessage().getSourceInfo().get(0) + fb.getFileMessage().getDateTime().format(fileFormatter) + fb.getFileMessage().getFileName();
            try (FileOutputStream fos = new FileOutputStream(address)) {
                System.out.println(fb.getBytes().length);
                fos.write(fb.getBytes());
                //fos.close // no need, try-with-resources auto close
            } catch (IOException e) {
                e.printStackTrace();
            }
            if ((boolean) cmd.getPrimary()) {
                serverSide.moveCmd(Command.newChannelMsg(fb.getFileMessage().getSourceInfo().get(0), fb.getFileMessage().getSourceInfo().get(2), fb.getFileMessage().getSourceInfo().get(1), fb.getFileMessage()), this);
            } else {
                serverSide.moveCmd(Command.newPvMsg(fb.getFileMessage().getSourceInfo().get(0), cmd.getUser(), fb.getFileMessage()), this);
            }
        }

        if (cmd.getKeyword().equals("download")) {
            byte[] bytes = null;
            String address = (String) dt.getPrimary();
            System.out.println(address);
            try {
                bytes = readAllBytes(Paths.get(address));
            } catch (NoSuchFileException e) {
                System.out.println("the file with path doesnt exists");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fout.writeObject(FileBytes.toClient((String) cmd.getPrimary(), bytes));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return;
    }


    public void sendInstantMessage(Data dt) {
        try {
            out.writeObject(dt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
