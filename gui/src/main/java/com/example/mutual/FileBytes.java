package com.example.mutual;
import java.io.Serializable;

/**
 * holds the information of a file to be transferred between server and client side
 */
public class FileBytes implements Serializable {
    private byte[] bytes;
    private String fileName;
    private FileMessage fileMessage;

    public void setBytes(byte[] bytes, String filename) {
        this.bytes = bytes;
        this.fileName = filename;
    }

    private FileBytes() {
    }

    /**
     * creates a new object of class before sending it to server
     *
     * @param fileMessage
     * @param bytes
     * @return the new fileBytes object
     */
    public static FileBytes toServer(FileMessage fileMessage, byte[] bytes) {
        FileBytes fb = new FileBytes();
        fb.fileMessage = fileMessage;
        fb.bytes = bytes;
        return fb;
    }

    public FileMessage getFileMessage() {
        return fileMessage;
    }

    /**
     * creates a new object of class before sending it to client
     *
     * @param fileName name of the file
     * @param bytes
     * @return the new fileBytes object
     */
    public static FileBytes toClient(String fileName, byte[] bytes) {
        FileBytes fb = new FileBytes();
        fb.bytes = bytes;
        fb.fileName = fileName;
        return fb;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
