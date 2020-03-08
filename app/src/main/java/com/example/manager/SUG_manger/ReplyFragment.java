package com.example.manager.SUG_manger;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.Common;
import com.example.g1.R;
import com.example.task.CommonTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;


public class ReplyFragment extends Fragment {
    private final static String TAG = "TAG_ReplyFragment";
    private FragmentActivity activity;
    private Box box;
    private TextView tvTopic;
    private EditText etReply;
    private Button btSent;
    private CommonTask commonTask;
    private List<Box>boxes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reply, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTopic = view.findViewById(R.id.tvTopic);
        etReply = view.findViewById(R.id.etReply);
        btSent = view.findViewById(R.id.btReply);

        final NavController navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("box") == null){
            Common.showToast(activity,R.string.textNoTitle);
            navController.popBackStack();
            return;
        }
        box = (Box)bundle.getSerializable("box");
        showBox();//bundle整個box資訊抓進reply頁面

        btSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reply = etReply.getText().toString().trim();

                if (Common.networkConnected(activity)){
                    String url = Common.URL_SERVER + "/BoxServlet";
                    box.setReply(reply);//get reply轉jason
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action","boxUpdate");
                    jsonObject.addProperty("id",String.valueOf(box.getId()));
                    jsonObject.addProperty("box",new Gson().toJson(box));
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("box",box);//送出
//
//                   Navigation.findNavController(v).navigate(R.id.action_replyFragment_to_listFragment,bundle);
                    commonTask = new CommonTask(url,jsonObject.toString());
                    int count = 0;
                    try {
                        String result = commonTask.execute().get();
                        count = Integer.valueOf(result);
                    }catch (Exception e){
                        Log.e(TAG,e.toString());
                    }
                    if (count == 0){
                        Common.showToast(activity,R.string.textDeleteFail);
                    }else {
                        Common.showToast(activity,R.string.textReplySuccess);
                    }
                }else {
                    Common.showToast(activity,R.string.textNoNetwork);
                }
                navController.popBackStack();
            }
        });


    }

    private  void  showBox(){
        String url = Common.URL_SERVER + "/BoxServlet";
        etReply.setText(box.getReply());
        tvTopic.setText(box.getTopic());


    }

}
