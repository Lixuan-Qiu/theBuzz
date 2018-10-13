package cloud9.cse216.lehigh.edu.cloud9;

import android.app.Activity;
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


public class LoginActivity extends Activity  {
    Button logBtn, ccelBtn;
    EditText uName,passWd;
    VolleySingleton volley;
    TextView tx1;
    int counter = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                            int key;
                            @Override
                            public void onResponse(JSONObject response) {
                                // response
                                Log.d("cpl220", "Successful submission");
                                try {
                                    key = response.getJSONObject("FirstResponse").getInt("key");
                                    Toast.makeText(getApplicationContext(),"Redirecting...",Toast.LENGTH_SHORT).show();
                                } catch (final JSONException e) {
                                    Log.d("cpl220", "Error parsing JSON file: " + e.getMessage());
                                    return;
                                }
                                //save the session key using sharePreference, if there is no existing SP, it will create a new one;
                                //if there is a existing one, the existing content will be overwritten
                                SharedPreferences mySP = getSharedPreferences("sesKey", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = mySP.edit();
                                editor.putInt("key",key);
                                editor.commit();
                                Toast.makeText(getApplicationContext(), "Redirecting...",Toast.LENGTH_SHORT).show();
                            }

                        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_SHORT).show();

                        tx1.setVisibility(View.VISIBLE);
                        tx1.setBackgroundColor(Color.RED);
                        counter--;
                        tx1.setText(Integer.toString(counter));

                        if (counter == 0) logBtn.setEnabled(false);
                        Log.d("cpl220", "error:" + error.getMessage());
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