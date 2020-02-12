package com.example.waiter;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.Common;
import com.example.g1.Booking;
import com.example.g1.R;
import com.example.task.ImageTask;

import java.text.SimpleDateFormat;


public class WaiterSelectDetailFragment extends Fragment {
    private final static String TAG = "TAG_WaiterSelectBookingDetailFragment";
    private Activity activity;
    private TextView tvBkIdGet,tvTableGet,tvTimeGet,
            tvDateGet,tvChildGet,tvAdultGet,tvPhoneGet;
    private Booking waiterSelectBookingDetail;
    private Button btBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_waiter_select_detail, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvBkIdGet = view.findViewById(R.id.tvBkIdGet);
        tvTableGet = view.findViewById(R.id.tvTableGet);
        tvTimeGet = view.findViewById(R.id.tvTimeGet);
        tvDateGet = view.findViewById(R.id.tvDateGet);
        tvChildGet = view.findViewById(R.id.tvChildGet);
        tvAdultGet = view.findViewById(R.id.tvAdultGet);
        tvPhoneGet = view.findViewById(R.id.tvPhoneGet);
        btBack = view.findViewById(R.id.btBack);
        Bundle bundle = getArguments();
        if (bundle != null){
            waiterSelectBookingDetail = (Booking)bundle.getSerializable("booking");
            showWaiterSelectBookinDetail();
        }
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_waiterSelectDetailFragment_to_waiterSelectFragment);

            }
        });
    }

    private void showWaiterSelectBookinDetail() {
        String url = Common.URL_SERVER + "BookingServlet";
        String memId = waiterSelectBookingDetail .getBkId();
        Bitmap bitmap = null;
        try {
            bitmap = new ImageTask(url,memId).execute().get();
        }catch (Exception e){
            Log.e(TAG,e.toString());
        }

        tvBkIdGet.setText(waiterSelectBookingDetail.getBkId());
        tvTableGet.setText(waiterSelectBookingDetail.getTableId());
        tvTimeGet.setText(waiterSelectBookingDetail.getBkTime());
        String date = new SimpleDateFormat("yyyy-MM-dd").format(waiterSelectBookingDetail.getBkDate());
        tvDateGet.setText(date);
        tvChildGet.setText(waiterSelectBookingDetail.getBkChild());
        tvAdultGet.setText(waiterSelectBookingDetail.getBkAdult());
        tvPhoneGet.setText(waiterSelectBookingDetail.getBkPhone());

    }
}
