package com.example.waiter;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.Common;
import com.example.g1.Booking;
import com.example.g1.R;
import com.example.task.CommonTask;
import com.example.task.ImageTask;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class WaiterSelectFragment extends Fragment {
    private static final String TAG = "TAG_WaiterSelectFragment";
    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvWaiterSelectBooking;
    private List<Booking> waiterSelectBookings;
    private CommonTask waiterSelectBookingGetAllTask;
    private ImageTask waiterSelectBookingTask;
    private String bkId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waiter_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchView searchView = view.findViewById(R.id.seachView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvWaiterSelectBooking = view.findViewById(R.id.rvWaiterSelect);
        rvWaiterSelectBooking.setLayoutManager(new LinearLayoutManager(activity));
        waiterSelectBookings = getWaiterSelectBooking();
        showWaiterSelectBooking(waiterSelectBookings);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showWaiterSelectBooking(waiterSelectBookings);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText.isEmpty()) {
                    showWaiterSelectBooking(waiterSelectBookings);
                } else {
                    List<Booking> searchBookings = new ArrayList<>();

                    for (Booking searchBooking : waiterSelectBookings) {
                        if (searchBooking.getBkId().contains(newText)) {
                            searchBookings.add(searchBooking);
                        }
                    }
                    showWaiterSelectBooking(searchBookings);
                }

                return true;
            }
        });
    }




    private class WaiterSelectBookingAdapter extends RecyclerView.Adapter<WaiterSelectBookingAdapter.WaiterSelectBookingHolder>{
        private LayoutInflater layoutInflater;
        private List<Booking> waiterSelectBooking;

            WaiterSelectBookingAdapter(Context context,List<Booking> waiterSelectBookin){
            layoutInflater = LayoutInflater.from(context);
            this.waiterSelectBooking = waiterSelectBookin;
        }

        void setWaiterSelectBookin(List<Booking> waiterSelectBookin){
            this.waiterSelectBooking = waiterSelectBookin;
        }


        class WaiterSelectBookingHolder extends RecyclerView.ViewHolder {
            TextView tvBkId,tvBkDate;

           WaiterSelectBookingHolder(@NonNull View view) {
                super(view);
                tvBkDate = view.findViewById(R.id.tvMemberName);
                tvBkId = view.findViewById(R.id.tvMemberId);
            }
        }


        @Override
        public int getItemCount() {
            return waiterSelectBooking.size();
        }

        @NonNull
        @Override
        public WaiterSelectBookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.item_view_select,parent,false);
            return new WaiterSelectBookingHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WaiterSelectBookingHolder holder, int position) {
            final Booking booking = waiterSelectBooking.get(position);
            String url = Common.URL_SERVER + "/BookingServlet";
            String bkId = booking.getBkId();
            waiterSelectBookingTask = new ImageTask(url,bkId);
            waiterSelectBookingTask.execute();
            holder.tvBkId.setText(booking.getBkId());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            holder.tvBkDate.setText(simpleDateFormat.format(booking.getBkDate()));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("booking",booking);
                    Navigation.findNavController(v).navigate(R.id.action_waiterSelectFragment_to_waiterSelectDetailFragment,bundle);
                }
            });
        }

    }



    private List<Booking> getWaiterSelectBooking() {
        List<Booking> waiterSelectBooking = null;
        if (Common.networkConnected(activity)){
            String url = Common.URL_SERVER + "/BookingServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","getAll");
            String jsonOut = jsonObject.toString();
            waiterSelectBookingGetAllTask = new CommonTask(url,jsonOut);
            try {
                String jsonIn = waiterSelectBookingGetAllTask.execute().get();
                Type listType = new TypeToken<List<Booking>>(){

                }.getType();
                waiterSelectBooking = Common.gson.fromJson(jsonIn,listType);
            }catch (Exception e){
                Log.e(TAG,e.toString());
            }
        }else {
            Common.showToast(activity,R.string.textNoNetwork);
        }
        return waiterSelectBooking;
    }

    private void showWaiterSelectBooking(List<Booking> waiterSelectBooking) {
        if (waiterSelectBooking == null || waiterSelectBooking.isEmpty()){
            Common.showToast(activity,R.string.textNoSelectBookingFound);
        }
        WaiterSelectBookingAdapter waiterSelectBookingAdapter = (WaiterSelectBookingAdapter) rvWaiterSelectBooking.getAdapter();
        if (waiterSelectBookingAdapter == null){
            rvWaiterSelectBooking.setAdapter(new WaiterSelectBookingAdapter(activity,waiterSelectBooking));
        }else {
            waiterSelectBookingAdapter.setWaiterSelectBookin(waiterSelectBooking);
            waiterSelectBookingAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (waiterSelectBookingGetAllTask != null){
            waiterSelectBookingGetAllTask.cancel(true);
            waiterSelectBookingGetAllTask = null;
        }
        if (waiterSelectBookingTask != null){
            waiterSelectBookingTask.cancel(true);
            waiterSelectBookingTask = null;
        }
    }
}
