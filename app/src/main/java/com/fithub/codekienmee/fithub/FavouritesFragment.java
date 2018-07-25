package com.fithub.codekienmee.fithub;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.List;
import java.util.Stack;

public class FavouritesFragment extends ListFragment {

    private FitUser user;
    private Stack<Fragment> fragmentStack;

    private ViewFlipper viewFlipper;
    private RecyclerView locationsRecyclerView;
    private LocationsAdapter locationsAdapter;
    private ConstraintLayout posts;
    private ConstraintLayout locations;

    /**
     * Private class that displays information of a favourited location in a card.
     */
    private class LocationsHolder extends RecyclerView.ViewHolder {

        private FitLocation location;

        private ProgressBar crowdLevel;
        private TextView locationName;

        public LocationsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.favourite_locations_card, parent, false));

            this.crowdLevel = itemView.findViewById(R.id.favourites_card_crowd_level);
            this.locationName = itemView.findViewById(R.id.favourites_card_location);
        }

        public void initView(FitLocation location) {
            this.location = location;

            this.locationName.setText(location.getLocationName());
            // TODO: Set progress bar level.
        }
    }

    /**
     * Private class that loads user's favourite locations into a card for display in a Recycler View.
     */
    private class LocationsAdapter extends RecyclerView.Adapter<LocationsHolder> {

        private List<FitLocation> locationList;

        public LocationsAdapter(List<FitLocation> locationList) {
            this.locationList = locationList;
        }

        @Override
        public LocationsHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            return new LocationsHolder(inflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(LocationsHolder locationsHolder, int i) {
            FitLocation location = this.locationList.get(i);
            locationsHolder.initView(location);
        }

        @Override
        public int getItemCount() {
            return (this.locationList == null) ? 0 : this.locationList.size();
        }
    }

    public static FavouritesFragment newInstance() {

        Bundle args = new Bundle();

        FavouritesFragment fragment = new FavouritesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.user = ((MainPageActivity) getActivity()).getUser();
        this.postList = user.getFavouritePosts();
        this.fragmentStack = new Stack<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favourites_fragment, container, false);
        this.initView(view);

        return view;
    }

    private void initView(final View view) {
        this.viewFlipper = view.findViewById(R.id.favourites_view_flipper);
        this.posts = view.findViewById(R.id.favourites_posts);
        this.locations = view.findViewById(R.id.favourites_locations);
        this.postRecyclerView = view.findViewById(R.id.favourites_posts_recycler_view);
        this.locationsRecyclerView = view.findViewById(R.id.favourites_locations_recycler_view);

        this.postAdapter = new PostAdapter(this.postList);
        this.postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.postRecyclerView.setAdapter(this.postAdapter);

        this.locationsAdapter = new LocationsAdapter(user.getFavouriteLocations());
        this.locationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.locationsRecyclerView.setAdapter(this.locationsAdapter);

        this.posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewFlipper.getDisplayedChild() != viewFlipper
                        .indexOfChild(view.findViewById(R.id.favourites_posts_recycler_view))) {
                    viewFlipper.setInAnimation(getActivity(), R.anim.slide_in_right);
                    viewFlipper.setOutAnimation(getActivity(), R.anim.slide_out_right);
                    viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(
                            view.findViewById(R.id.favourites_posts_recycler_view)));
                }
            }
        });
        this.locations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewFlipper.getDisplayedChild() != viewFlipper
                        .indexOfChild(view.findViewById(R.id.favourites_locations_recycler_view))) {
                    viewFlipper.setInAnimation(getActivity(), R.anim.slide_in_left);
                    viewFlipper.setOutAnimation(getActivity(), R.anim.slide_out_left);
                    viewFlipper.setDisplayedChild(viewFlipper.indexOfChild(
                            view.findViewById(R.id.favourites_locations_recycler_view)));
                }
            }
        });
    }

    @Override
    public void onPostClick(FitPost post) {
        CommentsFragment commentsFragment = CommentsFragment.newInstance(post);
        setSlideAnim(Gravity.RIGHT, commentsFragment);
        getFragmentManager().beginTransaction()
                .add(R.id.favourites_fragment, commentsFragment)
                .addToBackStack(null)
                .commit();
        this.fragmentStack.push(commentsFragment);
        onPause();
    }

    @Override
    public void onResume() {
        if (this.postAdapter != null) {
            this.postAdapter.notifyAdapterSetDataChanged();
        }
        super.onResume();
    }

    public boolean onBackPressed() {
        if (!this.fragmentStack.isEmpty()) {
            getFragmentManager().popBackStack();
            this.fragmentStack.pop();
            return true;
        }
        return false;
    }
}
