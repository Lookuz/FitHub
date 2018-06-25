package com.fithub.codekienmee.fithub;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.PriorityQueue;

public class ForumFragment extends Fragment {

    private final static FitPost MOCK_POST = new FitPost("Post Title", "Post Content",
            "Post Author", 10, 1, new Date());

    private RecyclerView postRecyclerView; //RecyclerView that handles displaying of posts
    private PostAdapter postAdapter;
    private PriorityQueue<FitPost> postList; //Heap that stores Post objects.
    // Order is determined by Comparator passed in on initialization.
    // TODO: Replace Object with Post class Object.

    /**
     * Inner class that extends the use of ViewHolder.
     * Holds the view for each post.
     */
    private class PostHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView author;
        private TextView date;
        private TextView numLikes;
        private TextView numDislikes;

        public PostHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.post_view_forum, parent, false));

            this.title = (TextView) itemView.findViewById(R.id.post_forum_title);
            this.author = (TextView) itemView.findViewById(R.id.post_forum_author);
            this.date = (TextView) itemView.findViewById(R.id.post_forum_date);
            this.numLikes = (TextView) itemView.findViewById(R.id.post_forum_likesNum);
            this.numDislikes = (TextView) itemView.findViewById(R.id.post_forum_dislikesNum);
        }

        /**
         * Method that binds a FitPost and it's data to the current view holder.
         */
        public void bindPost(FitPost post) {
            this.title.setText(post.getTitle());
            this.author.setText(post.getAuthor());
            this.date.setText(post.getDate().toString()); // TODO: Formatting for date.
            this.numDislikes.setText(Integer.toString(post.getNumDislikes()));
            this.numLikes.setText(Integer.toString(post.getNumLikes()));
            // TODO: set color for thumbs up/down depending on which is more.
        }
    }

    /**
     * Inner class that extends the use of Adapter.
     * Connects the respective ViewHolder class to the RecyclerView
     */
    private class PostAdapter extends RecyclerView.Adapter<PostHolder> {

        private PriorityQueue<FitPost> postListInner;

        public PostAdapter(PriorityQueue<FitPost> postList) {
            this.postListInner = postList;
        }

        @Override
        public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new PostHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(PostHolder holder, int position) {
            FitPost post = this.postListInner.poll();
            holder.bindPost(post);
        }

        @Override
        public int getItemCount() {
            return postListInner.size();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Initialize PQ with Comparator(default by date)
        this.initList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        this.postRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_forum_recycler_view);
        this.postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.postAdapter = new PostAdapter(this.postList);
        this.postRecyclerView.setAdapter(this.postAdapter);
        return view;
    }

    /**
     * Initializes the list of posts into postList.
     * TODO: Set Comparator object for postList.
     */
    private void initList() {
        this.postList = new PriorityQueue<>();
        this.postList.offer(MOCK_POST);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
