package com.example.instagram.Model;

public class Story {
    private String imageurl;
    private long timeStart;
    private long timeEnd;
    private String storyid;
    private String userid;

    public Story(String imageurl, long timeStart, long timeEnd, String storyid, String userid) {
        this.imageurl = imageurl;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.storyid = storyid;
        this.userid = userid;
    }

    public Story() {
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
