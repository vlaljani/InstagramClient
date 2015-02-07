package com.codepath.instagramclient;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhotosActivity extends ActionBarActivity {

    private ArrayList<InstagramPhoto> photos;

    private InstagramPhotoAdapter aPhotos;
    private ListView lvPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        // Get the ListView
        lvPhotos = (ListView) findViewById(R.id.lvPhotos);

        // Create a custom ArrayAdapter for the Instagram photos
        photos = new ArrayList<InstagramPhoto>();
        aPhotos = new InstagramPhotoAdapter(this, photos);

        // Assign the custom ArrayAdapter to the ListView
        lvPhotos.setAdapter(aPhotos);

        // Send out API request to popular photos
        fetchPopularPhotos();
    }

    // Method to trigger API request for popular photos
    public void fetchPopularPhotos() {
        String url = "/media/popular";

        // Trigger the GET request
        // This request is asynchronous i.e. we do not send the request and waiting.
        // It sends the request in the background, and when it finishes retrieving,
        // the responseHandler is called.
        InstagramRestClient.get(url, null, new JsonHttpResponseHandler() {

            // Choosing JSONObject (as opposed to a JSONArray) because our root is a dictionary
            // rather than an array (determined by { - for dictionary vs [ for array)
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Iterate over response and decode each popular photo item into a java object
                JSONArray photosJSON = null;
                try {
                    photosJSON = response.getJSONArray("data"); //array of posts
                    //Iterate array of posts
                    for (int i = 0; i < photosJSON.length(); i++) {
                        JSONObject photoJSON = photosJSON.getJSONObject(i);

                        //Decode JSONObject
                        InstagramPhoto photo = new InstagramPhoto();

                        // These keys should not be null
                        photo.setUsername(photoJSON.getJSONObject("user").getString("username"));
                        photo.setImgUrl(photoJSON.getJSONObject("images").
                                getJSONObject("standard_resolution").getString("url"));
                        photo.setImgHeight(photoJSON.getJSONObject("images").
                                getJSONObject("standard_resolution").getInt("height"));
                        photo.setImgHeight(photoJSON.getJSONObject("images").
                                getJSONObject("standard_resolution").getInt("width"));
                        photo.setType(photoJSON.getString("type"));
                        photo.setLikesCount(photoJSON.getJSONObject("likes").getInt("count"));
                        photo.setProfilePicUrl(photoJSON.getJSONObject("user").
                                getString("profile_picture"));
                        photo.setCreatedAt(photoJSON.getLong("created_time"));

                        if (photoJSON.optJSONObject("caption") != null) {
                            photo.setCaption(photoJSON.getJSONObject("caption").getString("text"));
                        }

                        photos.add(photo);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Callback
                aPhotos.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode,
                                  Header[] headers,
                                  String responseString,
                                  Throwable throwable) {
                //DO SOMETHING HERE

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
