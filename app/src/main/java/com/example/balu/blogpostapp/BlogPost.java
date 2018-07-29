package com.example.balu.blogpostapp;

import java.util.Date;

;

public class BlogPost extends BlogPostID{

    public String user_id;
    public String image_url;


    public void settime_stamp(Date time_stamp) {
        this.time_stamp = time_stamp;
    }

    public Date time_stamp;




    public BlogPost() {}

    public BlogPost(String user_id, String image_url, String desc, String thumb,Date time_stamp) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.desc = desc;
        this.thumb = thumb;
        this.time_stamp = time_stamp;

    }

    public String desc;
    public String thumb;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
    public Date getTimestamp() {
        return time_stamp;
    }




}
