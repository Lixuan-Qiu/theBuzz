package cloud9.cse216.lehigh.edu.cloud9;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

    public class GoogleLogin extends AppCompatActivity implements View.OnClickListener/*, GoogleApiClient.OnConnectionFailedListener*/ {

    //View.OnClickListener{
        private GoogleSignInClient mGoogleSignInClient;
        private TextView mTextView;//, Profile;
        private static final String TAG = "SignInActivity";
        private static final int RC_SIGN_IN = 9001;
        private Button SignOut, Continue;
        //TextView nameText = (TextView)findViewById(R.id.Name);
        //TextView emailText = (TextView)findViewById(R.id.Email);
        @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        SignOut = (Button)findViewById(R.id.bn_logout);
        Continue = (Button)findViewById(R.id.bn_continue);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        SignOut.setOnClickListener(this);


        Continue.setOnClickListener(this);
        //Profile.setVisibility(View.GONE);
    }

       /* @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult){


        }*/


        @Override
        public void onActivityResult (int requestCode,int resultCode, Intent data){
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
        public void onStart () {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

    }


        private void updateUI (GoogleSignInAccount account){

        if (account != null) {
            String name = account.getDisplayName();
            //nameText.setText(name);
            String lastName = account.getFamilyName();
            String email = account.getEmail();
            //emailText.setText(email);
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();

            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.bn_logout).setVisibility(View.VISIBLE);
            findViewById(R.id.bn_continue).setVisibility(View.VISIBLE);


        } else {


            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.bn_logout).setVisibility(View.GONE);
            findViewById(R.id.bn_continue).setVisibility(View.GONE);


        }
    }

        private void handleSignInResult (Task < GoogleSignInAccount > completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
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
