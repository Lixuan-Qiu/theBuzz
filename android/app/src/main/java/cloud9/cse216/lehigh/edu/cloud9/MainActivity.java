package cloud9.cse216.lehigh.edu.cloud9;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * messageArrayList holds the data we get from Volley
     * volley is the VolleySingleton object
     */
    ArrayList<Message> messageArrayList = new ArrayList<>();
    // Instantiate the VolleySingleton
    VolleySingleton volley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    /**
     * getMessages gets all of the current messages from the backend server
     * */
    public void getMessages(){
        String url = "https://agile-plateau-21593.herokuapp.com/messages/all";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final ArrayList<String> myList = new ArrayList<>();
                        populateListFromVolley(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cpl220", "That didn't work!");
            }
        });

// Add the request to the RequestQueue.
        volley.getRequestQueue().add(stringRequest);
    }

    /**
     * pushMessage does a PUSH request to add a new message to the app.
     * It creates a new JSON object mMessage -> <new message boy>
     * Once the request is successful the array is cleared and a new getMessages is called to refresh all the messages.
     * @param et is a final EditText where we grab the value of the new massage to send.
     * */
    public void pushMessage(final EditText et){
        String url = "https://agile-plateau-21593.herokuapp.com/messages";
        JSONObject request = new JSONObject();
        try {
            request.put("mMessage", et.getText().toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        JsonObjectRequest newMessage = new JsonObjectRequest(Request.Method.POST, url,request,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.d("cpl220", "Successful post of new message");
                        messageArrayList.clear();
                        getMessages();
                        et.getText().clear();
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

        volley.getRequestQueue().add(newMessage);

    }
    /**
     * putLike makes a PUT request to add one like to the message of specified ID. It sends an empty json object to the url.
     * Once the request is successful the array is cleared and a new getMessages is called to refresh all the messages.
     * @param m is a final message object that we are adding a like to
     * */
    public void putLikeCount(final Message m){
        String url = "https://agile-plateau-21593.herokuapp.com/messages/" + m.mId + "/like";
        JSONObject request = new JSONObject();
        JsonObjectRequest putLike = new JsonObjectRequest(Request.Method.PUT, url,request,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        Log.d("cpl220", "Successful post of new message");
                        messageArrayList.clear();
                        getMessages();
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
        volley.getRequestQueue().add(putLike);
    }

    public void populateListFromVolley(String response){
        try {
            int arrayStart= response.indexOf('[');
            JSONArray json= new JSONArray(response.substring(arrayStart,response.length()-1));
            for (int i = 0; i < json.length(); ++i) {
                int num = json.getJSONObject(i).getInt("mId");
                String str = json.getJSONObject(i).getString("mMessage");
                int count = json.getJSONObject(i).getInt("mlikeCount");
                messageArrayList.add(new Message(num, str, count));
            }
        } catch (final JSONException e) {
            Log.d("cpl220", "Error parsing JSON file: " + e.getMessage());
            return;
        }
        Log.d("cpl220", "Successfully parsed JSON file.");
        RecyclerView rv = (RecyclerView) findViewById(R.id.message_list_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        ItemListAdapter adapter = new ItemListAdapter(this, messageArrayList,this);
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
