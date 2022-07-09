package com.example.server; /**
 *this class gets a command and implements changes in database
 * and then sends the result back to client using Data object
 */
import com.example.mutual.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.nio.file.Files.copy;
import static java.nio.file.Files.readAllBytes;

public class CmdManager {

    private ServerSide serverSide;
    private static Statement stmt = null;
    private Connection con = null;
    private static String filespath = null;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm:ss");
    private DateTimeFormatter fileFormatter = DateTimeFormatter.ofPattern("yyyy-mm-dd hh-mm-ss");

    public CmdManager(Connection con, Statement stmt, String filepath,ServerSide serverSide) {
        this.serverSide = serverSide;
        this.stmt = stmt;
        this.con = con;
        this.filespath = filepath;
    }

    /**
     * calls appropriate method for each command based on their keyword
     * @param cmd
     * @return
     */
    public Data process(Command cmd) {
        Data dt = Data.fake();
        switch (cmd.getKeyword()) {
            case "newPvMsg":
                newPvMsg(cmd);
                break;
            case "changeProfilePhoto":
                changeProfilePhoto(cmd);
                break;
            case "newChannelMsg":
                newChannelMsg(cmd);
                break;
            case "newUser":
                dt = newUser(cmd);
                break;
            case "login":
                dt = login(cmd);
                break;
            case "newChannel":
                dt = newChannel(cmd);
                break;
            case "getPinnedMsgs":
                dt = getPinnedMsgs(cmd);
                break;
            case "newRelation":
                newRelation(cmd);
                break;
            case "newReaction":
                newReaction(cmd);
                break;
            case "getNewMsgs":
                dt = getNewMsgs(cmd);
                break;
            case "getRequests":
                dt = getRequests(cmd);
                break;
            case "getFriends":
                dt = friends(cmd);
                break;
            case "getBlockList":
                dt = getBlockList(cmd);
                break;
            case "getBlockedBy":
                dt = getBlockedBy(cmd);
                break;
            case "getReactions":
                dt = getReactions(cmd);
                break;
            case "getChannelMsgs":
                dt = getChannelMsgs(cmd);
                break;
            case "getPvMsgs":
                dt = getPvMsgs(cmd);
                break;
            case "getChannelMembers":
                dt = getChannelMembers(cmd);
                break;
            case "getServerMembers":
                dt = serverMembers(cmd);
                break;
            case "deleteServer":
                dt = deleteServer(cmd);
                break;
            case "deleteChannel":
                dt = deleteChannel(cmd);
                break;
            case "changeUsername":
                dt = changeUsername(cmd);
                break;
            case "changeServername":
                dt = changeServerName(cmd);
                break;
            case "newServer":
                dt = newServer(cmd);
                break;
            case "banFromChannel":
                banFromChannel(cmd);
                break;
            case "changeRole":
                changeRole(cmd);
                break;
            case "addOneMemberToChannel":
                addOneMemberToChannel(cmd);
                break;
            case "addPeopleToServer":
                addPeopleToServer(cmd);
                break;
            case "banFromServer":
                banFromServer(cmd);
                break;
            case "userServers":
                dt = userServers(cmd);
                break;
            case "userChannels":
                dt = userChannels(cmd);
                break;
            case "getUser":
                dt = getUser(cmd);
                break;
            case "pinMsg":
                pinMsg(cmd);
                break;
            case "getDirectChats":
                dt = getDirectChats(cmd);
                break;
            case "tellPv":
                break;
            case "tellChannel":
                break;
            case "getRole":
                dt = getRole(cmd);
                break;
            case "lastSeenAll":
                lastseenAll(cmd);
                break;
            case "lastseenPv":
                lastseenPv(cmd);
                dt = Data.exitChat(cmd.getUser());
                break;
            case "lastseenChannel":
                lastseenChannel(cmd);
                dt = Data.exitChat(cmd.getUser());
                break;
            case "download":
                dt = getFilePath(cmd);
                break;
        }

        return dt;
    }

    public Data getFilePath(Command cmd) {
        boolean isChannel = (boolean) cmd.getSecondary();
        String filePath = null;
        if (isChannel) {
            try {
                String query = "select FILELINK from channel_messages where server=? and channel=? and filename=?";
                PreparedStatement preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, cmd.getServer());
                preparedStatement.setString(2, cmd.getChannel());
                preparedStatement.setString(3, (String) cmd.getPrimary());
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    filePath = rs.getString("FILELINK");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                String query = "select FILELINK from pv_messages where ( (sender =? and receiver = ?) or (receiver=? and sender=?) ) and filename=?";
                PreparedStatement preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, cmd.getServer());
                preparedStatement.setString(2, cmd.getChannel());
                preparedStatement.setString(3, cmd.getServer());
                preparedStatement.setString(4, cmd.getChannel());
                preparedStatement.setString(5, (String) cmd.getPrimary());
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    filePath = rs.getString("FILELINK");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return Data.giveFilePath(filePath);
    }

    public ArrayList<Message> addReactionsToMessages(ArrayList<Message> messages) {
        for (Message message : messages) {
            if (message.getSourceInfo().size() == 3) {
                try {
                    String query = "select count(*) as C1 from reactions where messageSender=? and messageDate=? and type='like'";
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.setString(1, message.getSourceInfo().get(0));
                    preparedStatement.setTimestamp(2, Timestamp.valueOf(message.getDateTime()));
                    ResultSet rs = preparedStatement.executeQuery();
                    rs.next();
                    message.setLikes(rs.getInt("C1"));

                    query = "select count(*) as C1 from reactions where messageSender=? and messageDate=? and type='dislike'";
                    preparedStatement = con.prepareStatement(query);
                    preparedStatement.setString(1, message.getSourceInfo().get(0));
                    preparedStatement.setTimestamp(2, Timestamp.valueOf(message.getDateTime()));
                    rs = preparedStatement.executeQuery();
                    rs.next();
                    message.setDislikes(rs.getInt("C1"));
                    query = "select count(*) as C1 from reactions where messageSender=? and messageDate=? and type='laugh'";
                    preparedStatement = con.prepareStatement(query);
                    preparedStatement.setString(1, message.getSourceInfo().get(0));
                    preparedStatement.setTimestamp(2, Timestamp.valueOf(message.getDateTime()));
                    rs = preparedStatement.executeQuery();
                    rs.next();
                    message.setLaughs(rs.getInt("C1"));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return messages;
    }

    public void addPeopleToServer(Command cmd) {
        ArrayList<String> people = (ArrayList<String>) cmd.getPrimary();
        Iterator it = people.iterator();
        while (it.hasNext()) {
            String s = (String) it.next();
            try {
                ResultSet rs = stmt.executeQuery(String.format("select count(*) as C1 from server_members where username='%s' and server='%s'", s, cmd.getServer()));
                rs.next();
                if (rs.getInt("C1") > 0) {
                    it.remove();
                }
                rs = stmt.executeQuery(String.format("select count(*) as C1 from users where username='%s'", s));
                rs.next();
                if (rs.getInt("C1") <= 0) {
                    it.remove();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        for (String p : people) {
            try {
                stmt.executeUpdate(String.format("insert into server_members values ('%s','%s','member','000000000')", p, cmd.getServer()));
                ResultSet rs = stmt.executeQuery(String.format("select distinct channel as C from channel_members where server='%s'", cmd.getServer()));
                ArrayList<String> channels = new ArrayList<String>();
                while (rs.next()) {
                    channels.add(rs.getString("C"));
                }

                for (String ch : channels) {
                    String query = "insert into channel_members values (?,?,?,CURRENT_TIMESTAMP);";
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.setString(1, p);
                    preparedStatement.setString(2, cmd.getServer());
                    preparedStatement.setString(3, ch);
                    preparedStatement.executeUpdate();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void addOneMemberToChannel(Command cmd) {
        try {
            ResultSet rs = stmt.executeQuery(String.format("select count(*) as C1 from channel_members where username='%s' and server='%s' and channel='%s'", cmd.getPrimary(), cmd.getServer(), cmd.getChannel()));
            rs.next();
            if (rs.getInt("C1") > 0) {
                return;
            }
            rs = stmt.executeQuery(String.format("select count(*) as C1 from users where username='%s'", (String) cmd.getPrimary()));
            rs.next();
            if (rs.getInt("C1") <= 0) {
                return;
            }
            String query = "insert into channel_members values (?,?,?,CURRENT_TIMESTAMP)";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, (String) cmd.getPrimary());
            preparedStatement.setString(2, cmd.getServer());
            preparedStatement.setString(3, cmd.getChannel());
            preparedStatement.executeUpdate();

            rs = stmt.executeQuery(String.format("select count(*) as C1 from server_members where username='%s' and server='%s'", cmd.getPrimary(), cmd.getServer()));
            rs.next();
            if (rs.getInt("C1") > 0) {
                stmt.executeUpdate(String.format("delete from server_members where username='%s' and server='%s'", cmd.getPrimary(), cmd.getServer()));
                return;
            }
            stmt.executeUpdate(String.format("insert into server_members values ('%s','%s','member','000000000')", (String) cmd.getPrimary(), cmd.getServer()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Data getDirectChats(Command cmd) {
        HashSet<String> chats = new HashSet<>();
        ArrayList<String> chatsArray = new ArrayList<>();
        try {
            ResultSet rs = stmt.executeQuery(String.format("select distinct sender as S from pv_messages where receiver='%s'", cmd.getUser()));
            while (rs.next()) {
                chats.add(rs.getString("S"));
            }
            rs = stmt.executeQuery(String.format("select distinct receiver as S from pv_messages where sender='%s'", cmd.getUser()));
            while (rs.next()) {
                chats.add(rs.getString("S"));
            }

            for (String ch : chats) {
                chatsArray.add(ch);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Data.directChats(cmd.getUser(), chatsArray);
    }

    public Data login(Command cmd) {
        try {
            ResultSet rs = stmt.executeQuery(String.format("select count(*) as C1 from users where username='%s' and password='%s'", cmd.getUser(), (String) cmd.getPrimary()));
            rs.next();
            if (rs.getInt("C1") > 0) {
                return Data.checkLogin(cmd.getUser(), true);
            } else {
                return Data.checkLogin(cmd.getUser(), false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Data.checkLogin(cmd.getUser(), false);
        }
    }

    public void newPvMsg(Command cmd) {
        String sender = cmd.getUser();
        String receiver = (String) cmd.getSecondary();
        Message message1 = (Message) cmd.getPrimary();
        String date = message1.getDateTime().format(dateTimeFormatter);
        if (message1 instanceof TextMessage) {
            TextMessage message = (TextMessage) message1;
            String body = ((TextMessage) message).getText();
            try {
                String query = "insert into pv_messages(sender,receiver,date,body,isfile,seen) values (?,?,?,?,?,?);";
                PreparedStatement preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, sender);
                preparedStatement.setString(2, receiver);
                preparedStatement.setTimestamp(3, Timestamp.valueOf(message.getDateTime()));
                preparedStatement.setString(4, body);
                preparedStatement.setBoolean(5, false);
                preparedStatement.setBoolean(6, false);
                preparedStatement.executeUpdate();
                FeedBack.say(sender + "'s text message sent to " + receiver);
            } catch (SQLException e) {
                e.printStackTrace();
                FeedBack.say("sender+\"'s text message was not sent to \"+receiver");
            }
        }
        if (message1 instanceof FileMessage) {
            FileMessage message = (FileMessage) message1;
            String address = filespath + "\\" + message.getSourceInfo().get(0) + message.getDateTime().format(fileFormatter) + message.getFileName();
            try {
                String query = "insert into pv_messages(sender,receiver,date,isfile,seen,filename,filelink) values (?,?,?,true,false,?,?);";
                PreparedStatement preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, sender);
                preparedStatement.setString(2, receiver);
                preparedStatement.setTimestamp(3, Timestamp.valueOf(message.getDateTime()));
                preparedStatement.setString(4, message.getFileName());
                preparedStatement.setString(5, address);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public void changeProfilePhoto(Command cmd) {
        User user = (User) getUser(Command.getUser(cmd.getUser())).getPrimary();
        byte[] image = (byte[]) cmd.getPrimary();
        String format = (String) cmd.getSecondary();

        String address = filespath + "\\profilePhoto_" + user.getEmail() + "." + format;
        Path url = Paths.get(address);
        File file = new File(address);
        file.delete();

        try {
            bytesToFile(image, address);
        } catch (IOException e) {
            FeedBack.say("could not save profile photo of " + user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            stmt.executeUpdate(String.format("UPDATE users SET picturelink ='%s' WHERE username='%s')", url, user));
        } catch (SQLException e) {
            FeedBack.say("could not update picture url of " + user);
        }
    }

    public void newChannelMsg(Command cmd) {
        String sender = cmd.getUser();
        Message message1 = (Message) cmd.getPrimary();
        String server = message1.getSourceInfo().get(2);
        String channel = message1.getSourceInfo().get(1);
        LocalDateTime date = message1.getDateTime();
        if (message1 instanceof TextMessage) {
            TextMessage message = (TextMessage) message1;
            String body = message.getText();
            try {
                String query = "insert into channel_messages(sender,server,channel,date,body,ispinned,isfile) values (?,?,?,?,?,false,false);";
                PreparedStatement preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, sender);
                preparedStatement.setString(2, server);
                preparedStatement.setString(3, channel);
                preparedStatement.setTimestamp(4, Timestamp.valueOf(date));
                preparedStatement.setString(5, body);
                preparedStatement.executeUpdate();
                FeedBack.say(sender + "'s text message sent to " + server + "/" + channel);
            } catch (SQLException e) {
                FeedBack.say(sender + "'s text message was not sent to " + server + "/" + channel);
            }
        }
        if (message1 instanceof FileMessage) {
            FileMessage message = (FileMessage) message1;
            String address = filespath + "\\" + message.getSourceInfo().get(0) + message.getDateTime().format(fileFormatter) + message.getFileName();
            try {
                String query = "insert into channel_messages (sender,date,ispinned,isfile,filename,filelink,server,channel) values (?,?,false,true,?,?,?,?);";
                PreparedStatement preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, sender);
                preparedStatement.setTimestamp(2, Timestamp.valueOf(message.getDateTime()));
                preparedStatement.setString(3, message.getFileName());
                preparedStatement.setString(4, address);
                preparedStatement.setString(5, cmd.getServer());
                preparedStatement.setString(6, cmd.getChannel());
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public Data newUser(Command cmd) {
        User user = (User) cmd.getPrimary();
        byte[] image = user.getProfilePhoto();
        String format = (String) cmd.getSecondary();
        String address = new String("");
        if(image != null && image.length != 0) {
            address = filespath + "\\profilePhoto_" + user.getEmail() + "." + format;
        }

        try {
            ResultSet r = stmt.executeQuery(String.format("select count(*) as C1 from server_members where username='%s'", user.getUsername()));
            r.next();
            int count = r.getInt("C1");
            if (count > 0) {
                return Data.checkSignUp(((User) cmd.getPrimary()).getUsername(), false);
            }
        } catch (SQLException e) {
            FeedBack.say("a member with username: " + user.getUsername() + "already exists");
            return Data.checkSignUp(((User) cmd.getPrimary()).getUsername(), false);
        }
        try {
            if( image == null || image.length == 0){
                try {
                    image = readAllBytes(Paths.get("C:\\DiscordFiles\\default.png"));
                } catch (NoSuchFileException e) {
                    System.out.println("the file with path doesnt exists");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                address = filespath + "\\profilePhoto_" + user.getEmail() + ".png";

            }
            else
                bytesToFile(user.getProfilePhoto(), address);

        } catch (IOException e) {
            FeedBack.say("could not save profile photo of " + user.getUsername());
            return Data.checkSignUp(((User) cmd.getPrimary()).getUsername(), false);
        } catch (Exception e) {
            e.printStackTrace();
            return Data.checkSignUp(((User) cmd.getPrimary()).getUsername(), false);
        }

        try {
            stmt.executeUpdate(String.format("insert into users values ('%s','%s','%s','%S','%S','%S','%s');", user.getUsername(), user.getPassword(), user.getPhoneNum(), user.getEmail(), user.getStatus() == null ? null : user.getStatus().toString().toLowerCase(), user.getProfilePhotoFormat(), address));
        } catch (SQLException e) {
            FeedBack.say("could not add new User with username : " + user.getUsername());
            return Data.checkSignUp(((User) cmd.getPrimary()).getUsername(), false);
        }
        return Data.checkSignUp(((User) cmd.getPrimary()).getUsername(), true);
    }

    public Data newChannel(Command cmd) {
        try {
            ResultSet r = stmt.executeQuery(String.format("select count(*) as C1 from channel_members where channel='%s' and server='%s'", cmd.getChannel(), cmd.getServer()));
            r.next();
            int count = r.getInt("C1");
            if (count > 0) {
                return Data.checkNewChannel(cmd.getUser(), cmd.getServer(), cmd.getChannel(), false);

            } else {
                String query = "insert into channel_members values (?,?,?,CURRENT_TIMESTAMP);";
                PreparedStatement preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, cmd.getUser());
                preparedStatement.setString(2, cmd.getServer());
                preparedStatement.setString(3, cmd.getChannel());
                preparedStatement.executeUpdate();
                FeedBack.say("channel " + cmd.getChannel() + " created successfully");
                return Data.checkNewChannel(cmd.getUser(), cmd.getServer(), cmd.getChannel(), true);
            }
        } catch (SQLException e) {
            System.out.println("4");
            FeedBack.say("could not create channel " + cmd.getChannel());
            e.printStackTrace();
            return Data.checkNewChannel(cmd.getUser(), cmd.getServer(), cmd.getChannel(), false);
        }
    }

    public void newRelation(Command cmd) {

        Relationship rel = (Relationship) cmd.getPrimary();
        String sender = cmd.getUser();
        String receiver = (String) cmd.getSecondary();
        if (receiver.equals(sender)) {
            return;
        }
        String statement = new String();
        try {
            System.out.println("receiver: " + receiver + " sender: " + sender);
            ResultSet r = stmt.executeQuery("SELECT count(*) as C1 from users where username='" + receiver + "'");
            r.next();
            System.out.println((int) r.getInt("C1"));
            System.out.println("receiver: " + receiver + " sender: " + sender);
            if ((int) r.getInt("C1") <= 0) {
                throw new SQLException();
            }
        } catch (SQLException s) {
            FeedBack.say("such user doesnt exists");
            s.printStackTrace();
            return;
        }
        if (rel == Relationship.Block) {
            String s1 = String.format("delete from relationships where (receiver='%s' and sender='%s') or (sender='%s' and receiver='%s');", receiver, sender, receiver, sender);
            String s2 = String.format("insert into relationships values('%s','%s','%s');", sender, receiver, rel.toString());
            statement = s1 + "\n" + s2;
        } else if (rel == Relationship.Rejected) {
            String s1 = String.format("delete from relationships where (receiver='%s' and sender='%s') or (sender='%s' and receiver='%s');", receiver, sender, receiver, sender);
            statement = s1;
        } else if (rel == Relationship.Friend) {
            String s1 = String.format("delete from relationships where (receiver='%s' and sender='%s') or (sender='%s' and receiver='%s');", receiver, sender, receiver, sender);
            String s2 = String.format("insert into relationships values('%s','%s','%s');", sender, receiver, rel.toString());
            statement = s1 + "\n" + s2;
        } else if (rel == Relationship.Friend_pending) {
            try {
                ResultSet res = stmt.executeQuery(String.format("select count(*) as C1 from relationships where sender='%s' and receiver='%s' and status='Block';", receiver, sender));
                res.next();
                int count = res.getInt("C1");
                if (count > 0) {
                    throw new SQLException();
                }
                String s1 = String.format("delete from relationships where receiver='%s' and sender='%s';", receiver, sender);
                String s2 = String.format("insert into relationships values('%s','%s','%s');", sender, receiver, rel.toString());
                statement = s1 + "\n" + s2;
            } catch (SQLException e) {
                statement = null;
                e.printStackTrace();
                FeedBack.say("cannot send request bc they have blocked you");
            }
        }

        try {
            if (statement != null) {
                stmt.executeUpdate(statement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            FeedBack.say("could not add this relationship");
        }

    }

    public void newReaction(Command cmd) {
        Message message = (Message) cmd.getPrimary();
        LocalDateTime date = message.getDateTime();
        try {
            String query = "delete from reactions where reactionSender=? and messageSender=? and messageDate=?";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, cmd.getUser());
            preparedStatement.setString(2, message.getSourceInfo().get(0));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(date));
            preparedStatement.executeUpdate();

            String query1 = "insert into reactions values (?,?,?,?,?,?)";
            preparedStatement = con.prepareStatement(query1);
            preparedStatement.setString(1, cmd.getUser());
            preparedStatement.setString(2, message.getSourceInfo().get(2));
            preparedStatement.setString(3, message.getSourceInfo().get(1));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(date));
            preparedStatement.setString(5, message.getSourceInfo().get(0));
            preparedStatement.setString(6, (String) cmd.getSecondary());
            preparedStatement.executeUpdate();

        } catch (SQLException s) {
            s.printStackTrace();
        }
    }

    public Data getReactions(Command cmd) {
        HashMap<String, Integer> reactions = new HashMap<>();
        Message message = (Message) cmd.getPrimary();

        try {
            ResultSet rs = stmt.executeQuery(String.format("select count(*) as C1 from reactions where messageSender='%s' and messageDate='%s' and type='like'", message.getSourceInfo().get(0), message.getDateTime().format(dateTimeFormatter)));
            rs.next();
            reactions.put("like", rs.getInt("C1"));
            rs = stmt.executeQuery(String.format("select count(*) as C1 from reactions where messageSender='%s' and messageDate='%s' and type='dislike'", message.getSourceInfo().get(0), message.getDateTime().format(dateTimeFormatter)));
            rs.next();
            reactions.put("dislike", rs.getInt("C1"));
            rs = stmt.executeQuery(String.format("select count(*) as C1 from reactions where messageSender='%s' and messageDate='%s' and type='laugh'", message.getSourceInfo().get(0), message.getDateTime().format(dateTimeFormatter)));
            rs.next();
            reactions.put("laugh", rs.getInt("C1"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Data.reactions(cmd.getUser(), message, reactions);
    }

    public Data getNewMsgs(Command cmd) {
        ArrayList<Message> messages = new ArrayList<>();
        try {
            ResultSet rs = stmt.executeQuery(String.format("select * from pv_messages where seen=false and receiver='%s' order by date,sender", cmd.getUser()));
            while (rs.next()) {
                if (rs.getBoolean("isfile")) {

                    FileMessage m = new FileMessage(rs.getString("SENDER"), rs.getTimestamp("DATE").toLocalDateTime(), rs.getString("FILENAME"));
                    messages.add(m);
                } else {
                    TextMessage m = new TextMessage(rs.getString("SENDER"), rs.getString("BODY"), rs.getTimestamp("DATE").toLocalDateTime());
                    messages.add(m);
                }
            }
            HashMap<String, Timestamp> channelsDates = new HashMap<>();
            rs = stmt.executeQuery(String.format("select channel,lastseen from channel_members where username='%s'", cmd.getUser()));
            while (rs.next()) {
                channelsDates.put(rs.getString("CHANNEL"), rs.getTimestamp("LASTSEEN"));
            }
            for (HashMap.Entry<String, Timestamp> entry : channelsDates.entrySet()) {
                String key = entry.getKey();
                Timestamp value = entry.getValue();
                String query = "select * from channel_messages where channel=? and date > ? order by date";
                PreparedStatement preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, key);
                preparedStatement.setTimestamp(2, value);
                rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    if (rs.getBoolean("isfile")) {
                        byte[] bytes = fileToBytes(rs.getString("FILELINK"));
                        FileMessage m = new FileMessage(rs.getString("SENDER"), rs.getString("SERVER"), rs.getString("CHANNEL"), rs.getTimestamp("DATE").toLocalDateTime(), rs.getString("FILENAME"));
                        messages.add(m);
                    } else {
                        TextMessage m = new TextMessage(rs.getString("SENDER"), rs.getString("SERVER"), rs.getString("CHANNEL"), rs.getString("BODY"), rs.getTimestamp("DATE").toLocalDateTime());
                        messages.add(m);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        messages = addReactionsToMessages(messages);
        return Data.newMsgs(cmd.getUser(), messages);
    }

    public Data getChannelMsgs(Command cmd) {
        int number = (int) cmd.getPrimary();
        ArrayList<Message> messages = new ArrayList<>();
        try {
            String query = "select * from ( select * from channel_messages where channel=? and server=? order by date desc limit ? ) order by date";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, cmd.getChannel());
            preparedStatement.setString(2, cmd.getServer());
            preparedStatement.setInt(3, number);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                if (rs.getBoolean("isfile")) {
                    FileMessage m = new FileMessage(rs.getString("SENDER"), rs.getString("SERVER"), rs.getString("CHANNEL"), rs.getTimestamp("DATE").toLocalDateTime(), rs.getString("FILENAME"));
                    messages.add(m);
                } else {
                    TextMessage m = new TextMessage(rs.getString("SENDER"), rs.getString("SERVER"), rs.getString("CHANNEL"), rs.getString("BODY"), rs.getTimestamp("DATE").toLocalDateTime());
                    messages.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        messages = addReactionsToMessages(messages);
        return Data.channelMsgs(cmd.getUser(), cmd.getServer(), cmd.getChannel(), messages);
    }

    public Data getPvMsgs(Command cmd) {
        String friend = (String) cmd.getPrimary();
        int number = (int) cmd.getSecondary();
        ArrayList<Message> messages = new ArrayList<>();
        try {
            String query = "select * from(select * from pv_messages where (receiver=? and sender=?) or (sender=? and receiver=?) order by date desc limit ? )order by date ";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, cmd.getUser());
            preparedStatement.setString(2, friend);
            preparedStatement.setString(3, cmd.getUser());
            preparedStatement.setString(4, friend);
            preparedStatement.setInt(5, number);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                if (rs.getBoolean("isfile")) {
                    byte[] bytes = fileToBytes(rs.getString("FILELINK"));
                    FileMessage m = new FileMessage(rs.getString("SENDER"), rs.getTimestamp("DATE").toLocalDateTime(), rs.getString("FILENAME"));
                    messages.add(m);
                } else {
                    TextMessage m = new TextMessage(rs.getString("SENDER"), rs.getString("BODY"), rs.getTimestamp("DATE").toLocalDateTime());
                    messages.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("1");
        return Data.PvMsgs(cmd.getUser(), friend, messages);
    }

    public Data getRequests(Command cmd) {
        ResultSet rs = null;
        ArrayList<String> requesters = new ArrayList<>();
        try {
            rs = stmt.executeQuery(String.format("select sender as S from relationships where status='%s' and receiver='%s'", Relationship.Friend_pending.toString(), cmd.getUser()));

            while (rs.next()) {
                requesters.add(rs.getString("S"));
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
        return Data.allFriendRequests(cmd.getUser(), requesters);
    }

    public Data friends(Command cmd) {
        ResultSet rs = null;
        ArrayList<String> friends = new ArrayList<>();
        try {
            rs = stmt.executeQuery(String.format("select sender as S from relationships where status='%s' and receiver='%s'", Relationship.Friend.toString(), cmd.getUser()));

            while (rs.next()) {
                friends.add(rs.getString("S"));
            }

            rs = stmt.executeQuery(String.format("select receiver as S from relationships where status='%s' and sender='%s'", Relationship.Friend.toString(), cmd.getUser()));

            while (rs.next()) {
                friends.add(rs.getString("S"));
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
        Data dt = Data.friends(cmd.getUser(), friends);
        return dt;
    }

    public Data getBlockList(Command cmd) {
        ResultSet rs = null;
        ArrayList<String> blockeds = new ArrayList<>();
        try {
            rs = stmt.executeQuery(String.format("select receiver as S from relationships where status='%s' and sender='%s'", Relationship.Block.toString(), cmd.getUser()));

            while (rs.next()) {
                blockeds.add(rs.getString("S"));
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
        return Data.blockList(cmd.getUser(), blockeds);
    }

    public Data getBlockedBy(Command cmd) {
        ResultSet rs = null;
        ArrayList<String> blockedBys = new ArrayList<>();
        try {
            rs = stmt.executeQuery(String.format("select sender as S from relationships where status='%s' and receiver='%s'", Relationship.Block.toString(), cmd.getUser()));

            while (rs.next()) {
                blockedBys.add(rs.getString("S"));
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
        return Data.blockedBy(cmd.getUser(), blockedBys);
    }

    public Data getChannelMembers(Command cmd) {
        ResultSet rs = null;
        ArrayList<String> members = new ArrayList<>();
        try {
            rs = stmt.executeQuery(String.format("select username as S from channel_members where server='%s' and channel='%s'", cmd.getServer(), cmd.getChannel()));

            while (rs.next()) {
                members.add(rs.getString("S"));
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
        ArrayList<UserShort> guiMembers = stringToUserShort(members);
        return Data.channelMembers(cmd.getUser(), cmd.getServer(), cmd.getChannel(), guiMembers);
    }

    public Data serverMembers(Command cmd) {
        ResultSet rs = null;
        HashMap<String, Role> members = new HashMap<>();

        try {
            rs = stmt.executeQuery(String.format("select username as U,rolename as R,abilities as A from channel_members where server='%s'", cmd.getServer()));

            while (rs.next()) {
                members.put(rs.getString("U"), new Role(rs.getString("A"), rs.getString("R")));
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
        return Data.serverMembers(cmd.getUser(), cmd.getServer(), members);
    }

    public ArrayList<UserShort> stringToUserShort(ArrayList<String> people) {
        ArrayList<UserShort> userShorts = new ArrayList<>();
        Status status;
        for (String username : people) {
            try {
                ResultSet rs = stmt.executeQuery(String.format("select PICTURELINK,STATUS from users where username='%s'", username));
                rs.next();
                UserShort userShort;
                if (!rs.getString("STATUS").equals("NULL")) {
                    status = Status.valueOf(rs.getString("STATUS"));
                }
                else{
                    if (serverSide.getClientHandlers().containsKey(username)){
                        status = Status.online;
                    }
                    else{
                        status = Status.offline;
                    }
                }
                byte[] bytes = new byte[0];
                try {
                    bytes = readAllBytes(Paths.get(rs.getString("PICTURELINK")));
                } catch (NoSuchFileException e) {
                    System.out.println("the file with path doesnt exists");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                userShort = new UserShort(username,bytes,status);
                userShorts.add(userShort);
            } catch (SQLException s) {
            s.printStackTrace();
            }
        }
        return userShorts;
    }

    public Data deleteServer(Command cmd) {
        try {
            stmt.executeUpdate(String.format("Delete from server_members where server='%s'", cmd.getServer()));
            stmt.executeUpdate(String.format("Delete from channel_messages where server='%s'", cmd.getServer()));
            stmt.executeUpdate(String.format("Delete from channel_members where server='%s'", cmd.getServer()));
            stmt.executeUpdate(String.format("Delete from reactions where server='%s'", cmd.getServer()));
        } catch (SQLException s) {
            s.printStackTrace();
            return Data.checkDeleteServer(cmd.getServer(), false);
        }
        return Data.checkDeleteServer(cmd.getServer(), true);
    }

    public Data deleteChannel(Command cmd) {
        try {
            stmt.executeUpdate(String.format("Delete from channel_messages where server='%s' and channel='%s'", cmd.getServer(), cmd.getChannel()));
            stmt.executeUpdate(String.format("Delete from channel_members where server='%s' and channel='%s'", cmd.getServer(), cmd.getChannel()));
            stmt.executeUpdate(String.format("Delete from reactions where server='%s' and channel='%s'", cmd.getServer(), cmd.getChannel()));
        } catch (SQLException s) {
            s.printStackTrace();
            return Data.checkDeleteChannel(cmd.getServer(), cmd.getChannel(), false);
        }
        return Data.checkDeleteChannel(cmd.getServer(), cmd.getChannel(), true);
    }

    public Data changeUsername(Command cmd) {
        String old = cmd.getUser();
        String newName = (String) cmd.getPrimary();

        try {
            ResultSet r = stmt.executeQuery(String.format("select count(*) as C1 from users where username='%s'", newName));
            r.next();
            int count = r.getInt("C1");
            if (count > 0) {
                return Data.checkChangeUsername(old, newName, false);
            }
            stmt.executeUpdate(String.format("UPDATE relationships SET sender = '%s' WHERE sender='%s';", newName, old));
            stmt.executeUpdate(String.format("UPDATE relationships SET receiver = '%s' WHERE receiver='%s';", newName, old));
            stmt.executeUpdate(String.format("UPDATE channel_members SET username = '%s' WHERE username='%s';", newName, old));
            stmt.executeUpdate(String.format("UPDATE channel_messages SET sender = '%s' WHERE sender='%s';", newName, old));
            stmt.executeUpdate(String.format("UPDATE pv_messages SET receiver = '%s' WHERE receiver='%s';", newName, old));
            stmt.executeUpdate(String.format("UPDATE pv_messages SET sender = '%s' WHERE sender='%s';", newName, old));
            stmt.executeUpdate(String.format("UPDATE reactions SET reactionsender = '%s' WHERE reactionsender='%s';", newName, old));
            stmt.executeUpdate(String.format("UPDATE reactions SET messagesender = '%s' WHERE messagesender='%s';", newName, old));
            stmt.executeUpdate(String.format("UPDATE server_members SET username = '%s' WHERE username='%s';", newName, old));
            stmt.executeUpdate(String.format("UPDATE users SET username = '%s' WHERE username='%s';", newName, old));
            return Data.checkChangeUsername(old, newName, true);
        } catch (SQLException s) {
            return Data.checkChangeUsername(old, newName, false);
        }
    }

    public Data changeServerName(Command cmd) {
        String old = cmd.getServer();
        String newName = (String) cmd.getPrimary();

        try {
            ResultSet r = stmt.executeQuery(String.format("select count(*) as C1 from server_members where server='%s'", newName));
            r.next();
            int count = r.getInt("C1");
            if (count > 0) {
                return Data.checkChangeServerName(cmd.getUser(), old, newName, false);
            }

            stmt.executeUpdate(String.format("UPDATE channel_members SET server = '%s' WHERE server='%s';", newName, old));
            stmt.executeUpdate(String.format("UPDATE channel_messages SET server = '%s' WHERE server='%s';", newName, old));
            stmt.executeUpdate(String.format("UPDATE reactions SET server = '%s' WHERE server='%s';", newName, old));
            stmt.executeUpdate(String.format("UPDATE server_members SET server = '%s' WHERE server='%s';", newName, old));
            return Data.checkChangeServerName(cmd.getUser(), old, newName, true);
        } catch (SQLException s) {
            return Data.checkChangeServerName(cmd.getUser(), old, newName, false);
        }
    }

    public Data newServer(Command cmd) {

        try {
            ResultSet r = stmt.executeQuery(String.format("select count(*) as C1 from server_members where server='%s'", cmd.getServer()));
            r.next();
            int count = r.getInt("C1");
            if (count > 0) {
                return Data.checkNewServer(cmd.getUser(), cmd.getServer(), false);
            }
            stmt.executeUpdate(String.format("insert into server_members values('%s','%s','creator','111111111')", cmd.getUser(), cmd.getServer()));
        } catch (SQLException s) {
            return Data.checkNewServer(cmd.getUser(), cmd.getServer(), false);
        }
        newChannel(Command.newChannel(cmd.getUser(),cmd.getServer(),"general"));
        return Data.checkNewServer(cmd.getUser(), cmd.getServer(), true);
    }

    public Data getUser(Command cmd) {
        String username = cmd.getUser();
        User user = null;
        try {
            ResultSet rs = stmt.executeQuery(String.format("select * from users where username='%s'", username));
            while (rs.next()) {
                user = new User(username, rs.getString("PASSWORD"), rs.getString("EMAIL"));
                user.setPhoneNum(rs.getString("PHONE"));
                if ((!rs.getString("STATUS").equals("NULL"))) {
                    user.setStatus(rs.getString("STATUS").toLowerCase());
                }
                user.setProfilePhoto(fileToBytes(rs.getString("PICTURELINK")), rs.getString("PICTUREFORMAT"));

            }

        } catch (SQLException s) {
            s.printStackTrace();
        }
        return Data.userInfo(username, user);
    }

    public void pinMsg(Command cmd) {
        Message message = (Message) cmd.getPrimary();
        try {
            if (message.getSourceInfo().size() == 3) {
                String query = "update channel_messages set ispinned=true where date=? and sender=?";
                PreparedStatement preparedStatement = con.prepareStatement(query);
                preparedStatement.setTimestamp(1, Timestamp.valueOf(message.getDateTime()));
                preparedStatement.setString(2, message.getSourceInfo().get(0));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Data getPinnedMsgs(Command cmd) {
        ArrayList<Message> messages = new ArrayList<>();
        try {
            ResultSet rs = stmt.executeQuery(String.format("select * from channel_messages where channel='%s' and server='%s' and ispinned=true order by date", cmd.getChannel(), cmd.getServer()));
            while (rs.next()) {
                if (rs.getBoolean("isfile")) {
                    byte[] bytes = fileToBytes(rs.getString("FILELINK"));
                    FileMessage m = new FileMessage(rs.getString("SENDER"), rs.getString("SERVER"), rs.getString("CHANNEL"), rs.getTimestamp("DATE").toLocalDateTime(), rs.getString("FILENAME"));
                    messages.add(m);
                } else {
                    TextMessage m = new TextMessage(rs.getString("SENDER"), rs.getString("SERVER"), rs.getString("CHANNEL"), rs.getString("BODY"), rs.getTimestamp("DATE").toLocalDateTime());
                    messages.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        messages = addReactionsToMessages(messages);
        return Data.pinnedMsgs(cmd.getUser(), cmd.getServer(), cmd.getChannel(), messages);
    }

    public void banFromChannel(Command cmd) {
        try {
            stmt.executeUpdate(String.format("delete from channel_members where channel='%s' and server='%s' and username='%s'", cmd.getChannel(), cmd.getServer(), cmd.getUser()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void banFromServer(Command cmd) {
        try {
            stmt.executeUpdate(String.format("delete from server_members where server='%s' and username='%s'", cmd.getServer(), cmd.getUser()));
            stmt.executeUpdate(String.format("delete from channel_members where server='%s' and username='%s'", cmd.getServer(), cmd.getUser()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Data getRole(Command cmd) {
        Data dt = Data.role(cmd.getUser(), cmd.getServer(), null);
        try {
            String query = " select * from server_members where username=? and server=? ";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, cmd.getUser());
            preparedStatement.setString(2, cmd.getServer());
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                dt = Data.role(cmd.getUser(), cmd.getServer(), new Role(rs.getString("ABILITIES"), rs.getString("ROLENAME")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dt;
    }

    public void lastseenAll(Command cmd) {
        try {
            String query = "update channel_members set lastseen=CURRENT_TIMESTAMP where username=?";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, cmd.getUser());
            preparedStatement.executeUpdate();

            query = "update pv_messages set seen=true where receiver=? and date<CURRENT_TIMESTAMP;";
            preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, cmd.getUser());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void lastseenPv(Command cmd) {
        try {
            String query = "update pv_messages set seen=true where receiver=? and sender=? and DATE<CURRENT_TIMESTAMP;";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, cmd.getUser());
            preparedStatement.setString(2, (String) cmd.getPrimary());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void lastseenChannel(Command cmd) {
        try {
            String query = "update channel_members set lastseen = CURRENT_TIMESTAMP where username=? and channel=?";
            PreparedStatement preparedStatement = con.prepareStatement(query);
            preparedStatement.setString(1, cmd.getUser());
            preparedStatement.setString(2, cmd.getChannel());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Data userServers(Command cmd) {
        ResultSet rs = null;
        ArrayList<String> servers = new ArrayList<>();

        try {
            rs = stmt.executeQuery(String.format("select server as U from server_members where username='%s'", cmd.getUser()));

            while (rs.next()) {
                servers.add(rs.getString("U"));
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
        return Data.userServers(cmd.getUser(), servers);
    }

    public Data userChannels(Command cmd) {
        ResultSet rs = null;
        ArrayList<String> channels = new ArrayList<>();

        try {
            rs = stmt.executeQuery(String.format("select channel as U from channel_members where username='%s' and server='%s'", cmd.getUser(), cmd.getServer()));

            while (rs.next()) {
                channels.add(rs.getString("U"));
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }
        return Data.userChannels(cmd.getUser(), cmd.getServer(), channels);
    }

    public void changeRole(Command cmd) {
        Role role = (Role) cmd.getPrimary();
        try {
            stmt.executeUpdate(String.format("update server_members set rolename='%s' , abilities='%s' where username='%s' and server='%s'; ", role.getRoleName(), role.getValues(), (String) cmd.getSecondary(), cmd.getServer()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public byte[] fileToBytes(String path) {
        // file to byte[], Path
        byte[] bytes = null;
        try {
            bytes = readAllBytes(Paths.get(path));
        } catch (NoSuchFileException e) {
            System.out.println("the file with path " + path + " doesnt exists");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static void bytesToFile(byte[] dataForWriting, String path) throws Exception {
        File outputFile = new File(path);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(dataForWriting);

    }

}

