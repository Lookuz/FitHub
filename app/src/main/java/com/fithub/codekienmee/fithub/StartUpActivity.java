package com.fithub.codekienmee.fithub;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Class that displays start-up UI.
 * Functionality: Sign Up/ Log In Options.
 */
public class StartUpActivity extends AppCompatActivity implements WarningCallBack, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 100;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;

    private ViewFlipper viewFlipper;
    private ImageButton submit;
    private EditText user;
    private EditText password;
    private ImageButton signInGoogle;
    private ImageButton signInFacebook;
    private ImageButton signInTwitter;
    private TextView signUp;
    private TextView forgotPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up_page);
        this.firebaseAuth = FirebaseAuth.getInstance();

        this.initView();
    }
    /**
     * Method to initialize widgets.
     * Widgets: Log In Button, Sign In Options, Sign Up, Forgot Password.
     */
    private void initView() {
        this.viewFlipper = findViewById(R.id.start_up_view_flipper);
        this.submit = findViewById(R.id.start_up_submit);
        this.user = findViewById(R.id.start_up_user);
        this.password = findViewById(R.id.start_up_password);
        this.signInGoogle = findViewById(R.id.start_up_google);
        this.signInFacebook = findViewById(R.id.start_up_facebook);
        this.signInTwitter = findViewById(R.id.start_up_twitter);
        this.signUp = findViewById(R.id.start_up_sign_up);
        this.forgotPW = findViewById(R.id.start_up_forgot_pw);
        this.initWidgets();

        this.viewFlipper.setInAnimation(StartUpActivity.this, R.anim.slide_in_left);
        this.viewFlipper.setOutAnimation(StartUpActivity.this, R.anim.slide_out_left);
    }

    /**
     * Method to initialize functionality of widgets.
     */
    private void initWidgets() {
        this.initLogInEmail();
        this.initLogInGoogle();
        this.signInTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        this.signInFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        this.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        this.forgotPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // Not signed in. Display log in page.
                } else {
                    // Already signed in. Sign in straight to main activity.
                }
            }
        };
        this.firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onCallBack(boolean exit) {
        this.user.getText().clear();
        this.password.getText().clear();
    }

    /**
     * Method to sign in via email.
     */
    private void initLogInEmail() {
        this.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = StartUpActivity.this.user.getText().toString();
                String password = StartUpActivity.this.password.getText().toString();
                if (user.equals("") || password.equals("")) {
                    WarningDialog warningDialog = WarningDialog.newInstance(WarningEnum.EMPTY_FIELDS,
                            StartUpActivity.this);

                    warningDialog.show(getSupportFragmentManager(), "Empty Fields Warning");
                } else {
                    firebaseAuth.signInWithEmailAndPassword(user, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // TODO: Transition into loading screen.
                                    if(task.isSuccessful()) {
                                        startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                                    } else {
                                        WarningDialog warningDialog = WarningDialog
                                                .newInstance(WarningEnum.INCORRECT_CRED,
                                                        StartUpActivity.this);

                                        warningDialog.show(getSupportFragmentManager(),
                                                "Incorrect Credentials Warning");
                                    }
                                }
                            });
                }
            }
        });
    }

    /**
     * Method to sign in via Google.
     */
    private void initLogInGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        this.googleSignInClient = GoogleSignIn.getClient(this, gso);
//        final GoogleApiClient googleApiClient =   new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();

        this.signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case RC_SIGN_IN:
//                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                // TODO: Change to async task and combine with loading screen.
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//                if (result.isSuccess()) {
//                    GoogleSignInAccount account = result.getSignInAccount();
//                    this.authWithGoogle(account);
//                }
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    this.authWithGoogle(account);
                } catch (ApiException e) {
                    e.printStackTrace();
                    // TODO: Handle exception.
                }
                break;
            default:
                break;
        }
    }

    /**
     * Method to authenticate firebase using Google Sign in
     */
    private void authWithGoogle(GoogleSignInAccount googleSignInAccount) {

        AuthCredential authCredential = GoogleAuthProvider.getCredential(
                googleSignInAccount.getIdToken(), null);
        this.firebaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                            // TODO: update UI with new user.
                        } else {
                            Snackbar.make(findViewById(R.id.start_up_activity),
                                    R.string.google_sign_in_warning, Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}