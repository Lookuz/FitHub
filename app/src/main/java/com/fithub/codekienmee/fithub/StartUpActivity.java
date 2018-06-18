package com.fithub.codekienmee.fithub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class StartUpActivity extends Activity {

    private RingProgressBar loadingBar; // Loading bar view
    private int progress = 0; // Progress percentage
    // Object that translates progress into loading bar
    private Handler loadBarManager = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (progress < 100) {
                    loadingBar.setProgress(++progress);
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up_page);
        loadingBar = (RingProgressBar)findViewById(R.id.loading_bar_circle);

        /**
         * TODO: Asynchronously fetch remote updates and parse any rendering details
         * into progress parameter.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(100);
                        loadBarManager.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        while(progress < 100) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Start new activity
        startActivity(new Intent(StartUpActivity.this, MainPageActivity.class));
    }
}
