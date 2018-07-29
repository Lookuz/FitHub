package com.fithub.codekienmee.fithub;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.Arrays;

/**
 * Class that displays start-up UI.
 * Functionality: Sign Up/ Log In Options.
 */
public class StartUpActivity extends AppCompatActivity implements WarningCallBack,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 100;

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private CallbackManager callbackManager;
    private TwitterAuthClient twitterAuthClient;

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
        FacebookSdk.sdkInitialize(getApplicationContext());
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(
                        getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                        getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
                .debug(true).build();
        Twitter.initialize(config);
        setContentView(R.layout.activity_start_up_page);
        this.firebaseAuth = FirebaseAuth.getInstance();
        AppEventsLogger.activateApp(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

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
        this.initLogInFacebook();
        this.initLogInTwitter();
        this.initSignUp();
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
                if (user != null) { // Checks if there is currently a signed in user.
                    showLoadingScreen(getString(R.string.start_up_logging_in));
                    startActivity(new Intent(StartUpActivity.this, MainPageActivity.class));
                    finish();
                }
            }
        };
        this.firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCallBack(boolean exit) {
        this.user.getText().clear();
        this.password.getText().clear();
        ((EditText) findViewById(R.id.sign_up_name)).getText().clear();
        ((EditText) findViewById(R.id.sign_up_email)).getText().clear();
        ((EditText) findViewById(R.id.sign_up_password)).getText().clear();
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
                    WarningDialog warningDialog = WarningDialog
                            .newInstance(WarningEnum.EMPTY_FIELDS, StartUpActivity.this);

                    warningDialog.show(getSupportFragmentManager(), "Empty Fields Warning");
                } else {
                    showLoadingScreen(getString(R.string.start_up_logging_in));
                    firebaseAuth.signInWithEmailAndPassword(user, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(getApplicationContext(),
                                        MainPageActivity.class));
                                finish();
                            } else {
                                WarningDialog warningDialog = WarningDialog.newInstance(
                                        WarningEnum.INCORRECT_CRED, StartUpActivity.this);

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

        this.signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });
    }

    /**
     * Method to sign in via Facebook.
     */
    private void initLogInFacebook() {

        this.callbackManager = CallbackManager.Factory.create();

        this.signInFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(StartUpActivity.this,
                        Arrays.asList("email", "public_profile"));

                LoginManager.getInstance().registerCallback(callbackManager,
                        new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        authWithProvider(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // TODO: Set On Cancel
                    }

                    @Override
                    public void onError(FacebookException error) {
                        onConnectionFailed(null);
                    }
                });
            }
        });
    }

    /**
     * Method to sign in via Twitter
     */
    private void initLogInTwitter() {

        this.twitterAuthClient = new TwitterAuthClient();

        this.signInTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitterAuthClient.authorize(StartUpActivity.this,
                        new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> result) {
                        authWithProvider(result.data);
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        onConnectionFailed(null);
                    }
                });
            }
        });

    }

    /**
     * Method to sign up.
     */
    private void initSignUp() {
        this.signUp = findViewById(R.id.start_up_sign_up);
        this.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
                ImageButton submit = findViewById(R.id.sign_up_submit);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String name = ((EditText) findViewById(R.id.sign_up_name))
                                .getText().toString();
                        final String email = ((EditText) findViewById(R.id.sign_up_email))
                                .getText().toString();
                        final String password = ((EditText) findViewById(R.id.sign_up_password))
                                .getText().toString();

                        if (name.equals("") || email.equals("") || password.equals("")) {
                            WarningDialog warningDialog = WarningDialog.newInstance(
                                    WarningEnum.EMPTY_SIGN_IN, StartUpActivity.this);

                            warningDialog.show(getSupportFragmentManager().beginTransaction(),
                                    "Empty Sign In");
                        } else {
                            // Hide Keyboard
                            InputMethodManager manager = (InputMethodManager)StartUpActivity
                                    .this.getSystemService(Context.INPUT_METHOD_SERVICE);
                            manager.hideSoftInputFromWindow(StartUpActivity.this.getWindow()
                                    .getCurrentFocus().getWindowToken(), 0);

                            showLoadingScreen(getString(R.string.start_up_signing_up));
                            firebaseAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(StartUpActivity.this,
                                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // TODO: Show success(Tick?)
                                        TextView loadingMessage = findViewById(R.id.start_up_loading_message);
                                        loadingMessage.setText(getString(R.string.start_up_sign_up_success));
                                        // Update name.
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        user.updateProfile(new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name)
                                                .build());

                                        showLoadingScreen(getString(R.string.start_up_logging_in));
                                        startActivity(new Intent(StartUpActivity.this,
                                                MainPageActivity.class));
                                    } else {
                                        WarningDialog warningDialog = WarningDialog.newInstance(
                                                WarningEnum.FAILED_SIGN_IN, StartUpActivity.this);

                                        warningDialog.show(getSupportFragmentManager().beginTransaction(),
                                                "Failed Sign In");
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.callbackManager.onActivityResult(requestCode, resultCode, data);
        this.twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_SIGN_IN:
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    this.authWithProvider(account);
                } catch (ApiException e) {
                    Snackbar.make(findViewById(R.id.start_up_activity),
                            R.string.start_up_google_play_failed, Snackbar.LENGTH_SHORT).show();
                }
                return;
            default:
                break;
        }
    }

    /**
     * Method to authenticate Firebase using External Provider
     */
    private void authWithProvider(Object token) {

        AuthCredential credential = null;

        if (token instanceof GoogleSignInAccount) {
            credential = GoogleAuthProvider.getCredential(
                    ((GoogleSignInAccount) token).getIdToken(), null);
        } else if (token instanceof AccessToken) {
            credential = FacebookAuthProvider.getCredential(((AccessToken) token).getToken());
        } else if (token instanceof TwitterSession) {
            credential = TwitterAuthProvider.getCredential(((TwitterSession) token)
                    .getAuthToken().token, ((TwitterSession) token).getAuthToken().secret);
        }

        try {
            showLoadingScreen(getString(R.string.start_up_logging_in));
            this.firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this,
                    new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        showLoadingScreen(getString(R.string.start_up_logged_in).concat(" " + user.getDisplayName()));
                        startActivity(new Intent(StartUpActivity.this,
                                MainPageActivity.class));
                        finish();
                    } else {
                        Snackbar.make(findViewById(R.id.start_up_activity), R.string.google_sign_in_warning, Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to initialize loading screen for logging in/signing up.
     */
    private void showLoadingScreen(String message) {
        Log.d("Loading Screen", "On Click");
        this.viewFlipper.setDisplayedChild(
                this.viewFlipper.indexOfChild(findViewById(R.id.start_up_loading_screen)));
        TextView loadingMessage = findViewById(R.id.start_up_loading_message);
        loadingMessage.setText(message);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(findViewById(R.id.start_up_activity),
                R.string.connection_failed, Snackbar.LENGTH_SHORT).show();;
    }
}