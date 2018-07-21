package com.fithub.codekienmee.fithub;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Fragment that displays the profile page of current signed in FitUser.
 */
public class ProfileFragment extends Fragment {
    
    private FitUser user;

    private TextView name;
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
        this.timelineView = view.findViewById(R.id.profile_timeline);
        this.timelineAdapter = new TimelineAdapter(user.getTimeline());
        this.timelineView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.timelineView.setAdapter(this.timelineAdapter);

        this.name.setText(user.getName());
    }
}
