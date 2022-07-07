package com.example.mutual;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.time.LocalDateTime;

/**
 * holds the information of a file message which is their name + format
 * inherits Message class
 */
public class FileMessage extends Message implements Serializable {
    private static final long serialVersionUID = 535296473722284L;
    private String fileName;

    public FileMessage(String sender, String fileName) throws IOException {
        super(sender);
        this.fileName = fileName;
        System.out.println(fileName);
    }

    public FileMessage(String sender, String server, String channel, String fileName) throws IOException {
        super(sender, channel, server);
        this.fileName = fileName;
    }

    public FileMessage(String sender, String server, String channel, LocalDateTime date, String fileName) throws IOException {
        super(sender, channel, server, date);
        this.fileName = fileName;
    }

    public FileMessage(String sender, LocalDateTime date, String fileName) throws IOException {
        super(sender, date);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }


    @Override
    public String toString() {
        if (super.getSourceInfo().size() > 1)
            return super.getSourceInfoAsString() + fileName + "\n" + super.getReactions();
        else
            return super.getSourceInfoAsString() + fileName;
    }

    @Override
    public String getText() {
        return fileName;
    }

    @Override
    public String shortFormToString() {
        return fileName;
    }
}
