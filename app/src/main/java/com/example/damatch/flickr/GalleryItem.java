package com.example.damatch.flickr;

// Model Object Class GalleryItem
// Taken from Android Programming The Big Nerd Ranch Guide (3rd ed) Chapter 25 Pg. 486

public class GalleryItem {
    private String Caption;
    private String Id;
    private String Url;

    public String getCaption() {
        return Caption;
    }

    public void setCaption(String caption) {
        Caption = caption;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    @Override
    public String toString() {
        return Caption;
    }
}
