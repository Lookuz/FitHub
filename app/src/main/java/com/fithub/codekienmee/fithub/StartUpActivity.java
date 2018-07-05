package com.fithub.codekienmee.fithub;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class StartUpActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up_page);

        this.fragmentManager = getSupportFragmentManager();
        this.fragmentManager.beginTransaction()
                .replace(R.id.start_up_frag_view, LogInSignUp.newInstance())
                .commit();
    }

}
