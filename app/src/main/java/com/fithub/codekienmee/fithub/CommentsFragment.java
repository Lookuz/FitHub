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
    private void init(View view) {
        this.title = view.findViewById(R.id.comments_title);
        this.content = view.findViewById(R.id.comments_content);
        this.author = view.findViewById(R.id.comments_author);
        this.reply = view.findViewById(R.id.comments_reply);
        this.favourite = view.findViewById(R.id.comments_favourite);
        this.numLikes = view.findViewById(R.id.comments_likesNum);
        this.numDislikes = view.findViewById(R.id.comments_dislikesNum);
        this.likeImage = view.findViewById(R.id.comments_likesImg);
        this.dislikeImage = view.findViewById(R.id.comments_dislikesImg);
        this.comments = view.findViewById(R.id.comments_commentsList);

        this.title.setText(this.post.getTitle());
        this.content.setText(this.post.getContent());
        this.author.setText(this.post.getAuthor());
        // TODO: Set OnClickListener for reply and favourite buttons.
        // TODO: Dynamically set color for likes/dislikes image.
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
        this.init(view);
        this.comments.setAdapter(new PostAdapter(getContext(), 0, this.post.getComments()));

        return view;
    }
}
