package com.vfxf.fvxmob;

public class Category {
    public String profilepc, uid, username, property;
    public Category() {

    }

    public Category(String profilepc, String uid, String username, String property) {
        this.profilepc = profilepc;
        this.uid = uid;
        this.username = username;
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getProfilepc() {
        return profilepc;
    }

    public void setProfilepc(String profilepc) {
        this.profilepc = profilepc;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
