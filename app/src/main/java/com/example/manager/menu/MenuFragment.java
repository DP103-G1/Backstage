package com.example.manager.menu;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.main.Common;
import com.example.main.R;
import com.example.main.Url;
import com.example.task.CommonTask;
import com.example.task.ImageTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;




public class MenuFragment extends Fragment {
    private static final String TAG = "TAG_MenuFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvMenu;
    private Activity activity;
    private CommonTask menuGetAllTask;
    private ImageTask menuImageTask;
    private List<Menu> menus;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchView searchView = view.findViewById(R.id.searchView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvMenu = view.findViewById(R.id.rvMenu);


        rvMenu.setLayoutManager(new LinearLayoutManager(activity));
        menus = getMenu();
        showMenu(menus);


        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            menus = getMenu();
            showMenu(menus);
            swipeRefreshLayout.setRefreshing(false);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    showMenu(menus);
                } else {
                    List<Menu> searchMenu = new ArrayList<>();
                    for (Menu menu : menus) {
                        if (menu.getFOOD_NAME().toUpperCase().contains(newText.toUpperCase())) {
                            searchMenu.add(menu);
                        }
                    }
                    showMenu(searchMenu);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

        });

        FloatingActionButton btAdd = view.findViewById(R.id.btAdd);
        btAdd.setOnClickListener(view1 -> Navigation.findNavController(view1)
                .navigate(R.id.action_menuFragment_to_menuInsertFragment));
    }

    private List<Menu> getMenu() {
        List<Menu> menus = null;
        if (Common.networkConnected(activity)) {
            String url = Url.URL_SERVER + "MenuServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            menuGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = menuGetAllTask.execute().get();
                Type listType = new TypeToken<List<Menu>>() {
                }.getType();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                menus = gson.fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
        return menus;
    }

    private void showMenu(List<Menu> menus) {
        if (menus == null || menus.isEmpty()) {
            Common.showToast(activity, R.string.textNOMenu);
        }
             MenuAdapter menuAdapter = (MenuAdapter) rvMenu.getAdapter();

        if (menuAdapter == null) {
            rvMenu.setAdapter(new MenuAdapter(activity, menus));
        } else {
            menuAdapter.setMenus(menus);
            menuAdapter.notifyDataSetChanged();
        }
    }

    private class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Menu> menus;
        private int imageSize;

        MenuAdapter(Context context, List<Menu> menus) {
            layoutInflater = LayoutInflater.from(context);
            this.menus = menus;
            imageSize = getResources().getDisplayMetrics().widthPixels / 4;
        }

        void setMenus(List<Menu> menus) {
            this.menus = menus;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView tvMenuId, tvName, tvPrice, tvContent, tvStatus, tvStatusColor, tvNoStatusColor;


            MyViewHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.ivMenu);
                tvMenuId = itemView.findViewById(R.id.tvMenuId);
                tvName = itemView.findViewById(R.id.tvName);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvContent = itemView.findViewById(R.id.tvContent);
                tvStatus = itemView.findViewById(R.id.tvStatus);
                tvStatusColor = itemView.findViewById(R.id.tvStatusColor);
                tvNoStatusColor = itemView.findViewById(R.id.tvStatusNoColor);

            }

        }

        @Override
        public int getItemCount() {
            return menus.size();
        }

        @NonNull
        @Override
        public MenuAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.item_view_menu, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final Menu menu = menus.get(position);
            holder.itemView.refreshDrawableState();
            String url = Url.URL_SERVER + "MenuServlet";
            String id = menu.getMENU_ID();
            menuImageTask = new ImageTask(url, id, imageSize, holder.imageView);
            menuImageTask.execute();
            holder.tvName.setText(menu.getFOOD_NAME());
            holder.tvPrice.setText(String.valueOf(menu.getFOOD_PRICE()));
            if(menu.getFOOD_STATUS() == 0){
                holder.tvStatus.setText("已下架");
                holder.tvStatus.setTextColor(getResources().getColor(R.color.menuPutColor));
                holder.tvStatusColor.setVisibility(View.GONE);
            } else if(menu.getFOOD_STATUS() == 1){
                holder.tvStatus.setText("上架中");
                holder.tvStatus.setTextColor(getResources().getColor(R.color.menuPutColor));
                holder.tvNoStatusColor.setVisibility(View.GONE);
            } else {
                holder.tvStatusColor.setVisibility(View.GONE);
                holder.tvNoStatusColor.setVisibility(View.GONE);
            }
            holder.itemView.setOnLongClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("menu", menu);
                Navigation.findNavController(v)
                        .navigate(R.id.action_menuFragment_to_dateFragment, bundle);
                return true;
            });
            holder.itemView.setOnClickListener(v -> {
                PopupMenu foodmenu = new PopupMenu(activity, v, Gravity.END);
                foodmenu.inflate(R.menu.foodmenu);
                foodmenu.setOnMenuItemClickListener(item -> {
                    if(item.getItemId() == R.id.update) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("menu", menu);
                        Navigation.findNavController(v)
                                .navigate(R.id.action_menuFragment_to_menuUpdateFragment, bundle);
                    }
                    return true;
                });
                foodmenu.show();
            });
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (menuGetAllTask != null) {
            menuGetAllTask.cancel(true);
            menuGetAllTask = null;
        }

        if (menuImageTask != null) {
            menuImageTask.cancel(true);
            menuImageTask = null;
        }

    }
}



