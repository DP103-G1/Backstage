package com.example.manager.SUG_manger;


import android.os.Bundle;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class ReplyFragment extends Fragment {
    private final static String TAG = "TAG_ReplyFragment";
    private FragmentActivity activity;
    private Box box;
    private TextView tvTopic;
    private EditText etReply;
    private Button btSent;

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
        btSent = view.findViewById(R.id.btSent);

        final NavController navController = Navigation.findNavController(view);
        Bundle bundle = getArguments();
        if (bundle == null || bundle.getSerializable("box") == null){
            Common.showToast(activity,R.string.textNoTitle);
            navController.popBackStack();
            return;
        }
        box = (Box)bundle.getSerializable("box");
        showBox();

        btSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reply = etReply.getText().toString().trim();

                if (Common.networkConnected(activity)){
                    String url = Common.URL_SERVER + "/BoxServlet";
                    Box box = new Box(id,member,topic,purpose,info,date,satisfed,feed_back,reply);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("action","boxUpdate");
                    jsonObject.addProperty("box",new Gson().toJson(box));
                }
            }
        });
    }

    private  void  showBox(){
        String url = Common.URL_SERVER + "/BoxServlet";
        etReply.setText(box.getReply());
    }

}
