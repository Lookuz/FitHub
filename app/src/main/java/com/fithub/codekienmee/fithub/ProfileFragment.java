package com.fithub.codekienmee.fithub;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Fragment that displays the profile page of current signed in FitUser.
 */
public class ProfileFragment extends Fragment {
    
    private FitUser user;

    private TextView name;

    public static ProfileFragment newInstance(FitUser user) {

        Bundle args = new Bundle();
        
        ProfileFragment fragment = new ProfileFragment();
        fragment.user = user;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        this.initView(view);

        return view;
    }

    private void initView(View view) {
        this.name = view.findViewById(R.id.profile_user_name);

        this.name.setText(user.getName());
    }
}
