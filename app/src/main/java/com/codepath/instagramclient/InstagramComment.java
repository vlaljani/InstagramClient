package com.codepath.instagramclient;

/**
 * Created by vibhalaljani on 2/8/15.
 *
 * This is the model class for a single instagram comment. This is used with the item_lv_comment.xml
 * layout to display the comments in a listview in the comments
 * dialog fragment (fragment_all_comments.xml)
 */
public class InstagramComment {
    private String username;
    private String text;
    private String profilePicUrl;
    private long created_time;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public long getCreated_time() {
        return created_time;
    }

    public void setCreated_time(long created_time) {
        this.created_time = created_time;
    }
}
