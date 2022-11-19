package com.vfxf.fvxmob;

public class COMMENTS {
    public String comment, datetime, profilepic, username;

    public COMMENTS() {

    }

    public COMMENTS(String comment, String datetime, String profilepic, String username) {
        this.comment = comment;
        this.datetime = datetime;
        this.profilepic = profilepic;
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}




