
/**
 * a seperated thread to fetch file messages from server and saves it to local memory
 */

package com.example.gui;

import com.example.mutual.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class FileDownloader extends Thread {

    private ObjectInputStream fin;

    public FileDownloader(ObjectInputStream fin) {
        this.fin = fin;
    }

    @Override
    public void run() {

        /**
         *    getting fileBytes object from server
         */

        FileBytes fileBytes = null;
        try {
            fileBytes = (FileBytes) fin.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        /**
         * choosing a path and saving the file in it
         */
        String filePath = "C:\\discord";
        filePath = filePath + "\\" + fileBytes.getFileName();
        File file = new File(filePath);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(fileBytes.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
