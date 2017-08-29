package com.softwaretree.jdxandroidmanytomanyexample.model;

import java.util.Vector;

public class User {
    public int uId;
    public String uName;
    public Vector<Group> groups; // A collection of Group objects

    public User() {
    }

    public User(int uId, String uName) {
        this.uId = uId;
        this.uName = new String(uName);
        this.groups = null;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public Vector<Group> getGroups() {
        return groups;
    }

    public void setGroups(Vector<Group> groups) {
        this.groups = groups;
    }   
}
