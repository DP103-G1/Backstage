package com.example.waiter;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.Common;
import com.example.g1.Order;
import com.example.g1.R;
import com.example.manager.table.Table;
import com.example.task.CommonTask;
import com.example.task.ImageTask;
import com.google.gson.JsonObject;

import java.util.List;


public class WaiterMenuDetailFragment extends Fragment {
    private static  final String TAG = "TAG_WaiterMenuDetailFragment";
    private Activity activity;
    private RecyclerView rvMd;
    private Button btBill;
    private List<Order> orders;
    private CommonTask OrderGetAllTask;
    private ImageTask OrderTask;
    private Table table;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        table = (Table)(getArguments() != null ? getArguments().getSerializable("table") : null);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@NonNull ViewGroup container,
                             @NonNull Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_waiter_menu_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvMd = view.findViewById(R.id.rvMd);
        btBill = view.findViewById(R.id.btBill);

        final NavController navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("table") == null) {
            Common.showToast(activity, R.string.textNoOrder);
            navController.popBackStack();
            return;
        }
        table = (Table) bundle.getSerializable("table");
        rvMd.setLayoutManager(new LinearLayoutManager(activity));
//        orders = getorder();
//        showorder(orders);
    }

//    private List<Order> getorder() {
//        List<Order> orders = null;
//        if (Common.networkConnected(activity)) {
//            String url = Common.URL_SERVER + "OrderServlet";
//            JsonObject jsonObject = new JsonObject();
//            jsonObject.addProperty("actiom", "getBytableid");
//            String j
//        }
//    }
//
//    private void showorder(List<Order> orders) {
//    }
}
