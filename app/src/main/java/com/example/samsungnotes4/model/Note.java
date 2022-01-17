package com.example.samsungnotes4.model;

public class Note {

    //zmienne musza miec nazwe taka sama jak w FS
    private String title;
    private String content;

    public Note() {

    }

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    //getter / setter

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
