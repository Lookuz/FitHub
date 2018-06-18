package com.fithub.codekienmee.fithub;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public class LocationFragment extends Fragment {

    private Animation locButtonAnim;
    private ImageButton locButton;

    // Configure any variables
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locButtonAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.location_button_anim);
    }

    // Inflate view
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View locView = inflater.inflate(
               R.layout.fragment_location_service, container, false);
        locButton = locView.findViewById(R.id.main_location_button);
        // Set OnClickListener for locButton here
        return locView;
    }

    //Finalize animations and variables
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        locButton.startAnimation(locButtonAnim);
    }
}
