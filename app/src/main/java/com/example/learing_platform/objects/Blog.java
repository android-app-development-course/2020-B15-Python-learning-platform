package com.example.learing_platform.objects;

public class Blog {
    int id;
    private String title;
    private String content;
    private String author;
    public String authorPortraitUrl;
    public String coverUrl;
    private int repliesNum;
    private String post_time;
    private String introduction;
    private String author_id;
    public Blog(){}

    public String getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(String author_id) {
        this.author_id = author_id;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Blog(int id, String title, String content, String author, String authorPortraitUrl, String coverUrl, int repliesNum, String post_time, String introduction, String author_id) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.authorPortraitUrl = authorPortraitUrl;
        this.coverUrl = coverUrl;
        this.repliesNum = repliesNum;
        this.post_time = post_time;
        this.introduction = introduction;
        this.author_id = author_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorPortraitUrl() {
        return authorPortraitUrl;
    }

    public void setAuthorPortraitUrl(String authorPortraitUrl) {
        this.authorPortraitUrl = authorPortraitUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getRepliesNum() {
        return repliesNum;
    }

    public void setRepliesNum(int repliesNum) {
        this.repliesNum = repliesNum;
    }

    public String getPost_time() {
        return post_time;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
    }
}
