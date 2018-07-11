package com.fithub.codekienmee.fithub;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Class that displays start-up UI.
 * Functionalities: Sign Up/ Log In Options.
 */
public class StartUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

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
        this.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = StartUpActivity.this.user.getText().toString();
                String password = StartUpActivity.this.password.getText().toString();
                if (user.equals("") || password.equals("")) {
                    // TODO: Showing warning dialog.
                } else {
                    firebaseAuth.signInWithEmailAndPassword(user, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                                    } else {
                                        Toast.makeText(StartUpActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        this.signInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
}