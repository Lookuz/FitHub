package com.fithub.codekienmee.fithub;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Class that displays the log in and sign up buttons.
 */
public class LogInSignUp extends Fragment {

    private Button logIn;
    private Button signUp;

    public static LogInSignUp newInstance() {
         Bundle args = new Bundle();
         LogInSignUp fragment = new LogInSignUp();
         fragment.setArguments(args);
         return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log_in_sign_up, container, false);

        this.logIn = view.findViewById(R.id.start_up_login);
        this.signUp = view.findViewById(R.id.start_up_sign_up);
        this.logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: set log in callback.
            }
        });
        this.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: set sign up callback.
            }
        });

        return view;
    }
}
