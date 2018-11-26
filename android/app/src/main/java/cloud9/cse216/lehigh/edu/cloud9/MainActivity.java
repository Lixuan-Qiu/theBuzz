package cloud9.cse216.lehigh.edu.cloud9;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity  {
    Button logBtn, ccelBtn;
    EditText uName,passWd;
    VolleySingleton volley;
    TextView tx1;
    int counter = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        volley = new VolleySingleton(this);
        logBtn= (Button)findViewById(R.id.button);
        uName = (EditText)findViewById(R.id.editText);
        passWd = (EditText)findViewById(R.id.editText2);

        ccelBtn= (Button)findViewById(R.id.button2);
        tx1 = (TextView)findViewById(R.id.textView3);
        tx1.setVisibility(View.GONE);
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = uName.getText().toString();
                String password = passWd.getText().toString();
                String url = "https://agile-plateau-21593.herokuapp.com/login";
                Map<String, String> params = new HashMap<String,String>();
                params.put("username", username);
                params.put("password", password);
                JSONObject request = new JSONObject(params);
                JsonObjectRequest submitInfo = new JsonObjectRequest(
                        Request.Method.POST, url,request, new Response.Listener<JSONObject>()
                        {
                            int uid;
                            String key;
                            @Override
                            public void onResponse(JSONObject response) {
                                // response
                                Log.d("cpl220", response.toString());
                                Log.d("cpl220", "Successful submission");
                                try {
                                    if (response.getString("mStatus").equals("error"))
                                    {
                                        tx1.setVisibility(View.VISIBLE);
                                        tx1.setBackgroundColor(Color.RED);
                                        counter--;
                                        tx1.setText(Integer.toString(counter));
                                        Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_SHORT).show();
                                        if (counter <= 0) logBtn.setEnabled(false);

                                        return;
                                    }
                                    key = response.getString("sessionkey");
                                    uid = response.getInt("uid");
                                    Toast.makeText(getApplicationContext(),"Redirecting...",Toast.LENGTH_SHORT).show();
                                } catch (final JSONException e) {
                                    Log.d("cpl220", "Error parsing JSON file: " + e.getMessage());
                                    return;
                                }
                                //save the session key using sharePreference, if there is no existing SP, it will create a new one;
                                //if there is a existing one, the existing content will be overwritten
                                SharedPreferences mySP = getSharedPreferences("sesKey", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = mySP.edit();
                                editor.putString("key",key);
                                editor.putInt("uid",uid);
                                editor.commit();
                                Log.d("cpl220", "Key successfully saved");
                                Log.d("cpl220",key);
                                Log.d("cpl220",Integer.toString(uid));

                                Log.d("cpl220", "Successful submission");
                                Toast.makeText(getApplicationContext(), "Redirecting...",Toast.LENGTH_SHORT).show();
                                //start the main activity
                                Intent direct = new Intent(MainActivity.this, DisplayActivity.class);
                                startActivity(direct);
                            }
                        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("cpl220", "Login error:" + error.getMessage());
                        error.printStackTrace();
                        tx1.setVisibility(View.VISIBLE);
                        tx1.setBackgroundColor(Color.RED);
                        counter--;
                        tx1.setText(Integer.toString(counter));
                        Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_SHORT).show();
                        if (counter <= 0) logBtn.setEnabled(false);
                    }
                });

                volley.getRequestQueue().add(submitInfo);
            }
        });
         ccelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

