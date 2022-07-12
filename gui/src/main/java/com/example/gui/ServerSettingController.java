package com.example.gui;

import com.example.mutual.Role;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ServerSettingController {

    protected Role role;
    protected String currentUser;
    protected String currentServer;

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ObjectOutputStream fout;
    private ObjectInputStream fin;

    public ServerSettingController(Role role,String currentUser,String currentServer,ObjectOutputStream out,ObjectInputStream in,ObjectOutputStream fout,ObjectInputStream fin){
        this.in = in;
        this.out = out;
        this.role = role;
        this.currentServer = currentServer;
        this.currentUser = currentUser;
        this.fin = fin;
        this.fout = fout;
    }
}
