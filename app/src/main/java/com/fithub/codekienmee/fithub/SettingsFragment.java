package com.fithub.codekienmee.fithub;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

public class SettingsFragment extends Fragment {

    private HashMap<String, Boolean> userSettings;

    public static SettingsFragment newInstance(FitUser user) {

        Bundle args = new Bundle();

        SettingsFragment fragment = new SettingsFragment();
        fragment.userSettings = user.getUserSettings();
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
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        return view;
    }
}
