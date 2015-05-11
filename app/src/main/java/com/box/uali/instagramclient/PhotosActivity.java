package com.box.uali.instagramclient;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhotosActivity extends ActionBarActivity {

    public static final String CLIENT_ID = "53f6a336e5eb46b4add08ffc8c19b68b";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotosAdapter aPhotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        // Initialize the list of photos
        photos = new ArrayList<>();

        // Create the adapter linking it to the source
        aPhotos = new InstagramPhotosAdapter(this, photos);

        // Find the listview from the layout
        ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);

        // Attach the adapter to the list view
        lvPhotos.setAdapter(aPhotos);

        // Send out API request to popular photos
        fetchPopularPhotos();

    }

    // Triggers API request
    public void fetchPopularPhotos() {

        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;

        // Create the network client
        AsyncHttpClient client = new AsyncHttpClient();

        // Trigger the GET request
        client.get(url, null, new JsonHttpResponseHandler() {
            // onSuccess

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // Expecting a JSON object
                Log.i("DEBUG", "SUCCESS: " + response.toString());

                // Iterate over each photo and decode the item into a java object
                JSONArray photosJSON = null;

                try {
                    photosJSON = response.getJSONArray("data");

                    for (int i = 0; i < photosJSON.length(); i++) {
                        JSONObject photoJSON = photosJSON.getJSONObject(i);

                        // Decode the attributes of the json into a data model
                        InstagramPhoto photo = new InstagramPhoto();

                        Log.i("DEBUG", " Photo ID:" + photoJSON.getString("id"));

                        photo.username = photoJSON.getJSONObject("user").getString("username");
                        try {
                            JSONObject captionJSON = photoJSON.getJSONObject("caption");
                            Log.i("DEBUG", " Caption:" + captionJSON.toString());
                            photo.caption = captionJSON.getString("text");
                        } catch (JSONException jsonEx) {
                            photo.caption = "";
                        }

                        photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                        photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        photo.likeCount = photoJSON.getJSONObject("likes").getInt("count");

                        photos.add(photo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("DEBUG", "Exception: " + e.getMessage());
                }

                // callback
                aPhotos.notifyDataSetChanged();
            }


            // onFailure

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // DO SOMETHING
                Log.i("DEBUG", "FAILURE: " + responseString);
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
