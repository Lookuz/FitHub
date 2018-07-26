package com.fithub.codekienmee.fithub;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.file.attribute.DosFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class SuggestionsAdapter extends ArrayAdapter<FitLocation> {

    private List<FitLocation> locationList; // List that stores full list of locations.

    /**
     * Class that performs filtering of locations.
     */
    private Filter locationFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            final List<FitLocation> suggestionsList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(constraint == null || constraint.length() == 0) {
                suggestionsList.addAll(locationList);
            } else {
                final String expression = constraint.toString()
                        .toLowerCase()
                        .trim();

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("FitLocations");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            FitLocation location = ds.getValue(FitLocation.class);
                            if (location.getLocationName()
                                    .toLowerCase()
                                    .trim()
                                    .contains(expression)) {
                                suggestionsList.add(ds.getValue(FitLocation.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

//                for (FitLocation location : locationList) {
//                    if (location.getLocationName()
//                            .toLowerCase()
//                            .trim()
//                            .contains(expression))
//                        suggestionsList.add(location);
//                    Log.d("SuggestionsAdapter: ", "Adding Location " + location.getLocationName());
//                }
            }
            results.values = suggestionsList;
            results.count = suggestionsList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results.values != null) {
                addAll((List) results.values);
            }
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((FitLocation) resultValue).getLocationName();
        }
    };

    public SuggestionsAdapter(@NonNull Context context, @NonNull List<FitLocation> objects) {
        super(context, 0, objects);
        this.locationList = new ArrayList<>(objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.suggestions_view , parent, false);
        }

        TextView locationTitle = (TextView) convertView.findViewById(R.id.suggestions_title);
        TextView locationContent = (TextView) convertView.findViewById(R.id.suggestions_content);
        FitLocation location = this.getItem(position);

        if (location != null) {
            locationTitle.setText(location.getLocationName());
            locationContent.setText(location.getLocationAddress());
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return this.locationFilter;
    }

    @Override
    public void add(@Nullable FitLocation object) {
        super.add(object);
        this.locationList.add(object);
    }
}
