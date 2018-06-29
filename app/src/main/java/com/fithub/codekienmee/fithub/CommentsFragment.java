package com.fithub.codekienmee.fithub;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

/**
 * Class that shows contents of a post and it's comments.
 */
public class CommentsFragment extends Fragment {

    /**
     * Custom Adapter class that determines the view layout in CommentsFragment.
     */
    private class PostAdapter extends ArrayAdapter<FitPost> {

        private Context context;
        private List<FitPost> postList;

        public PostAdapter(@NonNull Context context, int resource, @NonNull List<FitPost> objects) {
            super(context, resource, objects);
            this.context = context;
            this.postList = objects;
        }

        @Override
        public int getCount() {
            return this.postList.size();
        }

        @Nullable
        @Override
        public FitPost getItem(int position) {
            return this.postList.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(this.context).inflate(
                        R.layout.comments_comment_card, null);
            }

            FitPost comment = getItem(position);

            // Recursively set adapter for cascading comments list.
            TextView poster = view.findViewById(R.id.comment_poster_name);
            poster.setText(comment.getAuthor());
            ListView commentsList = view.findViewById(R.id.comment_commentsList);
            if(comment.getComments() != null) {
                commentsList.setAdapter(new PostAdapter(getContext(), 0,
                        comment.getComments()));
            } else {
                commentsList.setVisibility(View.GONE);
            }
            return view;
        }
    }

    private FitPost post;

    private TextView title;
    private TextView content;
    private TextView author;
    private TextView reply;
    private TextView favourite;
    private TextView numLikes;
    private TextView numDislikes;
    private ImageView likeImage;
    private ImageView dislikeImage;
    private ListView comments;

    public static CommentsFragment newInstance(FitPost post) {
        CommentsFragment commentsFragment = new CommentsFragment();
        commentsFragment.post = post;
        return commentsFragment;

    }

    /**
     * Method that initializes the widgets in CommentsFragment.
     */
    private void init(View view, FitPost post) {
        // If not comment, set title.
        if (post.getTitle() != null) {
            TextView title = view.findViewById(R.id.comments_title);
            title.setText(this.post.getTitle());
        }

        this.content = view.findViewById(R.id.comments_content);
        this.author = view.findViewById(R.id.comments_author);
        this.reply = view.findViewById(R.id.comments_reply);
        this.favourite = view.findViewById(R.id.comments_favourite);
        this.numLikes = view.findViewById(R.id.comments_likesNum);
        this.numDislikes = view.findViewById(R.id.comments_dislikesNum);
        this.likeImage = view.findViewById(R.id.comments_likesImg);
        this.dislikeImage = view.findViewById(R.id.comments_dislikesImg);
        this.comments = view.findViewById(R.id.comments_commentsList);

        this.content.setText(this.post.getContent());
        this.author.setText(this.post.getAuthor());
        this.numLikes.setText(Integer.toString(this.post.getNumLikes()));
        this.numDislikes.setText(Integer.toString(this.post.getNumDislikes()));
        // TODO: Set OnClickListener for reply and favourite buttons.
        ForumFragment.setLikesColor(this.likeImage, this.dislikeImage,
                this.post.getNumLikes(), this.post.getNumDislikes());
    }

    /**
     * Method that initializes the favourite and comment button.
     */
    private void initButtons() {
        this.reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Bring up PostFragment.
            }
        });
        this.favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add post to favourite posts.
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        this.init(view, this.post);
        this.comments.setAdapter(new PostAdapter(getContext(), 0, this.post.getComments()));

        return view;
    }
}
