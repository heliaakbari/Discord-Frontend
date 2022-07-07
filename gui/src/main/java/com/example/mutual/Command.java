package com.example.mutual;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * this class holds a command's information which will be sent to server to do some operations
 * possible keywords : newMsg,newUser, newServer, newChannel, newRequest, newReaction,
 * getNewMsgs, getRequests, getChannelMsg, getPvMsgs, getChannelMembers, getServerMembers
 * getFriends, deleteServer , deleteChannel, changeUsername,changeServerName
 */
public class Command implements Serializable {

    private static final long serialVersionUID = 265786288349585L;

    private String keyword;
    private String server;
    private String channel;
    private String user;
    private Serializable primary;
    private Serializable secondary;

    public String getKeyword() {
        return keyword;
    }

    public String getChannel() {
        return channel;
    }

    public String getUser() {
        return user;
    }

    public String getServer() {
        return server;
    }

    public Object getPrimary() {
        return primary;
    }

    public Object getSecondary() {
        return secondary;
    }

    private Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * command constructor to send new private message from server
     *
     * @param sender   message's sender's username
     * @param receiver message's receiver's username
     * @param message  the new message to be sent to server
     * @return the new command object
     */
    public static Command newPvMsg(String sender, String receiver, Message message) {
        Command cmd = new Command("newPvMsg");
        cmd.user = sender;
        cmd.primary = message;
        cmd.secondary = receiver;
        return cmd;
    }

    /**
     * a command constructor to change the profile photo
     *
     * @param user   username of the person whose profile photo will be changed
     * @param image  new profile photo
     * @param format photo's format
     * @return the new command object
     */
    public static Command changeProfilePhoto(String user, byte[] image, String format) {
        Command cmd = new Command("changeProfilePhoto");
        cmd.user = user;
        cmd.primary = image;
        cmd.secondary = format;
        return cmd;
    }


    /**
     * a command constructor to send a new channel message to server
     *
     * @param sender  username of the sender
     * @param server  name of the server in which the message is sent
     * @param channel name of the channel in which the message is sent
     * @param message the new message to be sent
     * @return the new command object
     */
    public static Command newChannelMsg(String sender, String server, String channel, Message message) {
        Command cmd = new Command("newChannelMsg");
        cmd.user = sender;
        cmd.server = server;
        cmd.channel = channel;
        cmd.primary = message;
        return cmd;
    }

    /**
     * a command constructor to add a bew user's information to database
     *
     * @param user new user to be added
     * @return the new command object
     */
    public static Command newUser(User user) {
        Command cmd = new Command("newUser");
        cmd.primary = user;
        return cmd;
    }

    /**
     * a command constructor to log in a user and check the given username and password to be true
     *
     * @param username username to be checked
     * @param password password to be checked
     * @return the new command object
     */
    public static Command login(String username, String password) {
        Command cmd = new Command("login");
        cmd.user = username;
        cmd.primary = password;
        return cmd;
    }

    /**
     * a command constructor to add a new channel to database
     *
     * @param creator username of the creator of the channel
     * @param server  name of the server which the channel is made in
     * @param channel name of the new channel
     * @return the new command object
     */
    public static Command newChannel(String creator, String server, String channel) {
        Command cmd = new Command("newChannel");
        cmd.user = creator;
        cmd.server = server;
        cmd.channel = channel;
        return cmd;
    }

    /**
     * a command constructor to fetch pinned messages inside a channel from server
     *
     * @param user    username of the user who asks for pinned messages
     * @param server  name of the server
     * @param channel name of the channel in which we want the pinned messages
     * @return the new command object
     */
    public static Command getPinnedMsgs(String user, String server, String channel) {
        Command cmd = new Command("getPinnedMsgs");
        cmd.user = user;
        cmd.channel = channel;
        cmd.server = server;
        return cmd;
    }

    /**
     * a command constructor to add a new relationship status to the database
     *
     * @param relation new relationship enum
     * @param sender   first person (or the one who blocks or sends friend request)
     * @param receiver second person (or the one who is blocked or receive friend request)
     * @return the new command object
     */
    public static Command newRelation(Relationship relation, String sender, String receiver) {
        Command cmd = new Command("newRelation");
        cmd.primary = relation;
        cmd.user = sender;
        cmd.secondary = receiver;
        return cmd;
    }

    /**
     * a command constructor to tell server user wants to download a file
     *
     * @param p1OrServer  first person's username in private chats or server name in group chats
     * @param p2OrChannel second person's username in private chats or channel name in group chats
     * @param filename    file name along with its format
     * @param isChannel   a boolean to determine whether the file is sent in a channel or private chat
     * @return the new command object
     */
    public static Command download(String p1OrServer, String p2OrChannel, String filename, Boolean isChannel) {
        Command cmd = new Command("download");
        cmd.server = p1OrServer;
        cmd.channel = p2OrChannel;
        cmd.primary = filename;
        cmd.secondary = isChannel;
        return cmd;
    }

    /**
     * a command constructor to tell server user wants to upload a file
     *
     * @param receiver  username of the receiver of the message in private chats, must be null if isChannel is true
     * @param server    server name in channel chats, must be null if isChannel is false
     * @param channel   channel name in channel chats, must be null if isChannel is false
     * @param isChannel a boolean to determine whether the file is sent in a channel or privete chat
     * @return the new command object
     */
    public static Command upload(String receiver, String server, String channel, boolean isChannel) {
        Command cmd = new Command("upload");
        cmd.user = receiver;
        cmd.server = server;
        cmd.channel = channel;
        cmd.primary = isChannel;
        return cmd;
    }

    /**
     * a command constructor to add anew reaction to a message in database
     *
     * @param user    username of the sender of the reaction
     * @param message the message which the reaction is to
     * @param type    type of the reaction which can be : laugh, like or dislike
     * @return the new command object
     */
    public static Command newReaction(String user, Message message, String type) {
        Command cmd = new Command("newReaction");
        cmd.user = user;
        cmd.primary = message;
        cmd.secondary = type;
        return cmd;
    }

    /**
     * a command constructor to fetch all new messages in discord when the user enters application
     *
     * @param user username of the newly logged-in user
     * @return the new command object
     */
    public static Command getNewMsgs(String user) {
        Command cmd = new Command("getNewMsgs");
        cmd.user = user;
        return cmd;
    }

    /**
     * a command constructor to fetch all new friend requests from database
     *
     * @param user username
     * @return the new command object
     */
    public static Command getRequests(String user) {
        Command cmd = new Command("getRequests");
        cmd.user = user;
        return cmd;
    }

    /**
     * a command constructor to fetch the friends list from database
     *
     * @param user username
     * @return the new command object
     */
    public static Command getFriends(String user) {
        Command cmd = new Command("getFriends");
        cmd.user = user;
        return cmd;
    }

    /**
     * a command constructor to fetch the blocked list from database
     *
     * @param user username
     * @return the new command object
     */
    public static Command getBlockList(String user) {
        Command cmd = new Command("getBlockList");
        cmd.user = user;
        return cmd;
    }

    /**
     * a command constructor to get th list of the people who blocked this user
     *
     * @param user username of the specific user
     * @return the new command object
     */
    public static Command getBlockedBy(String user) {
        Command cmd = new Command("getBlockedBy");
        cmd.user = user;
        return cmd;
    }

    /**
     * a command constructor to fetch all reaction for a message from database
     *
     * @param user    username
     * @param message the message
     * @return the new command object
     */
    public static Command getReactions(String user, Message message) {
        Command cmd = new Command("getReactions");
        cmd.user = user;
        cmd.primary = message;
        return cmd;
    }

    /**
     * a command constructor to fetch all new messages in a channel from database when the user enters a channel
     *
     * @param user             username of the specific user
     * @param server           server name
     * @param channel          channel name in which we want to get new messages
     * @param numberOfMessages maximum number of recent messages to be fetched
     * @return the new command object
     */
    public static Command getChannelMsgs(String user, String server, String channel, Integer numberOfMessages) {
        Command cmd = new Command("getChannelMsgs");
        cmd.user = user;
        cmd.channel = channel;
        cmd.server = server;
        cmd.primary = numberOfMessages;
        return cmd;
    }

    /**
     * a command constructor to fetch recent messgaes in a private chat from database
     *
     * @param user             username
     * @param theFriend        other person's username in the chat
     * @param numberOfMessages maximum number of recent messages to be fetched from server
     * @return the new command objcet
     */
    public static Command getPvMsgs(String user, String theFriend, Integer numberOfMessages) {
        Command cmd = new Command("getPvMsgs");
        cmd.user = user;
        cmd.primary = theFriend;
        cmd.secondary = numberOfMessages;
        return cmd;
    }

    /**
     * a command constructor to fetch the list of members inside a channel
     *
     * @param user    username
     * @param server  server name
     * @param channel name of the channel which we want a list of members of
     * @return the new command object
     */
    public static Command getChannelMembers(String user, String server, String channel) {
        Command cmd = new Command("getChannelMembers");
        cmd.user = user;
        cmd.server = server;
        cmd.channel = channel;
        return cmd;
    }

    /**
     * a command constructor to fetch the list of members inside a server
     *
     * @param user   username
     * @param server server name
     * @return the new command object
     */
    public static Command getServerMembers(String user, String server) {
        Command cmd = new Command("getServerMembers");
        cmd.user = user;
        cmd.server = server;
        return cmd;
    }

    /**
     * a command constructor to remove a server from database
     *
     * @param user   username of the remover
     * @param server name of the server to be removed
     * @return the new command objcet
     */
    public static Command deleteServer(String user, String server) {
        Command cmd = new Command("deleteServer");
        cmd.user = user;
        cmd.server = server;
        return cmd;
    }

    /**
     * a command constructor to remove a channel from database
     *
     * @param user    username of the remover
     * @param server  name if the server where the channel is
     * @param channel the of the channel to be removed
     * @return the new command object
     */
    public static Command deleteChannel(String user, String server, String channel) {
        Command cmd = new Command("deleteChannel");
        cmd.user = user;
        cmd.channel = channel;
        cmd.server = server;
        return cmd;
    }

    /**
     * a command constructor to change a username in database
     *
     * @param oldName old username
     * @param newName new username
     * @return the new command object
     */
    public static Command changeUsername(String oldName, String newName) {
        Command cmd = new Command("changeUsername");
        cmd.user = oldName;
        cmd.primary = newName;
        return cmd;
    }

    /**
     * a command constructor to change a server name in database
     *
     * @param user    username
     * @param oldName old server name
     * @param newName new server name
     * @return the new command object
     */
    public static Command changeServerName(String user, String oldName, String newName) {
        Command cmd = new Command("changeServername");
        cmd.user = user;
        cmd.server = oldName;
        cmd.primary = newName;
        return cmd;
    }


    /**
     * a command constructor to add a new server to database
     *
     * @param creator    username of the server's creator
     * @param serverName
     * @return the new command object
     */
    public static Command newServer(String creator, String serverName) {
        Command cmd = new Command("newServer");
        cmd.user = creator;
        cmd.server = serverName;
        return cmd;
    }

    /**
     * a command constructor to remove a user from a channel in database
     *
     * @param personToBeBanned username of the person to be removed
     * @param server           server name
     * @param channel          channel name
     * @return the new command object
     */
    public static Command banFromChannel(String personToBeBanned, String server, String channel) {
        Command cmd = new Command("banFromChannel");
        cmd.user = personToBeBanned;
        cmd.server = server;
        cmd.channel = channel;
        return cmd;
    }

    /**
     * a command constructor to add a new role in a server in database
     *
     * @param user         username of the person who is adding the new role
     * @param userToChange username of the person whose role us being added to server
     * @param server       server name
     * @param role         new role
     * @return the new command object
     */
    public static Command changeRole(String user, String userToChange, String server, Role role) {
        Command cmd = new Command("changeRole");
        cmd.user = user;
        cmd.server = server;
        cmd.primary = role;
        cmd.secondary = userToChange;
        return cmd;
    }

    /**
     * a command constructor to add a new member to a channel in database
     *
     * @param user         username og the person who is adding the member
     * @param perseonToAdd username of the mmeber to be added
     * @param server       server name
     * @param channel      channel name
     * @return the new command object
     */
    public static Command addOneMemberToChannel(String user, String perseonToAdd, String server, String channel) {
        Command cmd = new Command("addOneMemberToChannel");
        cmd.user = user;
        cmd.primary = perseonToAdd;
        cmd.server = server;
        cmd.channel = channel;
        return cmd;
    }

    /**
     * a command constructor to tell server client socket is closed and user has closed the program
     *
     * @return the new command object
     */
    public static Command exit() {
        Command cmd = new Command("exit");
        return cmd;
    }

    /**
     * a command constructor to add new group of people to a server while creating it for the first time
     *
     * @param user        creator's username
     * @param server      server name
     * @param peopleToAdd array list of usernames to be added to server
     * @return the new command object
     */
    public static Command addPeopleToServer(String user, String server, ArrayList<String> peopleToAdd) {
        Command cmd = new Command("addPeopleToServer");
        cmd.user = user;
        cmd.primary = peopleToAdd;
        cmd.server = server;
        return cmd;
    }

    /**
     * a command constructor to remove a member from a server in database
     *
     * @param personToBeBanned username of the person to be removed
     * @param server           server name
     * @return the new commnd object
     */
    public static Command banFromServer(String personToBeBanned, String server) {
        Command cmd = new Command("banFromServer");
        cmd.user = personToBeBanned;
        cmd.server = server;
        return cmd;
    }

    /**
     * a command constructor to fetch the list of servers for the specific user
     *
     * @param user username of the person whose server list must be fetched from database
     * @return the new command object
     */
    public static Command userServers(String user) {
        Command cmd = new Command("userServers");
        cmd.user = user;
        return cmd;
    }

    /**
     * a command constructor to fetch the list of channels for the specific user
     *
     * @param user   username of the person whose channels list must be fetched from database
     * @param server server name
     * @return the new command object
     */
    public static Command userChannels(String user, String server) {
        Command cmd = new Command("userChannels");
        cmd.user = user;
        cmd.server = server;
        return cmd;
    }

    public static Command getUser(String user) {
        Command cmd = new Command("getUser");
        cmd.user = user;
        return cmd;
    }

    /**
     * a command constructor to pin a message in a server
     *
     * @param user    username of the person who pins the messge
     * @param message message to be pinned
     * @return the new command object
     */
    public static Command pinMsg(String user, Message message) {
        Command cmd = new Command("pinMsg");
        cmd.user = user;
        cmd.primary = message;
        return cmd;
    }


    /**
     * a command constructor to fetch the list of private chats from database
     *
     * @param user username
     * @return the new command object
     */
    public static Command getDirectChats(String user) {
        Command cmd = new Command("getDirectChats");
        cmd.user = user;
        return cmd;
    }

    /**
     * a command constructor to tell server user has entered a private chat
     *
     * @param user        username of the person who entered the chat
     * @param otherPerson the other person in the chat
     * @return the new command object
     */
    public static Command tellPv(String user, String otherPerson) {
        Command cmd = new Command("tellPv");
        cmd.user = user;
        cmd.primary = otherPerson;
        return cmd;
    }

    /**
     * a command constructor to tell server user has just entered a channel group chat
     *
     * @param user    username of the person who entered the chat
     * @param server  server name
     * @param channel channel name
     * @return the new command object
     */
    public static Command tellChannel(String user, String server, String channel) {
        Command cmd = new Command("tellChannel");
        cmd.channel = channel;
        cmd.server = server;
        cmd.user = user;
        return cmd;
    }

    /**
     * a command constructor to fetch the role of the specific user in this server from the database
     *
     * @param user   username of the person whose role must be fetched
     * @param server server name
     * @return the new command object
     */
    public static Command getRole(String user, String server) {
        Command cmd = new Command("getRole");
        cmd.user = user;
        cmd.server = server;
        return cmd;
    }


    /**
     * a command constructor to tell server user has seen all new notifications
     *
     * @param user username
     * @return the new commamd object
     */
    public static Command lastseenAll(String user) {
        Command cmd = new Command("lastSeenAll");
        cmd.user = user;
        return cmd;
    }

    /**
     * a command constructor user has just exit a private chat
     *
     * @param user           username of the person who exit
     * @param theOtherPerson username of the other person in chat
     * @return the new command object
     */
    public static Command lastseenPv(String user, String theOtherPerson) {
        Command cmd = new Command("lastseenPv");
        cmd.user = user;
        cmd.primary = theOtherPerson;
        return cmd;
    }

    /**
     * a command constructor to tell server user has just exit a channel chat
     *
     * @param user    username of the person who exit
     * @param server  server name
     * @param channel channel name
     * @return the new command object
     */
    public static Command lastseenChannel(String user, String server, String channel) {
        Command cmd = new Command("lastseenChannel");
        cmd.user = user;
        cmd.server = server;
        cmd.channel = channel;
        return cmd;
    }
}
