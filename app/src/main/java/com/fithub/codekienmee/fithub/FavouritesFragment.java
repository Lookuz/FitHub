package com.fithub.codekienmee.fithub;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Stack;

public class FavouritesFragment extends ListFragment {

    private FitUser user;
    private Stack<Fragment> fragmentStack;
    private boolean left;

    private ViewFlipper viewFlipper;
    private RecyclerView locationsRecyclerView;
    private LocationsAdapter locationsAdapter;
    private Button posts;
    private Button locations;
    private LinearLayout favouritesTabBar;

    /**
     * Asynchronously fetch favourites from Firebase DB.
     */
    private class SyncFavourites extends AsyncTask<String, String, String> {

        private DatabaseReference databaseReference;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.databaseReference = FirebaseDatabase.getInstance().getReference();
            locationsAdapter = new LocationsAdapter(user.getFavouriteLocations());
            postAdapter = new PostAdapter(user.getFavouritePosts());
        }

        @Override
        protected String doInBackground(String... strings) {

            if (user.getFavouritePostKeys() != null &&
                    user.getFavouritePosts().size() != user.getFavouritePostKeys().size()) {
                this.databaseReference.child("FitPosts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (user.getFavouritePostKeys() != null) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (user.getFavouritePostKeys().contains(ds.getKey())) {
                                    postAdapter.addPost(ds.getValue(FitPost.class));
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            if (user.getFavouriteLocationsKey() != null &&
                    user.getFavouriteLocations().size() != user.getFavouriteLocationsKey().size()) {
                this.databaseReference.child("FitLocations").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (user.getFavouriteLocationsKey() != null) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (user.getFavouriteLocationsKey().contains(ds.getKey())) {
                                    Log.d("Adding Location ", ds.getKey());
                                    locationsAdapter.addLocation(ds.getValue(FitLocation.class));
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            postRecyclerView.setAdapter(postAdapter);

            locationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            locationsRecyclerView.setAdapter(locationsAdapter);
        }
    }

    /**
     * Private class that displays information of a favourited location in a card.
     */
    private class LocationsHolder extends RecyclerView.ViewHolder implements WarningCallBack {

        private FitLocation location;

        private ProgressBar crowdLevel;
        private TextView locationName;
        private ImageButton deleteLocation;

        public LocationsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.favourite_locations_card, parent, false));

            this.crowdLevel = itemView.findViewById(R.id.favourites_card_crowd_level);
            this.locationName = itemView.findViewById(R.id.favourites_card_location);
            this.deleteLocation = itemView.findViewById(R.id.favourites_card_delete);
        }

        public void initView(FitLocation location) {
            this.location = location;

            this.locationName.setText(location.getLocationName());
            this.deleteLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WarningDialog warningDialog = WarningDialog.newInstance(
                            WarningEnum.REMOVE_LOCATION, LocationsHolder.this);

                    warningDialog.show(getFragmentManager(), "Removing Location");
                }
            });
        }

        @Override
        public void onCallBack(boolean exit) {
            if (exit) {
                user.unfavouriteLocationKey(this.location.getLocationKey());
                locationsAdapter.removeLocation(this.location);
                locationsAdapter.notifyDataSetChanged();
            }
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

        public void removeLocation(FitLocation location) {
            this.locationList.remove(location);
        }

        public void addLocation(FitLocation location) {
            this.locationList.add(location);
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
//        this.postList = user.getFavouritePosts();
        this.fragmentStack = new Stack<>();
        this.left = true;
        new SyncFavourites().execute();
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
        this.favouritesTabBar = view.findViewById(R.id.favourites_tab_bar);

//        this.postAdapter = new PostAdapter(this.postList);
//        this.postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        this.postRecyclerView.setAdapter(this.postAdapter);
//
//        this.locationsAdapter = new LocationsAdapter(user.getFavouriteLocations());
//        this.locationsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        this.locationsRecyclerView.setAdapter(this.locationsAdapter);

        this.posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!left) {
                    animateBar();
                }
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
                if (left) {
                    animateBar();
                }
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
            this.postAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    public boolean onBackPressed() {
        if (!this.fragmentStack.isEmpty()) {
            getFragmentManager().popBackStack();
            this.fragmentStack.pop();
            this.onResume();
            return true;
        }
        return false;
    }

    private void animateBar() {
//        this.favouritesTabBar.animate().translationX(100);
        if (this.left) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_right);
            favouritesTabBar.startAnimation(animation);
            this.left = false;
        } else {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_left);
            favouritesTabBar.startAnimation(animation);
            this.left = true;
        }
    }
}