package com.fithub.codekienmee.fithub;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import javax.security.auth.callback.Callback;

/**
 * Class that displays the functionality of fragment show when creating a new FitPost
 */
public class PostFragment extends Fragment {

    private PostCallBack callBack;
    private FitPost post;

    private EditText content;
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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        this.initView(view);
        return view;
    }

    /**
     * Method to initialize the widgets and views in PostFragment layout.
     */
    private void initView(View view) {
        this.content = view.findViewById(R.id.post_content);
        this.author = view.findViewById(R.id.post_author);
        this.submit = view.findViewById(R.id.post_submit);
        this.profilePic = view.findViewById(R.id.post_profile_pic);

        this.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onDestroy() {
        if(post != null)
            this.callBack.onCallBack(this.post);
        super.onDestroy();
    }
}
