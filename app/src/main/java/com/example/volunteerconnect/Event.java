package com.example.volunteerconnect;

public class Event {

    private String id;
    private String title;
    private String description;
    private String date;
    private String category;
    private String imageUrl;

    public Event() {
    }

    public Event(String id, String title, String description, String date, String category, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
