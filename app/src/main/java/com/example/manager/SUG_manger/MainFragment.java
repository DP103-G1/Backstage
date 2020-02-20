package com.example.manager.SUG_manger;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Common;
import com.example.g1.R;
import com.example.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {
    private static final String TAG = "TAG_MainFragment";
    private RecyclerView rvReact;
    private Button btReply;
    private Activity activity;
    private CommonTask boxMangerGetAllTask;
    private List<Box> boxes;

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
        SearchView searchView = view.findViewById(R.id.searchView);

        boxes = getBoxes();
        showBoxes(boxes);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.isEmpty()){
                    showBoxes(new ArrayList<Box>());//list是interface，要實作list的物件
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private List<Box> getBoxes() {
        List<Box> boxes = null;
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/BoxServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "getAll");
            String jsonOut = jsonObject.toString();
            boxMangerGetAllTask = new CommonTask(url, jsonOut);
            try {
                String jsonIn = boxMangerGetAllTask.execute().get();
                Type listType = new TypeToken<List<Box>>() {
                }.getType();
                boxes = new Gson().fromJson(jsonIn, listType);
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
            TextView tvNumber, tvQuestion, tvUser, tvUserNumber, tvContent;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNumber = itemView.findViewById(R.id.tvNumber);
                tvQuestion = itemView.findViewById(R.id.tvQuestion);
                tvUser = itemView.findViewById(R.id.tvUser);
                tvUserNumber = itemView.findViewById(R.id.tvUserNumber);
                tvContent = itemView.findViewById(R.id.tvContent);
                expandedLayout = itemView.findViewById(R.id.ExpandedLayout);
                btReply = itemView.findViewById(R.id.btReply);
                btReply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = getArguments();
                        if (bundle != null && bundle.getSerializable("box")!= null){
                            Box box = (Box)bundle.getSerializable("box");
                            int memberName = box.getMember();
                            String topic = box.getTopic();
                            String url = Common.URL_SERVER +"/BoxServlet";
                           // tvUser.setText(memberName);
                            // 寫在listFragment tvQuestion.setText(topic);
                        }

                        Navigation.findNavController(v).navigate(R.id.action_mainFragment_to_replyFragment);
                    }
                });

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
            String url = Common.URL_SERVER + "/BoxServlet";
            int id = box.getId();
            holder.tvNumber.setText(String.valueOf(box.getId()));
            holder.tvQuestion.setText(box.getTopic());
            holder.tvUserNumber.setText(String.valueOf(box.getMember()));
            holder.tvContent.setText(box.getFeed_back());

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
