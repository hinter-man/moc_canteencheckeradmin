package com.example.canteenchecker.canteenmanager.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.domain.Canteen;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;
import com.example.canteenchecker.canteenmanager.ui.helper.SectionsPageAdapter;

import java.io.IOException;

/**
 * handles tablayout fragments
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.toString();

    private SectionsPageAdapter sectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
    private ViewPager viewPager;

    private CanteenFragment canteenFragment = new CanteenFragment();
    private RatingsFragment ratingsFragment = new RatingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        viewPager = findViewById(R.id.container);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        GetCanteenData();
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter =
                new SectionsPageAdapter(getSupportFragmentManager());

        adapter.addFragment(canteenFragment, getString(R.string.canteen_fragment_title));
        adapter.addFragment(ratingsFragment, getString(R.string.ratings_fragment_title));

        viewPager.setAdapter(adapter);
    }

    @SuppressLint("StaticFieldLeak")
    private void GetCanteenData() {
        new AsyncTask<Void, Void, Canteen>() {
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setTitle("Fetching data for you...");
                progressDialog.show();
            }

            @Override
            protected Canteen doInBackground(Void... voids) {
                try {
                    return new ServiceProxy().getCanteen();
                } catch (IOException e) {
                    Log.e(TAG, getString(R.string.fetch_canteenData_failed), e);
                    Toast.makeText(MainActivity.this, R.string.fetch_canteenData_failed, Toast.LENGTH_SHORT).show();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Canteen canteen) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (canteen != null) {
                    canteenFragment.setCanteenData(canteen);
                }
            }
        }.execute();
    }


}
