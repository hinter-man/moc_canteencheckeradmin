package com.example.canteenchecker.canteenmanager.ui;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

        return view;
    }

    public void SetCanteenData(Canteen canteen) {
        this.canteen = canteen;
        // fill view with canteen data
        edtCanteenName.setText(canteen.getName());
        edtMenu.setText(canteen.getSetMeal());
        edtMenuPrice.setText(NumberFormat.getNumberInstance().format(canteen.getSetMealPrice()));
        edtAddress.setText(canteen.getLocation());
        edtHomepage.setText(canteen.getWebsite());
        edtPhoneNo.setText(canteen.getPhoneNumber());
        seekBarWaitingTime.setProgress(canteen.getAverageWaitingTime());
        txvSeekBarWaitingTimeValue.setText(canteen.getAverageWaitingTime() + " mins");
    }
}
