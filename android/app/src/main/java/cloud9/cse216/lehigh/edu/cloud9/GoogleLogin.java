package cloud9.cse216.lehigh.edu.cloud9;

import android.content.Intent;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class GoogleLogin extends AppCompatActivity implements View.OnClickListener {


        private GoogleSignInClient mGoogleSignInClient;
        private TextView mTextView;
        private static final String TAG = "SignInActivity";
        private static final String token = "319649689632-m3hicm6vgscjqbmbun52522tjmtikj4m.apps.googleusercontent.com";
        private static final int RC_SIGN_IN = 9001;
        private Button SignOut, Continue;
        private LinearLayout Prof_Section;
        private TextView nameText, emailText, famName, givenName, id;
        private ImageView pic;

        @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        SignOut = (Button)findViewById(R.id.bn_logout);
        Continue = (Button)findViewById(R.id.bn_continue);
        nameText = (TextView)findViewById(R.id.Name);
        emailText = (TextView)findViewById(R.id.Email);
        famName = (TextView)findViewById(R.id.FamilyName);
        givenName = (TextView)findViewById(R.id.GivenName);
        id = (TextView)findViewById(R.id.ID);
        pic = (ImageView)findViewById(R.id.photo);

        Prof_Section = (LinearLayout)findViewById(R.id.profSection);
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
        public void onActivityResult (int requestCode,int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

                // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
                if (requestCode == RC_SIGN_IN) {
                    // The Task returned from this call is always completed, no need to attach
                    // a listener.
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignInResult(task);
                }
                if(data == null){

                }
            }


        @Override
        public void onStart () {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

    }


        private void updateUI (GoogleSignInAccount account){

        if (account != null) {

            Prof_Section.setVisibility(View.VISIBLE);
            String name = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String email = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();

            nameText.setText(name);
            emailText.setText(email);
            famName.setText(personFamilyName);
            givenName.setText(personGivenName);
            id.setText(personId);
            pic.setImageURI(personPhoto);

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

        private void handleSignInResult (@NonNull Task < GoogleSignInAccount > completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            // Signed in successfully, show authenticated UI.
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("https://agile-plateau-21593.herokuapp.com/login");


            /*This block right here is post request and is not working correctly.
            try {
                List nameValuePairs = new ArrayList(1);
                nameValuePairs.add(new BasicNameValuePair("idToken", idToken));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                final String responseBody = EntityUtils.toString(response.getEntity());
                Log.i(TAG, "Signed in as: " + responseBody);
            } catch (ClientProtocolException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            } catch (IOException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            }
            End of block that is not working correctly
            */

            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }
        private void signIn (){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

        private void signOut(){
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateUI(null);

                        }
                    });

        }

        private void next(){
           Intent direct = new Intent(GoogleLogin.this, DisplayActivity.class);
            startActivity(direct);

        }



        @Override
        public void onClick (View v){
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
