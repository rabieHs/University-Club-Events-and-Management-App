package com.vfxf.fvxmob;

public class POSTS {
    public String postdescription, postimage, posttime, username, userprofileimage;


    public POSTS(){

    }


    public POSTS(String postdescription, String postimage, String posttime, String username, String userprofileimage) {
        this.postdescription = postdescription;
        this.postimage = postimage;
        this.posttime = posttime;
        this.username = username;
        this.userprofileimage = userprofileimage;
    }

    public String getPostdescription() {
        return postdescription;
    }

    public void setPostdescription(String postdescription) {
        this.postdescription = postdescription;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getPosttime() {
        return posttime;
    }

    public void setPosttime(String posttime) {
        this.posttime = posttime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserprofileimage() {
        return userprofileimage;
    }

    public void setUserprofileimage(String userprofileimage) {
        this.userprofileimage = userprofileimage;
    }


}
