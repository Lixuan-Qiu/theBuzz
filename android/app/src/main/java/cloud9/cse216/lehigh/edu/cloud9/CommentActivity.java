package cloud9.cse216.lehigh.edu.cloud9;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.util.ArrayList;

public class CommentActivity extends Activity {

    ArrayList<String> commentList = new ArrayList<>();
    VolleySingleton volley;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        final Message msg = intent.getParcelableExtra("message");
        volley = new VolleySingleton(this);
        getComments(msg);


        // The Send button gets the text from the input box and returns it to the calling activity
        final EditText et = (EditText) findViewById(R.id.cContent);
        Button bSend = (Button) findViewById(R.id.sendComment);
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et.getText().toString().equals("")) {
                    postComment(msg, et);
                }
            }

        });
    }

    /**
     * postComment makes a POST request to add a comment to the message of specified ID. It sends an json object containing the user id,
     * session key, and comment content to the url.
     * Once the request is successful the array is cleared and a new getComment is called to refresh all the messages.
     * @param m is a final message object that we are adding a comment to
     * */
    //need to put comment content into the request
    public void postComment(final Message m, final EditText content){
        String url = "https://agile-plateau-21593.herokuapp.com/messages/comment/" + m.mId;
        JSONObject request = new JSONObject();
        //retrieve uid and key from local saved file
        SharedPreferences mySP = getSharedPreferences("sesKey", Activity.MODE_PRIVATE);
        int userId=-1,sKey=-1;
        userId = mySP.getInt("uid", userId);
        sKey = mySP.getInt("key", sKey);
        if (userId==-1||sKey==-1)
            Log.d("cpl220", "uid or session key is failed to be retrieved from share preferences in putLikeCount");
        try {
            request.put("uId", userId);
            request.put("key", sKey);
            request.put("mMessage", content.getText().toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        JsonObjectRequest postComment = new JsonObjectRequest(Request.Method.POST, url,request,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.d("cpl220", "Successful post of new comment");
                        commentList.clear();
                        getComments(m);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("cpl220", "error:" + error.getMessage());
                    }
                });
        volley.getRequestQueue().add(postComment);
    }

    /**
     * getMessages gets all of the current messages from the backend server
     * */
    public void getComments(final Message m){
        String url = "https://agile-plateau-21593.herokuapp.com//comment/:" + m.mId;
        JSONObject request = new JSONObject();
        //retrieve uid and key from local saved file
        SharedPreferences mySP = getSharedPreferences("sesKey", Activity.MODE_PRIVATE);
        int userId=-1,sKey=-1;
        userId = mySP.getInt("uid", userId);
        sKey = mySP.getInt("key", sKey);
        if (userId==-1||sKey==-1)
            Log.d("cpl220", "uid or session key is failed to be retrieved from share preferences in pushMessage");

        try {
            request.put("uId", userId);
            request.put("key", sKey);
        }catch (Exception e){
            e.printStackTrace();
        }

        JsonObjectRequest getReq = new JsonObjectRequest(Request.Method.GET, url, request,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {

                        populateListFromVolley(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("cpl220", "error:" + error.getMessage());
                    }
                });
        // Add the request to the RequestQueue.
        volley.getRequestQueue().add(getReq);
    }

    public void populateListFromVolley(JSONObject response) {
        try {
            JSONArray list = response.getJSONArray("mData");
            for (int i = 0; i < list.length(); ++i) {
                String msg = list.getJSONObject(i).getString("cComment");
                commentList.add(msg);
            }
        } catch (final JSONException e) {
            Log.d("cpl220", "Error parsing JSON file: " + e.getMessage());
            return;
        }
        ListView mListView = (ListView) findViewById(R.id.commentText);
        ArrayAdapter adapter = new ArrayAdapter<>(CommentActivity.this,
                android.R.layout.simple_list_item_1,
                commentList);
        mListView.setAdapter(adapter);
    }
}

//1. complete getComment function
////2. complete postComment function
////3. ask about volley
////4. complete Comment's UI(up button and new button)