package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.R;
import com.example.myapplication.adapter.OnRegisterListener;
import com.example.myapplication.databinding.ActivityRegistrationBinding;
import com.example.myapplication.viewmodel.AuthViewModel;

public class RegistrationActivity extends AppCompatActivity implements OnRegisterListener {

    private ActivityRegistrationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AuthViewModel viewModel = new  ViewModelProvider(this).get(AuthViewModel.class);
        binding.setViewModel(viewModel);

        viewModel.registerListener=this;
    }

    @Override
    public void goToLogin() {
        startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
    }
}