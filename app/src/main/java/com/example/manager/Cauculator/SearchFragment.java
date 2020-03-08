package com.example.manager.Cauculator;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.main.R;


public class SearchFragment extends Fragment {
   Activity activity;

   @Override
   public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      activity = getActivity();
   }

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
      activity.setTitle("營收統計");
      return inflater.inflate(R.layout.fragment_search,container,false);
   }

   @Override
   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      view.findViewById(R.id.btDay).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Navigation.findNavController(v).navigate(R.id.action_searchFragment_to_incomeFragment);
         }
      });

      view.findViewById(R.id.btMonth).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Navigation.findNavController(v).navigate(R.id.action_searchFragment_to_monthIncomeFragment);
         }
      });

      view.findViewById(R.id.btYear).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            Navigation.findNavController(v).navigate(R.id.action_searchFragment_to_yearIncomeFragment);
         }
      });
   }
}