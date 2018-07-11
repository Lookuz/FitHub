package com.fithub.codekienmee.fithub;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

/**
 * Class that displays start-up UI.
 * Functionalities: Sign Up/ Log In Options.
 */
public class StartUpActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;

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

        this.viewFlipper.setInAnimation(StartUpActivity.this, R.anim.slide_in_left);
        this.viewFlipper.setOutAnimation(StartUpActivity.this, R.anim.slide_out_left);
    }
}
