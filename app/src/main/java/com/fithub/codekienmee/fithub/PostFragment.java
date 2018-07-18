package com.fithub.codekienmee.fithub;

import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class that displays the functionality of fragment show when creating a new FitPost
 */
public class PostFragment extends Fragment implements WarningCallBack, OnPostBackPressed {

    private static final String IS_COMMENT_KEY = "isComment";

    private PostCallBack callBack;
    private FitPost post;
    private boolean isComment;

    private EditText content;
    private EditText title;
    private ImageView profilePic;
    private TextView author;
    private TextView submit;

    public static PostFragment newInstance(PostCallBack parent) {
        Bundle args = new Bundle();
        PostFragment fragment = new PostFragment();
        fragment.callBack = parent;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.post = null;
        this.isComment = getArguments().getBoolean(IS_COMMENT_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        this.initView(view);
        return view;
    }

    /**
     * Method to initialize the widgets and views in PostFragment layout.
     */
    private void initView(View view) {
        this.content = view.findViewById(R.id.post_content);
        this.title = view.findViewById(R.id.post_title);
        if (this.isComment) {
            this.title.setVisibility(View.GONE);
        }
        this.author = view.findViewById(R.id.post_author);
        this.submit = view.findViewById(R.id.post_submit);
        this.profilePic = view.findViewById(R.id.post_profile_pic);

        this.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePost();
            }
        });
    }

    /**
     * Method that saves a post for posting.
     */
    private void savePost() {
        String postTitle = this.title.getText()
                .toString();
        String postContent = this.content.getText()
                .toString();

        if ((postTitle.equals("") && this.isComment == false) || postContent.equals("")) {
            WarningDialog warningDialog = WarningDialog.newInstance(
                    WarningEnum.EMPTY_POST, this);

            warningDialog.show(getFragmentManager()
                    .beginTransaction(), "Empty Post Warning");
        } else {
            FitPost newPost = new FitPost(postTitle, postContent,
                    this.author.getText().toString(), getDate(new Date()));
            this.post = newPost;
            ((ContainerFragment) getParentFragment()).popBackStack();
        }
    }

    @Override
    public void onDestroy() {
        if(post != null) {
            this.callBack.onCallBack(this.post);
        }

        super.onDestroy();
    }

    /**
     * Method that specifies callback of result to parent fragment.
     * Exits the new post without saving is result of exit is true.
     * @param exit
     */
    @Override
    public void onCallBack(boolean exit) {
        if (exit) {
            ((ContainerFragment) getParentFragment()).popBackStack();
        } else {
            return;
        }
    }

    /**
     * Method that determines action taken in this fragment when back button is pressed.
     */
    @Override
    public boolean onPostBackPressed() {
        if (title.getText() != null || content.getText() != null) {
            WarningDialog warningDialog = WarningDialog.newInstance(
                    WarningEnum.UNSAVED_POST, this);

            warningDialog.show(getFragmentManager()
                    .beginTransaction(), "Unsaved Post Warning");
        }

        return true;
    }

    public String getDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE d MMMM yyyy hh:mm aaa");
        return dateFormat.format(date);
    }
}