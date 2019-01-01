package com.example.canteenchecker.canteenmanager.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.domain.Rating;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RatingsFragment extends Fragment {

    private final RatingsAdapter ratingsAdapter = new RatingsAdapter();
    private SwipeRefreshLayout srlRatings;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ratings, container,false);

        RecyclerView rcvRatings = view.findViewById(R.id.rcv_ratings);
        rcvRatings.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvRatings.setAdapter(ratingsAdapter);

        srlRatings = view.findViewById(R.id.srl_ratings);
        srlRatings.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });

        return view;
    }

    private static class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.ViewHolder> {
        private List<Rating> ratings = new ArrayList<>();

        static class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView txvRatingName = itemView.findViewById(R.id.txv_rating_name);
            private final RatingBar rtbRatingPoints = itemView.findViewById(R.id.rtb_rating_points);
            private final TextView txvRatingRemark = itemView.findViewById(R.id.txv_rating_remark);
            private final TextView txvRatingTimestamp = itemView.findViewById(R.id.txv_rating_timestamp);
            ViewHolder(View itemView) { super(itemView); }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // TODO
            View view =
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.layout_ratings_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final Rating r = ratings.get(position);
            holder.rtbRatingPoints.setRating(r.getRatingPoints());
            holder.txvRatingName.setText(r.getUsername());
            holder.txvRatingRemark.setText(r.getRemark());
            holder.txvRatingTimestamp.setText(String.valueOf(r.getTimestamp()));
        }

        @Override
        public int getItemCount() {
            return ratings.size();
        }

        public void displayRatings(Collection<Rating> ratings) {
            this.ratings.clear();
            if (ratings != null) {
                this.ratings.addAll(ratings);
            }
            notifyDataSetChanged();
        }
    }
}
