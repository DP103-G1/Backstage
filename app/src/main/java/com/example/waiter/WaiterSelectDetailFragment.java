package com.example.waiter;


import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.main.Common;
import com.example.main.Booking;
import com.example.main.R;
import com.example.main.Url;
import com.example.manager.member.Member;
import com.example.socket.SocketMessage;
import com.example.task.CommonTask;
import com.example.task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;


public class WaiterSelectDetailFragment extends Fragment {
    private final static String TAG = "TAG_WaiterSelectBookingDetailFragment";
    private Activity activity;
    private TextView tvBkIdGet, tvTableGet, tvTimeGet,
            tvDateGet, tvChildGet, tvAdultGet, tvPhoneGet;
    private Booking waiterSelectBookingDetail;
    private Button btBack, btIn;

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

        Bundle bundle = getArguments();
        if (bundle != null) {
            waiterSelectBookingDetail = (Booking) bundle.getSerializable("booking");
            showWaiterSelectBookingDetail();
        }
        int member_id = waiterSelectBookingDetail.getMember().getmember_Id();
        int state = waiterSelectBookingDetail.getMember().getState();
        if (state == 0) {
            state = 1;
        }

        btIn = view.findViewById(R.id.btIn);
        int finalState = state;
        btIn.setOnClickListener(v -> {
            if (Common.networkConnected(activity)) {
                String url = Url.URL_SERVER + "/MembersServlet";
                String bookingUrl = Url.URL_SERVER + "BookingServlet";
                Member member = new Member(member_id, finalState);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("action", "updateState");
                jsonObject.addProperty("member", new Gson().toJson(member));
                JsonObject bookingJsonObject = new JsonObject();
                waiterSelectBookingDetail.setStatus(2);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                bookingJsonObject.addProperty("action", "update");
                bookingJsonObject.addProperty("booking", gson.toJson(waiterSelectBookingDetail));
                int count = 0;
                try {
                    String rs = new CommonTask(url, jsonObject.toString()).execute().get();
                    String bookingResult = new CommonTask(bookingUrl, bookingJsonObject.toString()).execute().get();
                    count = Integer.valueOf(rs) + Integer.parseInt(bookingResult);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (count != 2) {
                    Common.showToast(getActivity(), R.string.textUpdateFail);
                } else {
                    SocketMessage socketMessage = new SocketMessage("seat", "member" + member_id, "");
                    Common.eZeatsWebSocketClient.send(new Gson().toJson(socketMessage));
                    Common.showToast(getActivity(), R.string.textUpdateSuccess);
                    Navigation.findNavController(v).popBackStack();
                }
            } else {
                Common.showToast(getActivity(), R.string.textNoNetwork);
            }
        });
    }

    private void showWaiterSelectBookingDetail() {
        tvBkIdGet.setText(String.valueOf(waiterSelectBookingDetail.getBkId()));
        tvTableGet.setText(String.valueOf(waiterSelectBookingDetail.getTableId()));
        tvTimeGet.setText(waiterSelectBookingDetail.getBkTime());
        String date = new SimpleDateFormat("yyyy-MM-dd").format(waiterSelectBookingDetail.getBkDate());
        tvDateGet.setText(date);
        tvChildGet.setText(waiterSelectBookingDetail.getBkChild());
        tvAdultGet.setText(waiterSelectBookingDetail.getBkAdult());
        tvPhoneGet.setText(waiterSelectBookingDetail.getBkPhone());

    }
}
