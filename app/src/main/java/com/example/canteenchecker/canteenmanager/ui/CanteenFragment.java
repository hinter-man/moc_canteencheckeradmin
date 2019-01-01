package com.example.canteenchecker.canteenmanager.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.canteenchecker.canteenmanager.R;
import com.example.canteenchecker.canteenmanager.domain.Canteen;
import com.example.canteenchecker.canteenmanager.proxy.ServiceProxy;

import java.io.IOException;
import java.text.NumberFormat;

public class CanteenFragment extends Fragment {

    private static final String TAG = CanteenFragment.class.toString();

    private Canteen canteen;

    private EditText edtCanteenName;
    private EditText edtMenu;
    private EditText edtMenuPrice;
    private EditText edtAddress;
    private EditText edtHomepage;
    private EditText edtPhoneNo;
    private SeekBar seekBarWaitingTime;
    private TextView txvSeekBarWaitingTimeValue;
    private FloatingActionButton btnUpdateCanteen;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_canteen, container,false);

        edtCanteenName = view.findViewById(R.id.edt_canteen_name);
        edtMenu = view.findViewById(R.id.edt_menu);
        edtMenuPrice = view.findViewById(R.id.edt_menu_price);
        edtAddress = view.findViewById(R.id.edt_address);
        edtHomepage = view.findViewById(R.id.edt_homepage);
        edtPhoneNo = view.findViewById(R.id.edt_phoneNo);
        txvSeekBarWaitingTimeValue = view.findViewById(R.id.txv_seekBar_value);
        seekBarWaitingTime = view.findViewById(R.id.sb_waiting_time);
        seekBarWaitingTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int waitingTime = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                waitingTime = progress;
                txvSeekBarWaitingTimeValue.setText(waitingTime + " mins");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // nothing to do here
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txvSeekBarWaitingTimeValue.setText(waitingTime + " mins");
            }
        });
        btnUpdateCanteen = view.findViewById(R.id.btn_update_canteen);
        btnUpdateCanteen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCanteen();
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private void updateCanteen() {
        new AsyncTask<Object, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                setUiEnabled(false);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                setUiEnabled(true);
                if (result) {
                    Toast.makeText(getContext(), R.string.canteen_updated_msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.canteen_update_failed_msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected Boolean doInBackground(Object... objects) {
                try {
                    return new ServiceProxy().updateCanteen(
                            (int)objects[0],
                            (String)objects[1],
                            (String)objects[2],
                            (float)objects[3],
                            (String)objects[4],
                            (String)objects[5],
                            (String)objects[6],
                            (float)objects[7],
                            (int)objects[8]
                    );
                } catch (IOException e) {
                    Log.e(TAG, getString(R.string.canteen_update_failed_msg), e);
                    return false;
                }
            }
        }.execute(
                Integer.parseInt(canteen.getId()),
                edtCanteenName.getText().toString(),
                edtMenu.getText().toString(),
                Float.parseFloat(edtMenuPrice.getText().toString()),
                edtHomepage.getText().toString(),
                edtPhoneNo.getText().toString(),
                edtAddress.getText().toString(),
                canteen.getAverageRating(),
                seekBarWaitingTime.getProgress());
    }

    public void setCanteenData(Canteen canteen) {
        this.canteen = canteen;
        // fill view with canteen data
        edtCanteenName.setText(canteen.getName());
        edtMenu.setText(canteen.getSetMeal());
        edtMenuPrice.setText(NumberFormat.getNumberInstance().format(canteen.getSetMealPrice()));
        edtAddress.setText(canteen.getLocation());
        edtHomepage.setText(canteen.getWebsite());
        edtPhoneNo.setText(canteen.getPhoneNumber());
        seekBarWaitingTime.setProgress(canteen.getAverageWaitingTime());
        txvSeekBarWaitingTimeValue.setText(String.format("%d min", canteen.getAverageWaitingTime()));
    }


    private void setUiEnabled(boolean enabled) {
        edtCanteenName.setEnabled(enabled);
        edtMenu.setEnabled(enabled);
        edtMenuPrice.setEnabled(enabled);
        edtAddress.setEnabled(enabled);
        edtHomepage.setEnabled(enabled);
        edtPhoneNo.setEnabled(enabled);
        seekBarWaitingTime.setEnabled(enabled);
        txvSeekBarWaitingTimeValue.setEnabled(enabled);
        btnUpdateCanteen.setEnabled(enabled);
    }
}
