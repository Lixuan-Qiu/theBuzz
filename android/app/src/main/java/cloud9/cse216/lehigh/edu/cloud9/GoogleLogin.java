package cloud9.cse216.lehigh.edu.cloud9;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class GoogleLogin extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "SignInActivity";
    private static final String token = "319649689632-faqtfv5tgaa3n0urvoprhv66s9kdv6bg.apps.googleusercontent.com";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView mTextView;
    private Button SignOut, Continue;
    private LinearLayout Prof_Section;
    private TextView nameText, emailText, famName, givenName, id;
    VolleySingleton volley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        SignOut = (Button) findViewById(R.id.bn_logout);
        Continue = (Button) findViewById(R.id.bn_continue);
        nameText = (TextView) findViewById(R.id.Name);
        emailText = (TextView) findViewById(R.id.Email);
        famName = (TextView) findViewById(R.id.FamilyName);
        givenName = (TextView) findViewById(R.id.GivenName);
        id = (TextView) findViewById(R.id.ID);
        volley = new VolleySingleton(this);
        Prof_Section = (LinearLayout) findViewById(R.id.profSection);
        Prof_Section.setVisibility(View.GONE);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(token)
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        SignOut.setOnClickListener(this);
        Continue.setOnClickListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            handleSignInResult(task);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

    }


    private void updateUI(GoogleSignInAccount account) {

        if (account != null) {

            Prof_Section.setVisibility(View.VISIBLE);
            String name = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String email = account.getEmail();
            String personId = account.getId();

            nameText.setText(name);
            emailText.setText(email);
            famName.setText(personFamilyName);
            givenName.setText(personGivenName);
            id.setText(personId);

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            SignOut.setVisibility(View.VISIBLE);
            Continue.setVisibility(View.VISIBLE);


        } else {


            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            SignOut.setVisibility(View.GONE);
            Continue.setVisibility(View.GONE);
            Prof_Section.setVisibility(View.GONE);

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            String url = "https://agile-plateau-21593.herokuapp.com/login";
            Map<String, String> params = new HashMap<String, String>();
            params.put("id_token", idToken);
            JSONObject request = new JSONObject(params);
            Log.i("key and uid","newmessage");
            //sending JSONObject of id_token to login
            JsonObjectRequest getReq = new JsonObjectRequest(Request.Method.POST, url, request,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // response
                            try {
                                int uId = response.getInt("uid");
                                String session_key = response.getString("sessionkey");
                                Log.i("key and uid","key: "+ session_key+ " uid: "+uId);
                                SharedPreferences mySP = getSharedPreferences("sesKey", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = mySP.edit();
                                editor.putString("key",session_key);
                                editor.putInt("uid",uId);
                                editor.commit();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("cpl220", "error:" + error.getMessage());
                        }
                    });
            volley.getRequestQueue().add(getReq);
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);

                    }
                });
        /*
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
                            }
                        } catch (final JSONException e) {
                            Log.d("cpl220", "Error parsing JSON file: " + e.getMessage());
                            return;
                        }
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
        */
    }

    private void next() {
        Intent direct = new Intent(GoogleLogin.this, DisplayActivity.class);
        startActivity(direct);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.bn_logout:
                signOut();
                break;
            case R.id.bn_continue:
                next();
                break;


            // ...
        }
    }
    // }


}
