package com.example.mutual;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Data  implements Serializable {

    private static final long serialVersionUID = 757982467897459L;
    private String keyword;
    private String user;
    private String server;
    private String channel;
    private Serializable primary;
    private Serializable secondary;

    private Data(String keyword){
        this.keyword=keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getUser() {
        return user;
    }

    public String getServer() {
        return server;
    }

    public String getChannel() {
        return channel;
    }

    public Object getPrimary() {
        return primary;
    }

    public Object getSecondary() {
        return secondary;
    }

    /**
     * returns if the signup of a new member was successful or not
     */
    public static @NotNull Data checkSignUp(String user, Boolean isSignedUp){
        Data data = new Data("checkSignUp");
        data.user = user;
        data.primary = isSignedUp;
        return data;
    }

    public static @NotNull Data checkLogin(String user, Boolean isLoggedIn){
        Data data = new Data("checkLogin");
        data.user = user;
        data.primary = isLoggedIn;
        return data;
    }
    /**
     * returns if the new channel was successfully created
     */
    public static @NotNull Data exitChat(String user){
        Data data = new Data("exitChat");
        data.user = user;
        return data;
    }

    /**
     * tells if making a new channel was successful
     * @param creator
     * @param server
     * @param channel
     * @param isCreated
     * @return
     */
    public static @NotNull Data checkNewChannel(String creator, String server, String channel, Boolean isCreated){
        Data data = new Data("checkNewChannel");
        data.user = creator;
        data.server = server;
        data.channel = channel;
        data.primary = isCreated;
        return data;
    }

    /**
     * tells if deleting a channel was sussful or not
     * @param server
     * @param channel
     * @param isDeleted
     * @return
     */
    public static @NotNull Data checkDeleteChannel(String server, String channel, Boolean isDeleted){
        Data dt = new Data("checkDeleteChannel");
        dt.server = server;
        dt.channel = channel;
        dt.primary = isDeleted;
        return dt;
    }

    /**
     * tells if deleting a channel was successful or not
     * @param server
     * @param isDeleted
     * @return
     */
    public static @NotNull Data checkDeleteServer(String server, Boolean isDeleted){
        Data dt = new Data("checkDeleteServer");
        dt.server = server;
        dt.primary = isDeleted;
        return dt;
    }

    /**
     * is sent when the command does not demand a data
     * @return
     */
    public static @NotNull Data fake(){
        Data data = new Data("fake");
        return data;
    }

    /**
     * gets the messages the user has not seen yet
     * @param user
     * @param messages
     * @return
     */
    public static @NotNull Data newMsgs(String user, ArrayList<Message> messages){
        Data data = new Data("newMsgs");
        data.user = user;
        data.primary = messages;
        return data;
    }

    /**
     * the new channel message is sent by this data to others
     * for instant messages
     * @param sender
     * @param server
     * @param channel
     * @param msg
     * @return
     */
    public static @NotNull Data newChannelMsg(String sender,String server, String channel, Message msg){
        Data data = new Data("newChannelMsg");
        data.user = sender;
        data.server = server;
        data.channel = channel;
        data.primary = msg;
        return data;
    }

    /**
     * the new pv message is sent by this command to the other person
     * for instant messages
     * @param receiver
     * @param msg
     * @return
     */
    public static @NotNull Data newPvMsg(String receiver, Message msg){
        Data data = new Data("newPvMsg");
        data.user = receiver;
        data.primary = msg;
        return data;
    }

    /**
     * by sending file's name and other details gets its path in server's archive
     * @param filepath
     * @return
     */
    public static Data giveFilePath(String filepath){
        Data data = new Data("giveFilePath");
        data.primary = filepath;
        return data;
    }

    /**
     *gets all requests that is sent to the person
     * @param user
     * @param allRequests
     * @return
     */
    public static @NotNull Data allFriendRequests(String user, ArrayList<String> allRequests){
        Data data = new Data("allFriendRequests");
        data.user = user;
        data.primary = allRequests;
        return data;
    }

    /**
     * gets the person's friends
     * @param user
     * @param friends
     * @return
     */
    public static @NotNull Data friends(String user, ArrayList<String> friends){
        Data data = new Data("friends");
        data.user = user;
        data.primary = friends;
        return data;
    }

    /**
     * gets pinned messages of a certain channel
     * @param user
     * @param server
     * @param channel
     * @param messages
     * @return
     */

    public static @NotNull Data pinnedMsgs(String user, String server, String channel, ArrayList<Message> messages){
        Data data = new Data("pinnedMsgs");
        data.user = user;
        data.server = server;
        data.channel = channel;
        data.primary = messages;
        return data;
    }

    /**
     * gets people who someone has blocked
     * @param user
     * @param peopleTheyBlocked
     * @return
     */
    public static @NotNull Data blockList(String user, ArrayList<String> peopleTheyBlocked){
        Data data = new Data("blockList");
        data.user = user;
        data.primary = peopleTheyBlocked;
        return data;
    }

    /**
     * people who have blocked a certain user
     * @param user
     * @param peopleWhoBlockedThem
     * @return
     */
    public static @NotNull Data blockedBy(String user, ArrayList<String> peopleWhoBlockedThem){
        Data data = new Data("blockedBy");
        data.user = user;
        data.primary = peopleWhoBlockedThem;
        return data;
    }

    /**
     * messages of a certain channel
     * @param user
     * @param server
     * @param channel
     * @param messages
     * @return
     */
    public static @NotNull Data channelMsgs (String user, String server, String channel, ArrayList<Message> messages){
        Data data = new Data("channelMsgs");
        data.user = user;
        data.server= server;
        data.channel = channel;
        data.primary = messages;
        return data;
    }

    /**
     * gives pv messages of two people
     * @param user
     * @param theOtherPerson
     * @param messages
     * @return
     */
    public static @NotNull Data PvMsgs (String user, String theOtherPerson, ArrayList<Message> messages){
        Data data = new Data("PvMsgs");
        data.user = user;
        data.server= theOtherPerson;
        data.primary = messages;
        return data;
    }

    /**
     * members of a given channel
     * @param user
     * @param server
     * @param channel
     * @param members
     * @return
     */
    public static @NotNull Data channelMembers(String user, String server, String channel, ArrayList<UserShort> members){
        Data data = new Data("channelMembers");
        data.user = user;
        data.server = server;
        data.channel = channel;
        data.primary = members;
        return data;
    }

    /**
     * members of a given server
     * @param user
     * @param server
     * @param membersAndRoles
     * @return
     */
    public static @NotNull Data serverMembers(String user, String server, HashMap<String,Role> membersAndRoles){
        Data data = new Data("serverMembers");
        data.user = user;
        data.server = server;
        data.primary = membersAndRoles;
        return data;
    }

    /**
     * list of channels a user is in
     * @param user
     * @param server
     * @param channels
     * @return
     */
    public static @NotNull Data userChannels(String user, String server, ArrayList<String> channels){
        Data data = new Data("userChannels");
        data.user = user;
        data.server = server;
        data.primary = channels;
        return data;
    }

    /**
     * list of servers that the person is in
     * @param user
     * @param servers
     * @return
     */
    public static @NotNull Data userServers(String user, ArrayList<String> servers){
        Data data = new Data("userServers");
        data.user = user;
        data.primary = servers;
        return data;
    }

    /**
     * gets everything of a user by its username
     * @param username
     * @param user
     * @return
     */
    public static @NotNull Data userInfo(String username, User user){
        Data data = new Data ("userInfo");
        data.user= username;
        data.primary = user;
        return data;
    }

    /**
     * sends reactions of a message in channel
     * @param user
     * @param message
     * @param reactions
     * @return
     */
    public static @NotNull Data reactions(String user, Message message, HashMap<String,Integer> reactions){
        Data data = new Data("reactions");
        data.primary = reactions;
        data.secondary = message;
        return data;
    }

    /**
     * gets the abilities of a certain role
     * @param user
     * @param server
     * @param role
     * @return
     */
    public static @NotNull Data role(String user, String server, Role role){
        Data data = new Data("role");
        data.primary= role;
        data.user = user;
        data.server = server;
        return data;
    }

    /**
     * tells checks if changing a channel's name was successful
     * @param oldName
     * @param newName
     * @param successful
     * @return
     */
    public static @NotNull Data checkChangeUsername(String oldName, String newName, Boolean successful){
        Data data = new Data("checkChangeUsername");
        data.user = oldName;
        data.secondary= newName;
        data.primary = successful;
        return data;
    }

    /**
     * tells if changing server name was successful
     * @param user
     * @param oldName
     * @param newName
     * @param successful
     * @return
     */
    public static @NotNull Data checkChangeServerName(String user, String oldName, String newName, Boolean successful){
        Data data = new Data("checkChangeServerName");
        data.user = user;
        data.server=oldName;
        data.secondary=newName;
        data.primary = successful;
        return data;
    }
    /**
     * tells if creating new server was successful
     */
    public static @NotNull Data checkNewServer(String user, String server, Boolean successful){
        Data data = new Data("checkNewServer");
        data.user = user;
        data.server = server;
        data.primary = successful;
        return data;
    }

    /**
     * gets people user has chat with
     * @param user
     * @param chats
     * @return
     */
    public static @NotNull Data directChats(String user, ArrayList<String> chats){
        Data data = new Data("directChats");
        data.primary = chats;
        data.user = user;
        return  data;
    }
}

