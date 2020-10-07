package com.example.damatch.flickr;

import java.util.ArrayList;
import java.util.List;

/*
* Flickr manager used to retrieve information about the added image ID's in an array
* and the directory path to the internal storage
* */

public class FlickrManager {
    private String pathToImageStorage;
    private List<Integer> imageIDArray = new ArrayList<>();

    public String getPathToImageStorage() {
        return pathToImageStorage;
    }

    public void setPathToImageStorage(String pathToImageStorage) {
        this.pathToImageStorage = pathToImageStorage;
    }

    public List<Integer> getImageIDArray() {
        return imageIDArray;
    }

    public int getImageID(int index) {
        return imageIDArray.get(index);
    }

    public int imageIDArraySize() {
        return imageIDArray.size();
    }

    public void addImageID(int imageID) {
        imageIDArray.add(imageID);
    }

    public void removeImageFromIDArray(int value) {
        imageIDArray.remove(value);
    }

    public int[] transferImageID() {
        int[] result = new int[imageIDArray.size()];
        for(int i = 0; i < imageIDArray.size(); i++) {
            result[i] = getImageID(i);
        }
        return result;
    }

    public boolean doesDuplicateIDExist(int givenID) {
        if(imageIDArray.contains(givenID)) {
            return true;
        }
        return false;
    }

    private static FlickrManager instance;
    private FlickrManager() {
    }

    public static FlickrManager getInstance() {
        if (instance == null) {
            instance = new FlickrManager();
        }
        return instance;
    }

}
