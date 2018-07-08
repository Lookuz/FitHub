package com.fithub.codekienmee.fithub;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class StartUpActivity extends AppCompatActivity {

    private static final int LOG_IN = 0;
    private static final int SIGN_UP = 1;

    private ViewFlipper viewFlipper;
    private Button logIn;
    private Button signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up_page);
        this.initView();
    }

    /**
     * Method to initialize widgets.
     * Widgets: Log In Button, Sign Up Button.
     */
    private void initView() {
        this.viewFlipper = findViewById(R.id.start_up_view_flipper);
        this.logIn = findViewById(R.id.start_up_login);
        this.signUp = findViewById(R.id.start_up_sign_up);

        this.viewFlipper.setInAnimation(StartUpActivity.this, R.anim.slide_in_left);
        this.viewFlipper.setOutAnimation(StartUpActivity.this, R.anim.slide_out_left);
        this.logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
                initOptions(LOG_IN);
            }
        });
        this.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.showNext();
                initOptions(SIGN_UP);
            }
        });
    }

    /**
     * Initializes options for log in/ sign up.
     * @param code determines if the action is log in or sign up.
     */
    private void initOptions(int code) {
        if (this.viewFlipper.getCurrentView().getId() == R.id.sign_in_options_bar) {
            LinearLayout googleOption = findViewById(R.id.start_up_option_google);
            LinearLayout facebookOption = findViewById(R.id.start_up_option_facebook);
            LinearLayout emailOption = findViewById(R.id.start_up_option_email);
            LinearLayout phoneOption = findViewById(R.id.start_up_option_phone);

            // TODO: Set appropriate sign in functions.
            googleOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                    startActivity(intent);
                    // IMPORTANT TO USE FINISH HERE SO BACK IN MAIN ACTIVITY DOESN'T GO BACK TO THIS PAGE.
                    finish();
                }
            });
            facebookOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            emailOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            phoneOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}
