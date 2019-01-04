package com.example.canteenchecker.canteenmanager.ui;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication;
import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.domain.ReviewData;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Map;
import java.util.TreeMap;

public class StatsActivity extends AppCompatActivity {
    private static final String TAG = StatsActivity.class.toString();

    private final StatsAdapter statsAdapter = new StatsAdapter();

    private ReviewData reviewData;

    private TextView txvTotalRatings;
    private TextView txvAverageRating;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        RecyclerView rcvStats = findViewById(R.id.rcv_stats);
        rcvStats.setLayoutManager(new LinearLayoutManager(this));
        rcvStats.setAdapter(statsAdapter);

        txvAverageRating = findViewById(R.id.txv_average_rating);
        txvTotalRatings = findViewById(R.id.txv_total_ratings);

        updateReviewData();
    }

    @SuppressLint("StaticFieldLeak")
    private void updateReviewData() {
        new AsyncTask<Void, Void, ReviewData>() {
            @Override
            protected void onPostExecute(ReviewData rData) {
                reviewData = rData;
                if (reviewData != null) {
                    statsAdapter.displayStats(reviewData.getCountsPerGrade());
                    txvAverageRating.setText(NumberFormat.getNumberInstance().format(reviewData.getAverageRating()));
                    txvTotalRatings.setText(NumberFormat.getNumberInstance().format(reviewData.getTotalRatings()));
                }
            }

            @Override
            protected ReviewData doInBackground(Void... voids) {
                try {
                    return new ServiceProxy().getReviewData(CanteenManagerApplication.getInstance().getCanteenId().toString());
                } catch (IOException e) {
                    Log.e(TAG, "Review data update failed");
                    return null;
                }
            }
        }.execute();
    }

    private static class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.ViewHolder>{

        private Map<Integer, Integer> countsPerGrade = new TreeMap<>();

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView txvRatingCount = itemView.findViewById(R.id.txv_ratings_count);
            private final RatingBar rtbRatingsCount = itemView.findViewById(R.id.rtb_ratings_count);

            ViewHolder(View itemView) { super(itemView); }
        }

        @Override
        public StatsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_stats_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(StatsAdapter.ViewHolder holder, int position) {
            holder.txvRatingCount.setText(countsPerGrade.get(position+1).toString());
            holder.rtbRatingsCount.setRating(position);
        }

        @Override
        public int getItemCount() {
            return countsPerGrade.size();
        }

        public void displayStats(int[] counts) {

            // convert counts array to map
            this.countsPerGrade.clear();
            if (counts != null) {
                for(int i = counts.length-1; i >= 0; i--) {
                    countsPerGrade.put(i+1, counts[i]);
                }
            }
            notifyDataSetChanged();
        }
    }
}
