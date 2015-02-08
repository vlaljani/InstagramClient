package com.codepath.instagramclient;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by vibhalaljani on 2/4/15.
 */
public class InstagramPhoto {
    private String type;
    private String username;
    private String caption;
    private String imgUrl;
    private String profilePicUrl;
    private long createdAt;
    private JSONArray tags;

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public ArrayList<String> getRecentComments() {
        return recentComments;
    }

    public void setRecentComments(ArrayList<String> recentComments) {
        this.recentComments = recentComments;
    }

    private String location;
    private int numComments;
    private ArrayList<String> recentComments;
    private ArrayList<String> recentCommenters;

    public ArrayList<String> getRecentCommenters() {
        return recentCommenters;
    }

    public void setRecentCommenters(ArrayList<String> recentCommenters) {
        this.recentCommenters = recentCommenters;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public JSONArray getTags() {
        return tags;
    }

    public void setTags(JSONArray tags) {
        this.tags = tags;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public int imgHeight;
    public int imgWidth;
    public int likesCount;


    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getImgHeight() {
        return imgHeight;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }

    public int getImgWidth() {
        return imgWidth;
    }

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
}
