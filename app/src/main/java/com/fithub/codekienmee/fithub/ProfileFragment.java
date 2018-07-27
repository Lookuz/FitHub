package com.fithub.codekienmee.fithub;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Fragment that displays the profile page of current signed in FitUser.
 */
public class ProfileFragment extends Fragment {
    
    private FitUser user;

    private EditText name;
    private EditText bio;
    private ImageButton editProfile;
    private RecyclerView timelineView;
    private TimelineAdapter timelineAdapter;

    /**
     * ViewHolder for loading each timeline card.
     */
    private class TimelineHolder extends RecyclerView.ViewHolder {

        ImageView timelineIcon;
        TextView timelineLog;
        LinearLayout timelineBackgroundTop;
        LinearLayout timelineBackgroundBottom;

        public TimelineHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.timeline_card, parent, false));

            this.timelineIcon = itemView.findViewById(R.id.timeline_icon);
            this.timelineLog = itemView.findViewById(R.id.timeline_log);
            this.timelineBackgroundTop = itemView.findViewById(R.id.timeline_background_top);
            this.timelineBackgroundBottom = itemView.findViewById(R.id.timeline_background_bottom);
        }

        public LinearLayout getTimelineBackgroundTop() {
            return timelineBackgroundTop;
        }

        public LinearLayout getTimelineBackgroundBottom() {
            return timelineBackgroundBottom;
        }

        public void loadCard(String message) {
            this.timelineLog.setText(message);
        }
    }

    /**
     * Adapter for loading timeline logs into the RecyclerView.
     */
    private class TimelineAdapter extends RecyclerView.Adapter<TimelineHolder> {

        private List<String> timelineLogs;

        public TimelineAdapter(List<String> timelineLogs) {
            this.timelineLogs = timelineLogs;
        }

        @Override
        public TimelineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            return new TimelineHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(TimelineHolder holder, int position) {
            String timelineLog = this.timelineLogs.get(position);
            holder.loadCard(timelineLog);

            Drawable drawable;
            if (this.timelineLogs.size() == 1) {
                return;
            } else if (position == 0) {
                drawable = getResources().getDrawable(R.drawable.timeline_line_top);
                holder.getTimelineBackgroundBottom().setBackground(drawable);
            } else if (position == this.timelineLogs.size() - 1) {
                drawable = getResources().getDrawable(R.drawable.timeline_line_bottom);
                holder.getTimelineBackgroundTop().setBackground(drawable);
            } else {
                holder.getTimelineBackgroundTop().setBackground(getResources()
                        .getDrawable(R.drawable.timeline_line_bottom));
                holder.getTimelineBackgroundBottom().setBackground(getResources()
                        .getDrawable(R.drawable.timeline_line_top));
            }
        }

        @Override
        public int getItemCount() {
            return (this.timelineLogs == null)? 0 : this.timelineLogs.size();
        }
    }

    public static ProfileFragment newInstance(FitUser user) {

        Bundle args = new Bundle();
        
        ProfileFragment fragment = new ProfileFragment();
        fragment.user = user;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        user.updateStatistics();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        this.initView(view);

        return view;
    }

    private void initView(View view) {
        this.name = view.findViewById(R.id.profile_user_name);
        this.bio = view.findViewById(R.id.profile_user_bios);
        this.editProfile = view.findViewById(R.id.profile_edit);

        this.timelineView = view.findViewById(R.id.profile_timeline);
        this.timelineAdapter = new TimelineAdapter(user.getTimeline());
        this.timelineView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.timelineView.setAdapter(this.timelineAdapter);

        this.name.setText(user.getName());
        if (this.user.getBio() != null) {
            this.bio.setText(this.user.getBio());
        }
        this.name.setEnabled(false);
        this.bio.setEnabled(false);
        this.editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (name.isEnabled() && bio.isEnabled()) {
                    name.setEnabled(false);
                    name.setBackground(getResources().getDrawable(R.color.transparent));
                    bio.setEnabled(false);
                    bio.setTextColor(getResources().getColor(R.color.white));
                    bio.setBackground(getResources().getDrawable(R.color.transparent));
                    ProfileManager.updateProfile(user, name.getText().toString(),bio.getText().toString());
                } else {
                    name.setEnabled(true);
                    name.setBackground(getResources().getDrawable(R.drawable.post_view_background));
                    bio.setEnabled(true);
                    bio.setTextColor(getResources().getColor(R.color.black_translucent));
                    bio.setBackground(getResources().getDrawable(R.drawable.post_view_background));
                    // TODO: bring up other fields for editting (password, etc)
                }
            }
        });
    }
}
