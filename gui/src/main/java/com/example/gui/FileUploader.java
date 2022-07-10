package com.example.gui;

import com.example.mutual.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;

import static java.nio.file.Files.readAllBytes;

/**
 * a seperated thread to transfer fileBytes from client to server to be saved in database and sent to all other users
 */
public class FileUploader extends Thread {
    private ObjectOutputStream fout;
    private ArrayList<String> senderInfo;

    public FileUploader(ObjectOutputStream fout, ArrayList<String> senderInfo) {
        this.fout = fout;
        this.senderInfo = senderInfo;
    }

    @Override
    public void run() {

        // opening a file dialog for user to choose a file for upload
        FileDialog dialog = new FileDialog((Frame) null, "Select File to Open");
        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        String fileNameAndType = dialog.getFile();
        String path = dialog.getDirectory() + "//" + dialog.getFile();

        // converting the file to byte array
        byte[] bytes = new byte[0];
        try {
            bytes = readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileMessage fileMessage;
        try {
            if (senderInfo.size() == 1)
                fileMessage = new FileMessage(senderInfo.get(0), fileNameAndType);
            else
                fileMessage = new FileMessage(senderInfo.get(0), senderInfo.get(2), senderInfo.get(1), fileNameAndType);

            // sending the fileByte to server
            FileBytes fileBytes = FileBytes.toServer(fileMessage, bytes);
            fout.writeObject(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
