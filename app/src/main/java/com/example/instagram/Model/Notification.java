package com.example.instagram.Model;

public class Notification {
    private String userid;
    private String text;
    private String postid;
    private Boolean isPost;

    public Notification(String userid, String text, String postid, Boolean isPost) {
        this.userid = userid;
        this.text = text;
        this.postid = postid;
        this.isPost = isPost;
    }

    public Notification() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public Boolean getIsPost() {
        return isPost;
    }

    public void setIsPost(Boolean post) {
        isPost = post;
    }
}
