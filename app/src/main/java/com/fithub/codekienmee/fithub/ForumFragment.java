package com.fithub.codekienmee.fithub;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class ForumFragment extends ListFragment implements PostCallBack {

    private static final String IS_COMMENT_KEY = "isComment";

    private boolean isOpen;

    private FloatingActionButton newPost;
    private FloatingActionButton filterOptions;
    private FloatingActionButton alphabeticalOrder;
    private FloatingActionButton topRated;
    private FloatingActionButton mostRecent;
    private Stack<Fragment> postStack;
    private DatabaseReference postDB;

    private class SyncData extends AsyncTask<List, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(List... lists) {

            postDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        final FitPost post = ds.getValue(FitPost.class);
                        post.setPostKey(ds.getKey());
                        postList.add(post);
                    }

                    postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    postAdapter = new PostAdapter(postList);
                    postRecyclerView.setAdapter(postAdapter);
                    postAdapter.notifyAdapterSetDataChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
//            postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//            postAdapter = new PostAdapter(postList);
//            postRecyclerView.setAdapter(postAdapter);
//            postAdapter.notifyAdapterSetDataChanged();
            super.onPostExecute(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    @Override
    public void onCallBack(FitPost post) {
        FitUser user = ((MainPageActivity) getActivity()).getUser();

        if(post != null) {
            this.postList.add(0, post);
            String postKey = this.postDB.push().getKey();
            post.setPostKey(postKey);
            this.postDB.child(postKey).setValue(post);
            this.postAdapter.notifyAdapterSetDataChanged();

            ProfileManager.createPost(getContext(), user, post);
        }
    }

    public Stack<Fragment> getPostStack() {
        return postStack;
    }

    /**
     * Method to set the color of thumbs up and down image colors based dynamically based on the
     * number of likes and dislikes of the post.
     */
    public static void setLikesColor(ImageView thumbsUp, ImageView thumbsDown,
                                     int likes, int dislikes) {
        if (likes > dislikes) {
            thumbsUp.setImageResource(R.drawable.ic_like_green);
            thumbsDown.setImageResource(R.drawable.ic_dislike_mono);
        } else if (likes < dislikes) {
            thumbsUp.setImageResource(R.drawable.ic_like_mono);
            thumbsDown.setImageResource(R.drawable.ic_dislike_red);
        } else {
            thumbsUp.setImageResource(R.drawable.ic_like_mono);
            thumbsDown.setImageResource(R.drawable.ic_dislike_mono);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.postList = new ArrayList<>();
        this.postStack = new Stack<>();
        this.isOpen = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        this.initView(view);
        return view;
    }

    /**
     * Method to initialize the widgets of this fragment.
     * Widgets: New Post Button, Filter Button.
     */
    private void initView(View view) {
        this.newPost = view.findViewById(R.id.forum_create_post);
        this.filterOptions = view.findViewById(R.id.forum_filter_posts);
        this.alphabeticalOrder = view.findViewById(R.id.forum_filter_alphabetical);
        this.mostRecent = view.findViewById(R.id.forum_filter_most_recent);
        this.topRated = view.findViewById(R.id.forum_filter_top_rated);
        this.postRecyclerView = view.findViewById(R.id.fragment_forum_recycler_view);
        this.postDB = FirebaseDatabase.getInstance().getReference("FitPosts");

        if (((MainPageActivity) getActivity()).hasUser()){
            final PostCallBack callBack = this;
            this.newPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PostFragment postFragment = PostFragment.newInstance(callBack);
                    Bundle args = new Bundle();
                    args.putBoolean(IS_COMMENT_KEY, false);
                    postFragment.setArguments(args);
                    setSlideAnim(Gravity.BOTTOM, postFragment);
                    ((ContainerFragment) getParentFragment()).overlayFragment(postFragment);
                    onPause();
                }
            });
        } else {
            // TODO: Gray out button.
        }

        initFilterOptions();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        new SyncData().execute();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        this.newPost.hide();
        this.filterOptions.hide();
        super.onPause();
    }

    @Override
    public void onResume() {
        this.newPost.show();
        this.filterOptions.show();
        if (this.postAdapter != null) {
            this.postAdapter.notifyAdapterSetDataChanged();
        }
        super.onResume();
    }

    @Override
    public void onPostClick(FitPost post) {
        /**
         * Note that use of Slide Transition requires minimum API of 21.
         */
        CommentsFragment commentsFragment = CommentsFragment.newInstance(post);
        setSlideAnim(Gravity.RIGHT, commentsFragment);
        ((ContainerFragment) getParentFragment()).overlayFragment(commentsFragment);
        onPause();
    }

    private void initFilterOptions() {
        final Animation open = AnimationUtils.loadAnimation(getContext(), R.anim.floating_action_button_open);
        final Animation close = AnimationUtils.loadAnimation(getContext(), R.anim.floating_action_button_close);
        this.filterOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen) {
                    mostRecent.startAnimation(close);
                    alphabeticalOrder.startAnimation(close);
                    topRated.startAnimation(close);
                    mostRecent.setClickable(false);
                    alphabeticalOrder.setClickable(false);
                    isOpen = false;
                } else {
                    mostRecent.startAnimation(open);
                    alphabeticalOrder.startAnimation(open);
                    topRated.startAnimation(open);
                    mostRecent.setClickable(true);
                    alphabeticalOrder.setClickable(true);
                    isOpen = true;
                }
            }
        });
        this.alphabeticalOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByAlphabetical();
            }
        });
        this.topRated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByPopular();
            }
        });
    }

    private void sortByAlphabetical() {
        Comparator<FitPost> comparator = new Comparator<FitPost>() {
            @Override
            public int compare(FitPost o1, FitPost o2) {
                return o1.getTitle().compareTo(o2.getTitle());
            }
        };
        Collections.sort(postList, comparator);
        postAdapter.notifyDataSetChanged();
    }

    private void sortByPopular() {
        Comparator<FitPost> comparator = new Comparator<FitPost>() {
            @Override
            public int compare(FitPost o1, FitPost o2) {
                int likeDifference = o2.getNumLikes() - o1.getNumLikes();
                if (likeDifference != 0) {
                    return likeDifference;
                } else {
                    int dislikeDifference = o1.getNumDislikes() - o2.getNumDislikes();
                    return dislikeDifference;
                }
            }
        };
        Collections.sort(postList, comparator);
        postAdapter.notifyDataSetChanged();
    }
}