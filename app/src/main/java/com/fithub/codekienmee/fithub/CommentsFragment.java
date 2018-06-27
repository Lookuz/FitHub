package com.fithub.codekienmee.fithub;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Class that shows contents of a post and it's comments.
 */
public class CommentsFragment extends Fragment {

    private FitPost post;

    private TextView title;
    private TextView content;
    private TextView reply;
    private TextView favourite;
    private TextView numLikes;
    private TextView numDislikes;
    private ImageView likeImage;
    private ImageView dislikeImage;

    public static CommentsFragment newInstance(FitPost post) {
        CommentsFragment commentsFragment = new CommentsFragment();
        commentsFragment.post = post;
        return commentsFragment;

    }

    private void init(View view) {
        this.title = view.findViewById(R.id.comments_title);
        this.content = view.findViewById(R.id.comments_content);
        this.reply = view.findViewById(R.id.comments_reply);
        this.favourite = view.findViewById(R.id.comments_favourite);
        this.numLikes = view.findViewById(R.id.comments_likesNum);
        this.numDislikes = view.findViewById(R.id.comments_dislikesNum);
        this.likeImage = view.findViewById(R.id.comments_likesImg);
        this.dislikeImage = view.findViewById(R.id.comments_dislikesImg);

        this.title.setText(this.post.getTitle());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);
        this.init(view);

        return view;
    }
}
