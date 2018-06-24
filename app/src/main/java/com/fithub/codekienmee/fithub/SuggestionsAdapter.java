package com.fithub.codekienmee.fithub;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

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

            List<FitLocation> suggestionsList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(constraint == null || constraint.length() == 0) {
                suggestionsList.addAll(locationList);
            }else {
                String expression = constraint.toString()
                        .toLowerCase()
                        .trim();
                for (FitLocation location : locationList) {
                    if (location.getLocationName().toString()
                            .toLowerCase()
                            .trim()
                            .contains(expression))
                        suggestionsList.add(location);
                }
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
}
