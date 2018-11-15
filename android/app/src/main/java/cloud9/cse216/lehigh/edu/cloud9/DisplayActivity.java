package cloud9.cse216.lehigh.edu.cloud9;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DisplayActivity extends AppCompatActivity {

    /**
     * messageArrayList holds the data we get from Volley
     * volley is the VolleySingleton object
     */
    ArrayList<Message> messageArrayList = new ArrayList<>();
    // Instantiate the VolleySingleton
    VolleySingleton volley;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        volley = new VolleySingleton(this);
        getMessages();


        // The Send button gets the text from the input box and returns it to the calling activity
        final EditText et = (EditText) findViewById(R.id.editText);
        Button bSend = (Button) findViewById(R.id.sendButton);
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et.getText().toString().equals("")) {
                    pushMessage(et);
                }
            }

        });

    }
    /*
        function for Camera
     */
    public void Camera(View v){
        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Log.i("bitmap",imageBitmap.toString());
        }
    }

    /**
     * getMessages gets all of the current messages from the backend server
     */
    public void getMessages() {
        String url = "https://agile-plateau-21593.herokuapp.com/messages";
        //retrieve uid and key from local saved file
        SharedPreferences mySP = getSharedPreferences("sesKey", Activity.MODE_PRIVATE);
        int userId = -1;
        String sKey = "";
        userId = mySP.getInt("uid", userId);
        sKey = mySP.getString("key", sKey);
        final int userid = userId;
        final String skey = sKey;
        Log.d("cpl220", "getmessage uid: " + Integer.toString(userId));
        Log.d("cpl220", "getmessage key: " + sKey);

        if (userId == -1 || sKey == "")
            Log.d("cpl220", "uid or session key is failed to be retrieved from share preferences in getMessage");

        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", Integer.toString(userId));
        params.put("key", sKey);
        JSONObject request = new JSONObject(params);
        JsonObjectRequest getReq = new JsonObjectRequest(Request.Method.GET, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("cpl220", "getMessage response");
                        populateListFromVolley(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("cpl220", "error:" + error.getMessage());
                        error.printStackTrace();
                    }
                }) {
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", Integer.toString(userid));
                params.put("key", skey);
                return params;
            }
        };
        // Add the request to the RequestQueue.
        volley.getRequestQueue().add(getReq);
    }

    /**
     * pushMessage does a PUSH request to add a new message to the app.
     * It creates a new JSON object mMessage -> <new message boy>
     * Once the request is successful the array is cleared and a new getMessages is called to refresh all the messages.
     *
     * @param et is a final EditText where we grab the value of the new massage to send.
     */
    public void pushMessage(final EditText et) {
        String url = "https://agile-plateau-21593.herokuapp.com/messages";
        //retrieve uid and key from local saved file
        SharedPreferences mySP = getSharedPreferences("sesKey", Activity.MODE_PRIVATE);
        int userId = -1;
        String sKey = "";
        userId = mySP.getInt("uid", userId);
        sKey = mySP.getString("key", sKey);
        final int userid = userId;
        final String skey = sKey;
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", Integer.toString(userId));
        params.put("key", sKey);
        params.put("mMessage", et.getText().toString());
        JSONObject request = new JSONObject(params);
        if (userId == -1 || sKey == "")
            Log.d("cpl220", "uid or session key is failed to be retrieved from share preferences in pushMessage");

        JsonObjectRequest newMessage = new JsonObjectRequest(Request.Method.POST, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.d("cpl220", "Successful post of new message");
                        messageArrayList.clear();
                        getMessages();
                        et.getText().clear();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("cpl220", "error:" + error.getMessage());
                    }
                }) {
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", Integer.toString(userid));
                params.put("key", skey);
                return params;
            }
        };

        volley.getRequestQueue().add(newMessage);

    }

    /**
     * putLike makes a PUT request to add one like to the message of specified ID. It sends an empty json object to the url.
     * Once the request is successful the array is cleared and a new getMessages is called to refresh all the messages.
     *
     * @param m is a final message object that we are adding a like to
     */
    public void putLikeCount(final Message m) {
        String url = "https://agile-plateau-21593.herokuapp.com/messages/" + m.mId + "/like";
        //retrieve uid and key from local saved file
        SharedPreferences mySP = getSharedPreferences("sesKey", Activity.MODE_PRIVATE);
        int userId = -1;
        String sKey = "";
        userId = mySP.getInt("uid", userId);
        sKey = mySP.getString("key", sKey);
        final int userid = userId;
        final String skey = sKey;
        if (userId == -1 || sKey == "")
            Log.d("cpl220", "uid or session key is failed to be retrieved from share preferences in putLikeCount");
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", Integer.toString(userId));
        params.put("key", sKey);
        JSONObject request = new JSONObject(params);
        JsonObjectRequest putLike = new JsonObjectRequest(Request.Method.PUT, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.d("cpl220", "Successful post of new message");
                        messageArrayList.clear();
                        getMessages();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("cpl220", "error:" + error.getMessage());
                    }
                }) {
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", Integer.toString(userid));
                params.put("key", skey);
                return params;
            }
        };
        volley.getRequestQueue().add(putLike);
    }

    /**
     * putDislike makes a PUT request to add one like to the message of specified ID. It sends an empty json object to the url.
     * Once the request is successful the array is cleared and a new getMessages is called to refresh all the messages.
     *
     * @param m is a final message object that we are adding a like to
     */
    public void putDislikeCount(final Message m) {
        String url = "https://agile-plateau-21593.herokuapp.com/messages/" + m.mId + "/dislike";
        //retrieve uid and key from local saved file
        SharedPreferences mySP = getSharedPreferences("sesKey", Activity.MODE_PRIVATE);
        int userId = -1;
        String sKey = "";
        userId = mySP.getInt("uid", userId);
        sKey = mySP.getString("key", sKey);
        final int userid = userId;
        final String skey = sKey;
        if (userId == -1 || sKey == "")
            Log.d("cpl220", "uid or session key is failed to be retrieved from share preferences in putDislikeCount");
        Map<String, String> params = new HashMap<String, String>();
        params.put("uid", Integer.toString(userId));
        params.put("key", sKey);
        JSONObject request = new JSONObject(params);
        JsonObjectRequest putDislike = new JsonObjectRequest(Request.Method.PUT, url, request,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.d("cpl220", "Successful post of new message");
                        messageArrayList.clear();
                        getMessages();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("cpl220", "error:" + error.getMessage());
                    }
                }) {
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", Integer.toString(userid));
                params.put("key", skey);
                return params;
            }
        };
        volley.getRequestQueue().add(putDislike);
    }

    public void getComment(final Message m) {
        Intent in = new Intent(DisplayActivity.this, CommentActivity.class);
        in.putExtra("message", m);
        startActivity(in);
    }


    public void populateListFromVolley(JSONObject response) {
        try {
            JSONArray list = response.getJSONArray("mData");
            for (int i = 0; i < list.length(); ++i) {
                //wait for response from Corey to see the correct form of variable names
                int mId = list.getJSONObject(i).getInt("mId");
                String msg = list.getJSONObject(i).getString("mMessage");
                int like = list.getJSONObject(i).getInt("mlikeCount");
                int dislike = list.getJSONObject(i).getInt("mdislikeCount");
                messageArrayList.add(new Message(mId, msg, like, dislike));
            }
        } catch (final JSONException e) {
            Log.d("cpl220", "Error parsing JSON file: " + e.getMessage());
            return;
        }
        Log.d("cpl220", "Successfully parsed JSON file.");
        RecyclerView rv = (RecyclerView) findViewById(R.id.message_list_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        ItemListAdapter adapter = new ItemListAdapter(this, messageArrayList, this);
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * This function sets the behavior of menu on the toolbar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "Settings Option Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout:
                Intent intent = new Intent(getApplicationContext(), GoogleLogin.class);
                startActivity(intent);
                /*
                Toast.makeText(getApplicationContext(), "Logging out", Toast.LENGTH_SHORT).show();

                String url = "https://agile-plateau-21593.herokuapp.com/logout";
                final SharedPreferences mySP = getSharedPreferences("sesKey", Activity.MODE_PRIVATE);
                int userId = -1;
                String sKey = "";
                userId = mySP.getInt("uid", userId);
                sKey = mySP.getString("key", sKey);
                final int userid = userId;
                final String skey = sKey;
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", Integer.toString(userId));
                params.put("key", sKey);
                JSONObject request = new JSONObject(params);
                Log.d("cpl220", request.toString());

                final JsonObjectRequest getReq = new JsonObjectRequest(Request.Method.POST, url, request,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("mStatus").equals("error")) return;
                                    else {
                                        Log.d("cpl220", "Successfully log out with server status: " + response.getString("mStatus"));
                                        SharedPreferences.Editor editor = mySP.edit();
                                        editor.putInt("sessionkey", -1);
                                        editor.commit();
                                        Intent direct = new Intent(DisplayActivity.this, MainActivity.class);
                                        startActivity(direct);
                                    }
                                } catch (final JSONException e) {
                                    Log.d("cpl220", "Error parsing JSON file: " + e.getMessage());
                                    return;
                                }
                                populateListFromVolley(response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("cpl220", "error:" + error.getMessage());
                            }
                        }) {
                    @Override
                    public Map getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("uid", Integer.toString(userid));
                        params.put("key", skey);
                        return params;
                    }
                };
                // Add the request to the RequestQueue.
                Log.d("cpl220", "getReq: " + getReq.toString());

                volley.getRequestQueue().add(getReq);
                return true;
                */

            default:
                return super.onContextItemSelected(item);
        }
    }
}
