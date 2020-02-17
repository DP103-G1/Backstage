package com.example.manager.member;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.Common;
import com.example.g1.Booking;
import com.example.g1.R;
import com.example.task.CommonTask;
import com.example.task.ImageTask;
import com.example.waiter.WaiterSelectFragment;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MemberFragment extends Fragment {
    private static final String TAG = "TAG_MemberFragment";
    private Activity activity ;
    private RecyclerView rvMember;
    private List<Member> memberList;
    private CommonTask memberGetAllTask;
    private ImageTask memberTask;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SearchView searchView = view.findViewById(R.id.seachView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvMember = view.findViewById(R.id.rvMember);
        rvMember.setLayoutManager(new LinearLayoutManager(activity));
        memberList = getMembers();
        showMember(memberList);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showMember(memberList);
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
                    showMember(memberList);
                } else {
                    List<Member> searchMembers = new ArrayList<>();

                    for (Member searchMember : memberList) {
                        if (String.valueOf(searchMember.getmember_Id()).contains(newText)) {
                            searchMembers.add(searchMember);
                        }
                    }
                    showMember(searchMembers);
                }

                return true;
            }
        });
    }



    private class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberHolder>{
        private LayoutInflater layoutInflater;
        private List<Member> memberList;

         MemberAdapter(Context context ,List<Member> selectMembers) {
            layoutInflater = LayoutInflater.from(context);
            this.memberList = selectMembers;
        }

            void setMemberList(List<Member> selectMembers){
             this.memberList = selectMembers;
            }

         class MemberHolder extends RecyclerView.ViewHolder {
             TextView tvMemberId,tvMemberName;

             MemberHolder(View view) {
                super(view);
                tvMemberId = view.findViewById(R.id.tvMemberId);
                tvMemberName = view.findViewById(R.id.tvMemberName);
            }
        }

        @Override
        public int getItemCount() {
            return memberList.size();
        }

        @NonNull
        @Override
        public MemberHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
             View view = layoutInflater.inflate(R.layout.item_view_member,parent,false);
            return new MemberHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MemberHolder holder, int position) {
            final Member member = memberList.get(position);
            String url = Common.URL_SERVER + "/MembersServlet";
            String memId = String.valueOf(member.getmember_Id());
            memberTask = new ImageTask(url,memId);
            memberTask.execute();
            holder.tvMemberId.setText(String.valueOf(member.getmember_Id()));
            holder.tvMemberName.setText(member.getname());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("member",member);
//                    Navigation.findNavController(v).navigate(R.id);
                }
            });
        }

    }



    private List<Member> getMembers() {
        List<Member> members = null;
        if (Common.networkConnected(activity)){
            String url = Common.URL_SERVER + "/MembersServlet";
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action","getAll");
            String jsonOut = jsonObject.toString();
            memberGetAllTask = new CommonTask(url,jsonOut);
            try {
                String jsonIn = memberGetAllTask.execute().get();
                Type listType = new TypeToken<List<Member>>(){

                }.getType();
                members = Common.gson.fromJson(jsonIn,listType);
            }catch (Exception e){
                Log.e(TAG,e.toString());
            }
        }else {
            Common.showToast(activity,R.string.textNoNetwork);
        }
        return members;

    }

    private void showMember(List<Member> memberList) {
        if (memberList == null || memberList.isEmpty()){
            Common.showToast(activity,R.string.textMemberNoFound);
        }
        MemberAdapter memberAdapter = (MemberAdapter) rvMember.getAdapter();
        if (memberAdapter == null){
            rvMember.setAdapter(new MemberAdapter(activity,memberList));
        }else {
            memberAdapter.setMemberList(memberList);
            memberAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (memberGetAllTask != null){
            memberGetAllTask.cancel(true);
            memberGetAllTask = null;

        }
        if (memberTask != null){
            memberTask.cancel(true);
            memberTask =null;
        }
    }
}

