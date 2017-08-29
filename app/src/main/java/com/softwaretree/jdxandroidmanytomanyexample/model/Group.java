package com.softwaretree.jdxandroidmanytomanyexample.model;

import java.util.ArrayList;

public class Group {
    public int gId;
    public String gName;
    public ArrayList<User> users; // A collection of User objects

    public Group() {
    }

    public Group(int gId, String gName) {
        this.gId = gId;
        this.gName = new String(gName);
        this.users = null;
    }

    public int getgId() {
        return gId;
    }

    public void setgId(int gId) {
        this.gId = gId;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }  
}

