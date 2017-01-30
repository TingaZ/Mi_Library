package com.example.android.milibrary;

/**
 * Created by Zack on 2017/01/18.
 */
public class Book {

    private String title, author, desc, image, username;

    public Book() {
    }

    public Book(String author, String desc, String image, String title, String username) {
        this.author = author;
        this.desc = desc;
        this.image = image;
        this.title = title;
        this.username = username;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
