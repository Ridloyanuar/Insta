package org.ridlo.insta.data;


import java.util.Date;

public class Posts extends PostId {

    public String user_id,image_url,postingan,thumb;
    public Date timestamp;

    public Posts() {
    }

    public Posts(String user_id, String image_url, String postingan, String thumb, Date timestamp) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.postingan = postingan;
        this.thumb = thumb;
        this.timestamp = timestamp;
    }

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

    public String getPostingan() {
        return postingan;
    }

    public void setPostingan(String postingan) {
        this.postingan = postingan;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

