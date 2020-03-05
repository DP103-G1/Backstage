package com.example.kitchen;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.Common;
import com.example.g1.R;
import com.example.task.CommonTask;
import com.example.task.ImageTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


public class KitchenFragment extends Fragment {
    private static final String TAG = "TAG_KitchFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvKitch;
    private Activity activity;
    private CommonTask kitchGetAllTask;
    private ImageTask kitchImageTask;
    private List<MenuDetail> menuDetails;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_kitchen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final NavController navController = Navigation.findNavController(view);
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvKitch = view.findViewById(R.id.rvKitch);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                menuDetails = getMenuDetail();
                showMenuDetail(menuDetails);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        rvKitch.setLayoutManager(new LinearLayoutManager(activity));
        menuDetails = getMenuDetail();
        showMenuDetail(menuDetails);
    }

    private List<MenuDetail> getMenuDetail() {
        List<MenuDetail> menuDetails = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "MenuDetailServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            kitchGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = kitchGetAllTask.execute().get();
                Type listType = new TypeToken<List<MenuDetail>>() {
                }.getType();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                menuDetails = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return menuDetails;
    }

    private void showMenuDetail(List<MenuDetail> menuDetails) {
        if (menuDetails == null || menuDetails.isEmpty()) {
            Common.showToast(activity, R.string.textNOMenu);
        }
        MenuDetailAdapter menuDetailAdapter = (MenuDetailAdapter) rvKitch.getAdapter();

        if (menuDetailAdapter == null) {
            rvKitch.setAdapter(new MenuDetailAdapter(activity, menuDetails));
        } else {
            menuDetailAdapter.setMenuDetail(menuDetails);
            menuDetailAdapter.notifyDataSetChanged();
        }
    }

    private class MenuDetailAdapter extends RecyclerView.Adapter<MenuDetailAdapter.MyviewHolder> {
        private LayoutInflater layoutInflater;
        private List<MenuDetail> menuDetails;

        MenuDetailAdapter(Context context, List<MenuDetail> menuDetails) {
            layoutInflater = LayoutInflater.from(context);
            this.menuDetails = menuDetails;
        }

        void setMenuDetail(List<MenuDetail> menuDetails) {
            this.menuDetails = menuDetails;
        }

        class MyviewHolder extends RecyclerView.ViewHolder {
            TextView tvTableId, tvFoodName, tvFoodAmount;
            Button btStatus;
            boolean status;
            int ordid, foodamount, total;
            String menuid;

            MyviewHolder(@NonNull View itemView) {
                super(itemView);
                tvTableId = itemView.findViewById(R.id.tvTableId);
                tvFoodName = itemView.findViewById(R.id.tvFoodName);
                tvFoodAmount = itemView.findViewById(R.id.tvFoodAmount);
                btStatus = itemView.findViewById(R.id.btStatus);
                btStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!status) {
                            status = true;
                            if (Common.networkConnected(activity)) {
                                String url = Common.URL_SERVER + "MenuDetailServlet";
                                MenuDetail menuDetail = new MenuDetail(ordid, menuid, foodamount, total, status);
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("action", "update");
                                jsonObject.addProperty("menuDetail", new Gson().toJson(menuDetail));
                                int count = 0;
                                try {
                                    String result = new CommonTask(url, jsonObject.toString()).execute().get();
                                    count = Integer.valueOf(result);
                                } catch (Exception e) {
                                    Log.e(TAG, e.toString());
                                }
                                if (count != 0){
                                    Common.showToast(getActivity(), R.string.textUpdateSuccess);
                                    btStatus.setBackgroundColor(Color.parseColor("#424242"));
                                    btStatus.setText("已出餐");
                                } else {
                                    Common.showToast(getActivity(), R.string.textUpdateFail);
                                }
                            }
                        } else {
                            Common.showToast(activity, R.string.falseStatus);
                        }
                    }
                });
            }
            public void setOrdId(int ordId){
                this.ordid = ordId;
            }
            public void setMenuId(String menuid){
                this.menuid = menuid;
            }
            public void SetStatus(boolean status) {
                this.status = status;
            }
            public void setAmount(int foodamount) {
                this.foodamount = foodamount;
            }
            public void setTotal(int total) {
                this.total = total;
            }
        }

        @Override
        public int getItemCount() {
            return menuDetails.size();
        }

        @NonNull
        @Override
        public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_kitch, parent, false);
            return new MyviewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {
            final MenuDetail menuDetail = menuDetails.get(position);
            String url = Common.URL_SERVER + "MenuDetailServlet";
            String id = menuDetail.getMENU_ID();
            kitchImageTask = new ImageTask(url, id);
            kitchImageTask.execute();
            holder.tvFoodName.setText(menuDetail.getFOOD_NAME());
            holder.tvTableId.setText(String.valueOf(menuDetail.getTABLE_ID()));
            holder.tvFoodAmount.setText(String.valueOf(menuDetail.getFOOD_AMOUNT()));
            holder.setOrdId(menuDetail.getORD_ID());
            holder.setMenuId(menuDetail.getMENU_ID());
            holder.setAmount(menuDetail.getFOOD_AMOUNT());
            holder.setTotal(menuDetail.getTOTAL());
            holder.SetStatus(menuDetail.isFOOD_STATUS());
            if (menuDetail.isFOOD_STATUS()) {
                holder.btStatus.setText("已出餐");
                holder.btStatus.setBackgroundColor(Color.parseColor("#424242"));
            } else {
                holder.btStatus.setText("未出餐");
                holder.btStatus.setBackgroundColor(Color.parseColor("#222222"));
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (kitchGetAllTask != null) {
            kitchGetAllTask.cancel(true);
            kitchGetAllTask = null;
        }

        if (kitchImageTask != null) {
            kitchImageTask.cancel(true);
            kitchImageTask = null;
        }
    }
}

