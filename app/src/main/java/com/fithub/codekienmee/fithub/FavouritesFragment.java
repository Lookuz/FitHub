package com.fithub.codekienmee.fithub;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import java.util.Stack;

public class FavouritesFragment extends ListFragment {

    private FitUser user;
    private Stack<Fragment> fragmentStack;

    private ViewFlipper viewFlipper;
    private ConstraintLayout posts;
    private ConstraintLayout locations;

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

    private void initView(View view) {
        this.viewFlipper = view.findViewById(R.id.favourites_view_flipper);
        this.posts = view.findViewById(R.id.favourites_posts);
        this.locations = view.findViewById(R.id.favourites_locations);
        this.postRecyclerView = view.findViewById(R.id.favourites_recycler_view);

        this.postAdapter = new PostAdapter(this.postList);
        this.postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.postRecyclerView.setAdapter(this.postAdapter);

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
