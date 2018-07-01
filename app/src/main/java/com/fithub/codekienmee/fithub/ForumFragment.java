package com.fithub.codekienmee.fithub;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PriorityQueue;

public class ForumFragment extends Fragment implements PostCallBack {

    private final static FitPost MOCK_POST = new FitPost("Post Title", "Post Content",
            "Post Author", 10, 1, new Date());
    private static final String IS_COMMENT_KEY = "isComment";

    private RecyclerView postRecyclerView; //RecyclerView that handles displaying of posts
    private PostAdapter postAdapter;
    private List<FitPost> postList;
    private FloatingActionButton newPost;
    private FloatingActionButton filter;

    // Order is determined by Comparator passed in on initialization.

    @Override
    public void onCallBack(FitPost post) {
        if(post != null) {
            this.postList.add(post);
            this.postAdapter.notifyAdapterSetDataChanged();
        }
    }

    /**
     * Inner class that extends the use of ViewHolder.
     * Holds the view for each post.
     */
    private class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private FitPost post;

        private TextView title;
        private TextView author;
        private TextView date;
        private TextView numLikes;
        private TextView numDislikes;
        private ImageView thumbsUp;
        private ImageView thumbsDown;


        public PostHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.post_view_forum, parent, false));

            this.title = (TextView) itemView.findViewById(R.id.post_forum_title);
            this.author = (TextView) itemView.findViewById(R.id.post_forum_author);
            this.date = (TextView) itemView.findViewById(R.id.post_forum_date);
            this.numLikes = (TextView) itemView.findViewById(R.id.post_forum_likesNum);
            this.numDislikes = (TextView) itemView.findViewById(R.id.post_forum_dislikesNum);
            this.thumbsUp = (ImageView) itemView.findViewById(R.id.post_forum_likesImg);
            this.thumbsDown = (ImageView) itemView.findViewById(R.id.post_forum_dislikesImg);

            itemView.setOnClickListener(this);
        }

        /**
         * Method that binds a FitPost and it's data to the current view holder.
         */
        public void bindPost(FitPost post) {
            this.post = post;
            this.title.setText(post.getTitle());
            this.author.setText(post.getAuthor());
            this.date.setText(post.getDate().toString()); // TODO: Formatting for date.
            this.numDislikes.setText(Integer.toString(post.getNumDislikes()));
            this.numLikes.setText(Integer.toString(post.getNumLikes()));

            ForumFragment.setLikesColor(this.thumbsUp, this.thumbsDown,
                    this.post.getNumLikes(), this.post.getNumDislikes());
        }

        @Override
        public void onClick(View v) {
            /**
             * Note that use of Slide Transition requires minimum API of 21.
             */
            CommentsFragment commentsFragment = CommentsFragment.newInstance(this.post);
            ((MainPageActivity) getActivity()).overlayFragment(Gravity.RIGHT, commentsFragment);
        }
    }

    /**
     * Method to set the color of thumbs up and down image colors based dynamically based on the
     * number of likes and dislikes of the post.
     */
    public static void setLikesColor(ImageView thumbsUp, ImageView thumbsDown,
                                     int likes, int dislikes) {
        if (likes > dislikes) {
            thumbsUp.setImageResource(R.drawable.ic_thumb_up_green);
            thumbsDown.setImageResource(R.drawable.ic_thumb_down_grey);
        } else if (likes < dislikes) {
            thumbsUp.setImageResource(R.drawable.ic_thumb_up_grey);
            thumbsDown.setImageResource(R.drawable.ic_thumb_down_red);
        } else {
            thumbsUp.setImageResource(R.drawable.ic_thumb_up_grey);
            thumbsDown.setImageResource(R.drawable.ic_thumb_down_grey);
        }
    }

    /**
     * Inner class that extends the use of Adapter.
     * Connects the respective ViewHolder class to the RecyclerView
     */
    private class PostAdapter extends RecyclerView.Adapter<PostHolder> {

        private PriorityQueue<FitPost> postQueueInner;
        private List<FitPost> postListInner;

        public PostAdapter(List<FitPost> postList) {
            this.postListInner = postList;
            this.postQueueInner = new PriorityQueue<>();
            for (FitPost post : this.postListInner) {
                this.postQueueInner.offer(post);
            }
        }

        @Override
        public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new PostHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(PostHolder holder, int position) {
            FitPost post = this.postQueueInner.poll();
            holder.bindPost(post);
        }

        @Override
        public int getItemCount() {
            if (this.postListInner != null) {
                return postListInner.size();
            } else {
                return 0;
            }
        }

        /**
         * Custom implementation of notifySetDataChanged() method.
         */
        private void notifyAdapterSetDataChanged() {
            for(FitPost post : this.postListInner) {
                this.postQueueInner.offer(post);
            }
            this.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        this.initView(view);
        return view;
    }

    /**
     * Initializes the list of posts into postList.
     */
    private void initList() {
        FitPost firstPost = new FitPost("Top 10 health tips to follow", "Embrace a Healthy Diet Plan\n " +
                "Move more.\nBe smoke-free.\nScheduled Sleep.\nPrioritize preventive screenings.\nConnect with others.\nStay hydrated.\n" +
                "Appreciate what you have.\nPick up a hobby\nMeditate",
                "Health Guru", 10, 1, new Date());
        FitPost secondPost = new FitPost(null, "Great tips! Will try out 5 and 7!",
                "Fitness Beginner", 8, 0, new Date());
        FitPost thirdPost = new FitPost(null, "Didn't work for me..",
                "Non-Believer", 1, 6, new Date());
        secondPost.addComment(thirdPost);
        firstPost.addComment(secondPost);

        FitPost fourthPost = new FitPost("How to cardio effectively??", "Hi, I am a beginner to fitness, recently" +
                " started getting into it. Can anyone give tips on how to effectively build up stamina? Thanks!",
                "Fitness Beginner", 5, 0, new Date());

        FitPost fifthPost = new FitPost("Six Pack Shortcuts", "Hey guys! Wanna build six pack shortcuts in no time?" +
                " Check out my YouTube videos in the link descriptions in my profile! 100% guaranteed to work!",
                "Mike Chang", 3, 21, new Date());

        FitPost sixthPost = new FitPost("Looking for Gym Buddy at NUS", "Plz do back day thnx.",
                "Jag", 0, 0, new Date());

        this.postList = new ArrayList<>();
        this.postList.add(firstPost);
        this.postList.add(fourthPost);
        this.postList.add(fifthPost);
        this.postList.add(sixthPost);
    }

    /**
     * Method to initialize the widgets of this fragment.
     * Widgets: New Post Button, Filter Button.
     */
    private void initView(View view) {
        this.newPost = view.findViewById(R.id.forum_create_post);
        this.filter = view.findViewById(R.id.forum_filter_posts);
        this.postRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_forum_recycler_view);
        this.postRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.postAdapter = new PostAdapter(this.postList);
        this.postRecyclerView.setAdapter(this.postAdapter);

        final PostCallBack callBack = this;
        this.newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostFragment postFragment = PostFragment.newInstance(callBack);
                Bundle args = new Bundle();
                args.putBoolean(IS_COMMENT_KEY, false);
                postFragment.setArguments(args);
                ((MainPageActivity) getActivity()).overlayFragment(Gravity.BOTTOM, postFragment);
            }
        });

        this.filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}