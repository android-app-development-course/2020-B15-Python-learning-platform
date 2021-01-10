package com.example.learing_platform.objects;

import java.io.Serializable;

public class Knowledge implements Serializable {

    private static final long serialVersionUID = 1L;
    int id;
    private String title;
    private String content;
    private String introduction;


    public Knowledge()
    {
    }
    public Knowledge(int id,String title, String content,String introduction) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.introduction = introduction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getIntroduction() {
        return introduction;
    }

    public int getId() {
        return id;
    }

}
