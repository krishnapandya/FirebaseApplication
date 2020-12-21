package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Display;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityProfileBinding;
import com.example.myapplication.model.User;
import com.example.myapplication.viewmodel.UserViewModel;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private SharedPreferences preferences;
    private String userEmail="";
    LiveData<User> userDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UserViewModel viewModel= new ViewModelProvider(this).get(UserViewModel.class);

        preferences=getSharedPreferences("user", MODE_PRIVATE);

        userDetails=viewModel.getUserProfile(preferences.getString("email",""));

        userDetails.observe(this,user -> {
            binding.setUser(user);
        });
    }
}