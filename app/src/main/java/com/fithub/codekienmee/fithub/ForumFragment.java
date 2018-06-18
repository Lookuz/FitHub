package com.fithub.codekienmee.fithub;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.PriorityQueue;

public class ForumFragment extends Fragment {

    private RecyclerView postRecyclerView; //RecyclerView that handles displaying of posts
    private PostAdapter postAdapter;
    private PriorityQueue<Object> postList; //Heap that stores Post objects.
    // Order is determined by Comparator passed in on initialization.
    // TODO: Replace Object with Post class Object.

    /**
     * Inner class that extends the use of ViewHolder.
     * Holds the view for each post.
     */
    private class PostHolder extends RecyclerView.ViewHolder {
        public PostHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.post_view_forum, parent, false));
        }
    }

    /**
     * Inner class that extends the use of Adapter.
     * Connects the respective ViewHolder class to the RecyclerView
     */
    private class PostAdapter extends RecyclerView.Adapter<PostHolder> {

        private PriorityQueue<Object> postListInner;

        public PostAdapter(PriorityQueue<Object> postList) {
            this.postListInner = postList;
        }

        @Override
        public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new PostHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(PostHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
//            return postListInner.size();
            return 10;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Initialize PQ with Comparator(default by date)
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        this.postRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_forum_recycler_view);
        this.postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.initList();
        return view;
    }

    /**
     * Initializes the list of posts into postList.
     * TODO: Set Comparator object for postList.
     */
    private void initList() {
        this.postList = new PriorityQueue<>();

        this.postAdapter = new PostAdapter(postList);
        this.postRecyclerView.setAdapter(this.postAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
