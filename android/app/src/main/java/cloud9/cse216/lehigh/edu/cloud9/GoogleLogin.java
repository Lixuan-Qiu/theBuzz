package cloud9.cse216.lehigh.edu.cloud9;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;


public class GoogleLogin extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);


            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            // Build a GoogleSignInClient with the options specified by gso.
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        }


        @Override
        public void onStart(){
            super.onStart();
            // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            updateUI(account);

        }

        private void updateUI(GoogleSignInAccount account) {

            if (account != null) {
                mTextView.setText(getString(R.string.signed_in_fmt, account.getDisplayName()));

                findViewById(R.id.sign_in_button).setVisibility(View.GONE);
                findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
            }
            else {
                mTextView.setText(R.string.signed_out);

                findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
                findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
            }
        }

}
