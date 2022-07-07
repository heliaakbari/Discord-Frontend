package com.example.server; /**
 * makes connection to database
 * if wanted, will build a new database and truncate the previous one
 */
import com.example.mutual.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.readAllBytes;

public class DatabaseManager {
    private Connection con = null;
    public Scanner get = new Scanner(System.in);
    private String dbUrl = "jdbc:hsqldb:hsql://localhost/testdb";
    private Statement stmt = null;
    private String filespath = "C:\\DiscordFiles";
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public CmdManager cmdManager = null;

    public void start() {
        try {

            System.out.println("1.existing database    2.new database");
            connect();
            if (get.nextLine().equals("1")) {
                ResultSet result = stmt.executeQuery("SELECT COUNT(*) AS tablecount\n" +
                        "FROM   INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC';");
                while (result.next()) {
                    if (result.getInt("tablecount") == 0) {
                        newDatabase();
                    }
                }

            } else {
                newDatabase();
            }
            cmdManager = new CmdManager(con, stmt, filespath);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void connect() throws SQLException {
        //Registering the HSQLDB JDBC driver
        try {
            Class.forName("org.hsqldb.jdbc.JDBCDriver");
        } catch (ClassNotFoundException classNotFoundException) {
            classNotFoundException.printStackTrace();
        }
        //Creating the connection with HSQLDB
        con = DriverManager.getConnection(dbUrl, "SA", "");
        if (con != null) {
            System.out.println("Connection created successfully");
            stmt = con.createStatement();
            ResultSet result = stmt.executeQuery("SELECT COUNT(*) AS tablecount\n" +
                    "FROM   INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='PUBLIC';");
            while (result.next()) {
                System.out.println(result.getInt("tablecount"));
            }
        } else {
            System.out.println("Problem with creating connection");
        }
    }

    public void newDatabase() throws SQLException {
        //clears all databases rows
        stmt.executeUpdate("truncate schema PUBLIC and commit");
        stmt.executeUpdate("""
                drop table IF EXISTS channel_members;
                drop table IF EXISTS relationships;
                drop table IF EXISTS users;
                drop table IF EXISTS pv_messages;
                drop table IF EXISTS channel_messages;
                drop table IF EXISTS server_members;
                drop table IF EXISTS reactions;
                """);
        //delete images and files in pc too

        //add table member of channels
        stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS channel_members
                (username VARCHAR(20) not null,
                 server varchar(20) not null,
                 channel varchar(20) not null,
                 lastseen TIMESTAMP not null);""");
        stmt.executeUpdate("""
                    ALTER TABLE channel_members
                    ADD CONSTRAINT  IF NOT EXISTS uniquePersonInchannel UNIQUE(username,server,channel);
                """);
        //add relationships
        stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS relationships
                (sender VARCHAR(20) not null,
                 receiver varchar(20) not null,
                 status varchar(20));""");
        stmt.executeUpdate("""
                  ALTER TABLE relationships
                ADD CONSTRAINT  IF NOT EXISTS  uniqueRelationship UNIQUE(sender,receiver);
                        """);
        //users in general
        stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users
                (username VARCHAR(20) unique not null,
                 password varchar(20) not null,
                 phone varchar(15),
                 email varchar(50) unique not null,
                 status varchar(20),
                 pictureFormat varchar(10),
                 picturelink varchar(200));""");

        //channel messages
        stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS channel_messages
                (sender VARCHAR(20) not null,
                 server varchar(20) not null,
                 channel varchar(20) not null,
                 date TIMESTAMP not null,
                 body varchar(1000),
                 isPinned boolean not null,
                 isFile boolean not null,
                 filename varchar(200),
                 filelink varchar(1000));""");

        //pv messages
        stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS pv_messages
                (sender VARCHAR(20) not null,
                 receiver varchar(20) not null,
                 date TIMESTAMP not null,
                 body varchar(1000),
                 seen boolean not null,
                 isFile boolean not null,
                 filename varchar(200),
                 filelink varchar(1000));""");
        //roles
        stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS server_members
                (username VARCHAR(20) not null,
                 server varchar(20) not null,
                 roleName varchar(20),
                 abilities varchar(9) not null);""");
        stmt.executeUpdate("""
                    ALTER TABLE server_members
                    ADD CONSTRAINT IF NOT EXISTS uniqueRole UNIQUE(username,server);
                """);
        //reactions
        stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS reactions
                (reactionSender VARCHAR(20) not null,
                 server varchar(20) not null,
                 channel varchar(20) not null,
                 messageDate TIMESTAMP not null,
                 messageSender varchar(20) not null,
                 type varchar(10) not null);""");
        stmt.executeUpdate("""
                    ALTER TABLE reactions
                    ADD CONSTRAINT  IF NOT EXISTS uniqueReaction UNIQUE(reactionSender,messageSender,messageDate);
                """);
    }
}


