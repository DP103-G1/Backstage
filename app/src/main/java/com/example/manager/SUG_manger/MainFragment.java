package com.example.manager.SUG_manger;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main.Common;
import com.example.main.R;
import com.example.main.Url;
import com.example.task.CommonTask;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {
    private static final String TAG = "TAG_MainFragment";
    private RecyclerView rvReact;
    private Activity activity;
    private CommonTask boxMangerGetAllTask;
    private List<Box> boxes;
    private SearchView searchView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvReact = view.findViewById(R.id.rvReact);
        rvReact.setLayoutManager(new LinearLayoutManager(activity));
        rvReact.setAdapter(new BoxAdapter(activity,boxes));
        searchView = view.findViewById(R.id.searchView);

        boxes = getBoxes();
        showBoxes(boxes);

        //依照輸入變化
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                BoxAdapter adapter = (BoxAdapter) rvReact.getAdapter();
                if (adapter != null) {
                    if (newText.isEmpty()){
                       adapter.setBoxes(boxes);
                    }else{
                        List<Box> searchBox = new ArrayList<>();
                        for (Box box : boxes){
                            if (box.getTopic().toUpperCase().contains(newText.toUpperCase())){
                                searchBox.add(box);
                            }
                        }
                        adapter.setBoxes(searchBox);
                    }
                    adapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }

            //確定關鍵字後不會抓取資料
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
    }

    private List<Box> getBoxes() {
        List<Box> boxes = null;
        if (Common.networkConnected(activity)) {
            String url = Url.URL_SERVER + "/BoxServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            boxMangerGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = boxMangerGetAllTask.execute().get();
                Type listType = new TypeToken<List<Box>>() {
                }.getType();
                boxes = new GsonBuilder().setDateFormat("yyyy-MM-dd").create().fromJson(jsonIn, listType);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, "R.string.textDisconnected");
        }
        return boxes;
    }

    private void showBoxes(List<Box> boxes) {
        if (boxes == null || boxes.isEmpty()) {
            Common.showToast(activity, "textNoUpdate");
        }
        BoxAdapter boxAdapter = (BoxAdapter) rvReact.getAdapter();
        if (boxAdapter == null) {
            rvReact.setAdapter(new BoxAdapter(activity, boxes));
        } else {
            boxAdapter.setBoxes(boxes);
            boxAdapter.notifyDataSetChanged();
        }
    }

    private class BoxAdapter extends RecyclerView.Adapter<BoxAdapter.MyViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Box> boxes;


        BoxAdapter(Context context, List<Box> boxes) {
            layoutInflater = LayoutInflater.from(context);
            this.boxes = boxes;
        }

        void setBoxes(List<Box> boxes) {
            this.boxes = boxes;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ConstraintLayout expandedLayout;
            LinearLayout reMessageLayout;
            LinearLayout buttonLinearLayout;
            TextView tvNumber, tvQuestion, tvUser, tvUserNumber,tvtime , tvContent, tvReplyContent, tvIsReply;
            Button btReply;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNumber = itemView.findViewById(R.id.tvNumber);
                tvQuestion = itemView.findViewById(R.id.tvQuestion);
                tvUser = itemView.findViewById(R.id.tvUser);
                tvUserNumber = itemView.findViewById(R.id.tvUserNumber);
                tvtime = itemView.findViewById(R.id.tvTime);
                tvContent = itemView.findViewById(R.id.tvContent);
                tvReplyContent = itemView.findViewById(R.id.tvReplyContent);
                tvIsReply = itemView.findViewById(R.id.tvIsReply);
                reMessageLayout = itemView.findViewById(R.id.reMessageLayout);
                buttonLinearLayout =itemView.findViewById(R.id.buttonLinearLayout);
                expandedLayout = itemView.findViewById(R.id.ExpandedLayout);
                btReply = itemView.findViewById(R.id.btReply);

                tvQuestion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Box box = boxes.get(getAdapterPosition());
                        box.setExpanded(!box.isExpanded());
                        notifyItemChanged(getAdapterPosition());
                    }
                });
            }

        }

        @NonNull
        @Override
        public BoxAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.main_content_item_view, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            final Box box = boxes.get(position);
            holder.tvNumber.setText(String.valueOf(box.getId()));
            holder.tvQuestion.setText(box.getTopic());
            holder.tvUserNumber.setText(String.valueOf(box.getMember()));
            holder.tvtime.setText(box.getDate());
            holder.tvContent.setText(box.getFeed_back());
            if (box.getReply() != null) {
                holder.reMessageLayout.setVisibility(View.VISIBLE);
                holder.tvReplyContent.setText(box.getReply());
                holder.buttonLinearLayout.setVisibility(View.GONE);
            } else {
                holder.reMessageLayout.setVisibility(View.GONE);
                holder.buttonLinearLayout.setVisibility(View.VISIBLE);
            }

            //按下回覆將main資料帶入reply頁面,指明哪個按鈕
            holder.btReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG,"reply: " + box.getReply());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("box",box);
                    Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_replyFragment,bundle);
                }
            });

            boolean isExpanded = boxes.get(position).isExpanded();
            holder.expandedLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        }

        @Override
        public int getItemCount() {
            return boxes.size();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (boxMangerGetAllTask != null) {
            boxMangerGetAllTask.cancel(true);
            boxMangerGetAllTask = null;
        }
    }
}
