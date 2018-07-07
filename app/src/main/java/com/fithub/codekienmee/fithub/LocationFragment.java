package com.fithub.codekienmee.fithub;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Fade;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;

public class LocationFragment extends Fragment {

    private Animation locButtonAnim; // TODO: Animation for Idle Location Button.
    private ImageButton locButton;

    // Configure any variables
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.locButtonAnim = AnimationUtils
                .loadAnimation(getActivity(), R.anim.location_button_anim);
    }

    // Inflate view
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View locView = inflater.inflate(
               R.layout.fragment_location_service, container, false);
        this.locButton = locView.findViewById(R.id.main_location_button);
        this.locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // Fades out and replaces current Fragment with MapsFragment
                MapsFragment mapsFragment = new MapsFragment();
                mapsFragment.setEnterTransition((Transition) new Fade()
                        .setDuration(400).setStartDelay(1200));
                Animation animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setDuration(1000);
                animation.setInterpolator(new DecelerateInterpolator());
                v.startAnimation(animation);
                v.setVisibility(View.GONE);
                v.setClickable(false);

                getFragmentManager().beginTransaction()
                        .replace(R.id.frag_location, mapsFragment)
                        .commit();
            }
        });
        return locView;
    }

    //Finalize animations and variables
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.locButton.startAnimation(this.locButtonAnim);
    }
}
