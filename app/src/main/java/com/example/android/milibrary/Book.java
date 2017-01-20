package com.example.android.milibrary;

/**
 * Created by Zack on 2017/01/18.
 */
public class Book {

    String title, author, desc, image;

    public Book() {
    }

    public Book(String author, String desc, String image, String title) {
        this.author = author;
        this.desc = desc;
        this.image = image;
        this.title = title;
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
}
