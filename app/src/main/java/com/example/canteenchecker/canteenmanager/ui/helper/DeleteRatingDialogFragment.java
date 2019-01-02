package com.example.canteenchecker.canteenmanager.ui.helper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;

import java.io.IOException;

public class DeleteRatingDialogFragment extends DialogFragment{
    private static final String TAG = DeleteRatingDialogFragment.class.toString();
    private static final String RATING_ID_BUNDLE_KEY = "ratingId";
    private Context context;

    // for put data in bundle from outside
    public static String getRatingIdBundleKey() {
        return RATING_ID_BUNDLE_KEY;
    }

    Bundle bundle;
    int ratingId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        bundle = getArguments();
        if (bundle != null) {
            ratingId = getArguments().getInt(RATING_ID_BUNDLE_KEY);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle("Delete rating?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        new AsyncTask<Integer, Void, Boolean>() {
                            @Override
                            protected void onPostExecute(Boolean deleted) {
                                if (deleted) {
                                    // Send the positive button event back to the host activity
                                    Toast.makeText(context,
                                            "Rating deleted", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context,
                                            "Rating not deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            protected Boolean doInBackground(Integer... integers) {
                                try {
                                    return new ServiceProxy().deleteRating(integers[0]);
                                } catch (IOException e) {
                                    Log.e(TAG, String.format("deletion of %s rating failed", integers[0]),e);
                                    return false;
                                }
                            }
                        }.execute(ratingId);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}
