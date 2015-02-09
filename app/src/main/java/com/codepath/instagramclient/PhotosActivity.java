package com.codepath.instagramclient;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

    private SwipeRefreshLayout swipeContainer;

    private String location_holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        location_holder = null;

        // Get the ListView
        lvPhotos = (ListView) findViewById(R.id.lvPhotos);

        // Create a custom ArrayAdapter for the Instagram photos
        photos = new ArrayList<InstagramPhoto>();
        aPhotos = new InstagramPhotoAdapter(this, photos);

        // Assign the custom ArrayAdapter to the ListView
        lvPhotos.setAdapter(aPhotos);

        // This will help us show all the comments and the full caption in a dialog fragment when
        // a list item is clicked
        setupListViewListener();

        // This section of code is used to swipe to refresh
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(true);
                fetchPopularPhotos();
            }
        });
        swipeContainer.setColorSchemeResources(R.color.instagram_blue,
                R.color.location_blue,
                R.color.instagram_blue,
                R.color.location_blue);

        // Send out API request to popular photos
        fetchPopularPhotos();

    }

    // This will help us show all the comments and the full caption in a dialog fragment when
    // a list item is clicked
    private void setupListViewListener() {
        lvPhotos.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter,
                                            View item,
                                            int pos,
                                            long id) {
                           InstagramPhoto curr_photo = photos.get(pos);
                           showCommentsDialog(curr_photo.getUsername(),
                                              curr_photo.getCaption(),
                                              curr_photo.getProfilePicUrl(),
                                              curr_photo.getCreatedAt(),
                                              curr_photo.getAllComments());
                    }
                });
    }

    // This will help us show all the comments and the full caption in a dialog fragment when
    // a list item is clicked
    private void showCommentsDialog(String username,
                                    String caption,
                                    String profilePicUrl,
                                    Long createdAt,
                                    JSONArray recentComments) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        CommentsDialog allCommentsDialog = CommentsDialog.newInstance(username, caption,
                                                                      profilePicUrl, createdAt,
                                                                      recentComments);
        allCommentsDialog.show(fm, "fragment_all_comments");
    }

    // Method to trigger API request for popular photos
    public void fetchPopularPhotos() {
        String url = "/media/popular?";

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
                photos.clear();
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
                        photo.setImgWidth(photoJSON.getJSONObject("images").
                                getJSONObject("standard_resolution").getInt("width"));
                        photo.setType(photoJSON.getString("type"));
                        photo.setLikesCount(photoJSON.getJSONObject("likes").getInt("count"));
                        photo.setProfilePicUrl(photoJSON.getJSONObject("user").
                                getString("profile_picture"));
                        photo.setCreatedAt(photoJSON.getLong("created_time"));
                        photo.setTags(photoJSON.getJSONArray("tags"));

                        if (photoJSON.optJSONObject("caption") != null) {
                            photo.setCaption(photoJSON.getJSONObject("caption").getString("text"));
                        }

                        // This was an attempt to get the location. The idea was to get the
                        // latitude and longitude, and then use a different end point
                        // locations/search in the function below so it gets the English name loc.
                        // It worked for some cases. However, it turned out that a lot of photos
                        // stored the English name directly without latitude and longitude and
                        // so I had to handle more cases here, which I didn't fully have the time
                        // for.
                        if (photoJSON.optJSONObject("location") != null) {
                            /*String latitude = photoJSON.getJSONObject("location").
                                    getString("latitude");
                            String longitude = photoJSON.getJSONObject("location").
                                    getString("longitude");
                            getEnglishLocation(latitude, longitude);*/
                            photo.setLocation(location_holder);
                            location_holder = null;

                        }

                        if (photoJSON.optJSONObject("comments") != null) {
                            JSONObject commentsObj = photoJSON.getJSONObject("comments");

                            photo.setNumComments(commentsObj.getInt("count"));
                            JSONArray commentsArr = commentsObj.getJSONArray("data");
                            photo.setAllComments(commentsArr);
                        }
                        photos.add(photo);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Callback
                aPhotos.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
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

    // This was an attempt to get the location. The idea was to get the
    // latitude and longitude above, and then use a different end point
    // locations/search in the function below so it gets the English name loc.
    // In this case, it only picked the first possible location.
    // It worked for some cases. However, it turned out that a lot of photos
    // stored the English name directly without latitude and longitude and
    // so I had to handle more cases here, which I didn't fully have the time
    // for.
    public void getEnglishLocation(String latitude, String longitude) {
        String url = "locations/search?lat=" + latitude + "&lng=" +
                longitude + "&";
        InstagramRestClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray locsArray = response.getJSONArray("data");
                    JSONObject firstLoc = (JSONObject) locsArray.get(0);
                    location_holder = firstLoc.getString("name");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
