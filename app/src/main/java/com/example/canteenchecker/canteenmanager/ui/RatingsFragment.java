package com.example.canteenchecker.canteenmanager.ui;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.domain.Rating;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;
import com.example.canteenchecker.canteenmanager.ui.helper.DeleteRatingDialogFragment;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class RatingsFragment extends Fragment{

    private static final String TAG = RatingsFragment.class.toString();

    private final RatingsAdapter ratingsAdapter = new RatingsAdapter();
    private SwipeRefreshLayout srlRatings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ratings, container,false);

        RecyclerView rcvRatings = view.findViewById(R.id.rcv_ratings);
        rcvRatings.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvRatings.setAdapter(ratingsAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rcvRatings);

        srlRatings = view.findViewById(R.id.srl_ratings);
        srlRatings.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateRatings();
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private void updateRatings() {
        new AsyncTask<Void, Void, Collection<Rating>>() {
            @Override
            protected Collection<Rating> doInBackground(Void... voids) {
                try {
                    return new ServiceProxy().getCanteen().getRatings();
                } catch (IOException e) {
                    Log.e(TAG, getString(R.string.ratings_failed_msg), e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Collection<Rating> ratings) {
                if (ratings != null) {
                    ratingsAdapter.displayRatings(ratings);
                }
                srlRatings.setRefreshing(false);
            }
        }.execute();

    }

    public void setRatings(Collection<Rating> ratings) {
        ratingsAdapter.displayRatings(ratings);
    }

    private static class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.ViewHolder> {

        private List<Rating> ratings = new ArrayList<>();
        private final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm");


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
            Date timestamp = new Date(r.getTimestamp());
            holder.txvRatingTimestamp.setText(dateFormat.format(timestamp));
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

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        // we want to cache these and not allocate anything repeatedly in the onChildDraw method
        Drawable background;
        Drawable xMark;
        int xMarkMargin;
        boolean initiated;
        private void init() {
            background = new ColorDrawable(Color.RED);
            xMark = ContextCompat.getDrawable(RatingsFragment.this.getContext(), R.drawable.ic_delete_black_24dp);
            xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            xMarkMargin = (int) RatingsFragment.this.getResources().getDimension(R.dimen.distance_small);
            initiated = true;
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            int actualAdapterPosition = viewHolder.getAdapterPosition();

            // get rating id for deletion
            int ratingId = ratingsAdapter.ratings.get(actualAdapterPosition).getRatingId();
            DeleteRatingDialogFragment deleteRatingDialogFragment = new DeleteRatingDialogFragment();
            // put data into dialog fragment
            Bundle bundle = new Bundle();
            bundle.putInt(DeleteRatingDialogFragment.getRatingIdBundleKey(), ratingId);
            deleteRatingDialogFragment.setArguments(bundle);
            deleteRatingDialogFragment.show(getFragmentManager(), DeleteRatingDialogFragment.class.toString());
            ratingsAdapter.notifyItemChanged(actualAdapterPosition);
        }


        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;

            if (viewHolder.getAdapterPosition() == -1) {
                // not interested in those
                return;
            }

            if (!initiated) {
                init();
            }

            // draw red background
            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            background.draw(c);

            // draw x mark
            int itemHeight = itemView.getBottom() - itemView.getTop();
            int intrinsicWidth = xMark.getIntrinsicWidth();
            int intrinsicHeight = xMark.getIntrinsicWidth();

            int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
            int xMarkRight = itemView.getRight() - xMarkMargin;
            int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
            int xMarkBottom = xMarkTop + intrinsicHeight;
            xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

            xMark.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
}
