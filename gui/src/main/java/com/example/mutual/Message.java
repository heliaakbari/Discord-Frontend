package com.example.mutual;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * holds the general information - including sender's info and date&time and reactions - of a message the user sends.
 * a message can be a file or a text message which are the children of this class
 */
public abstract class Message implements Serializable {
    private static final long serialVersionUID = 232222247967289L;
    private LocalDateTime dateTime;
    // holds sender username, channel and server respectively
    private ArrayList<String> sourceInfo;
    private HashMap<String, Integer> reactions = new HashMap<>();


    public Message(String sender) {
        sourceInfo = new ArrayList<>(List.of(sender));
        dateTime = LocalDateTime.now();
        reactions.put("like", 0);
        reactions.put("dislike", 0);
        reactions.put("laugh", 0);

    }

    public Message(String sender, String channel, String server) {
        sourceInfo = new ArrayList<>(Arrays.asList(sender, channel, server));
        dateTime = LocalDateTime.now();
        reactions.put("like", 0);
        reactions.put("dislike", 0);
        reactions.put("laugh", 0);
    }

    public Message(ArrayList<String> sourceInfo) {
        this.sourceInfo = sourceInfo;
        dateTime = LocalDateTime.now();
        reactions.put("like", 0);
        reactions.put("dislike", 0);
        reactions.put("laugh", 0);
    }

    //for database to message
    public Message(String sender, LocalDateTime date) {
        sourceInfo = new ArrayList<>(List.of(sender));
        dateTime = date;
        reactions.put("like", 0);
        reactions.put("dislike", 0);
        reactions.put("laugh", 0);

    }

    public Message(String sender, String channel, String server, LocalDateTime date) {
        sourceInfo = new ArrayList<>(Arrays.asList(sender, channel, server));
        dateTime = date;
        reactions.put("like", 0);
        reactions.put("dislike", 0);
        reactions.put("laugh", 0);
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }


    /**
     * @return a string containing sender's username, channel and server
     */
    public String getSourceInfoAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = sourceInfo.size() - 1; i >= 0; i--) {
            stringBuilder.append(sourceInfo.get(i)).append("-> ");
        }
        stringBuilder.append(" : ");
        return stringBuilder.toString();
    }

    /**
     * @return a string only containing sender's username
     */
    public String getWriterOnly() {
        return sourceInfo.get(0) + "-> ";
    }

    /**
     * @return an array list of sender info containing usernaem, channel and server name
     */
    public ArrayList<String> getSourceInfo() {
        return sourceInfo;
    }

    public String getReactions() {
        StringBuilder stringBuilder = new StringBuilder("   ");
        for (Map.Entry<String, Integer> reaction : reactions.entrySet()) {
            stringBuilder.append(reaction.getValue()).append(" ").append(reaction.getKey()).append("    ");
        }
        return stringBuilder.toString();
    }

    public void setLikes(int numberOfLikes) {
        reactions.put("like", numberOfLikes);
    }

    public void setDislikes(int numberOfDislikes) {
        reactions.put("dislike", numberOfDislikes);
    }

    public void setLaughs(int numberOfLaughs) {
        reactions.put("laugh", numberOfLaughs);
    }

    public abstract String getText();

    public abstract String shortFormToString();
}