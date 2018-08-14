package com.fithub.codekienmee.fithub;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public abstract class ListFragment extends Fragment {

    protected RecyclerView postRecyclerView; //RecyclerView that handles displaying of posts
    protected PostAdapter postAdapter;
    protected List<FitPost> postList;
    protected List<String> postKeyList;

    /**
     * Inner class that extends the use of ViewHolder.
     * Holds the view for each post.
     */
    protected class PostHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

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
            this.date.setText(post.getDate());
            this.numDislikes.setText(Integer.toString(post.getNumDislikes()));
            this.numLikes.setText(Integer.toString(post.getNumLikes()));

            ForumFragment.setLikesColor(this.thumbsUp, this.thumbsDown,
                    this.post.getNumLikes(), this.post.getNumDislikes());

            if (((MainPageActivity) getActivity()).hasUser()) {
                FitUser user = ((MainPageActivity) getActivity()).getUser();

                if (user.getFavouritePostKeys() != null &&
                        user.getFavouritePostKeys().contains(post.getPostKey())) {
                    ((ImageView) itemView.findViewById(R.id.post_forum_favourite_img))
                            .setImageResource(R.drawable.ic_favourite_color);
                } else {
                    ((ImageView) itemView.findViewById(R.id.post_forum_favourite_img))
                            .setImageResource(R.drawable.ic_favourites_mono);
                }
            }
        }

        @Override
        public void onClick(View v) {
            onPostClick(this.post);
        }
    }

    /**
     * Inner class that extends the use of Adapter.
     * Connects the respective ViewHolder class to the RecyclerView
     */
    protected class PostAdapter extends RecyclerView.Adapter<PostHolder> {

        private List<String> postKeyList; // Maintain list of keys
        private DatabaseReference postDB;

        public PostAdapter(List<String> postKeyList) {
            this.postKeyList = postKeyList;

            this.postDB = FirebaseDatabase.getInstance().getReference("FitPosts");
        }

        @Override
        public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new PostHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(final PostHolder holder, final int position) {
//            final FitPost post = this.postListInner.get(position);
            if (this.postKeyList != null) {
                String postKey = this.postKeyList.get(position);
                this.postDB.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        FitPost post = dataSnapshot.getValue(FitPost.class);
                        holder.bindPost(post);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        @Override
        public int getItemCount() {
             return (this.postKeyList == null) ? 0 : this.postKeyList.size();
        }

        /**
         * Custom implementation of notifySetDataChanged() method.
         */
        protected void notifyAdapterSetDataChanged() {
            this.notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }

    /**
     * Method to set sliding animation for fragments.
     */
    public static void setSlideAnim(int resource, Fragment fragment) {
        Transition slideAnim = new Slide(resource).setDuration(200);
        fragment.setEnterTransition(slideAnim);
        fragment.setExitTransition(slideAnim);
    }

    /**
     * Abstract method that determines action taken when a post is clicked.
     */
    public abstract void onPostClick(FitPost post);
}