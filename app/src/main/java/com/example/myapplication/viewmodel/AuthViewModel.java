package com.example.myapplication.viewmodel;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.adapter.OnLoginListener;
import com.example.myapplication.adapter.OnRegisterListener;
import com.example.myapplication.data.AuthRepository;

public class AuthViewModel extends ViewModel {
    public String email,pass,name,mobile;
     LiveData<String> loginResponse;
     public OnLoginListener loginListener ;
     public OnRegisterListener registerListener;

    public void onLoginClicked(View view){
        loginListener.onLogin(new AuthRepository().loginUser(email,pass));
    }

    public void onRegistrationClicked(View view){
        new AuthRepository().createUser(name,mobile,email,pass);
    }

    public void onGotoRegistrationClicked(View view){
        loginListener.goToRegistraion();
    }

    public void onGotoLoginClicked(View view){
        registerListener.goToLogin();
    }


    public void onFacebookLogin(View view){

    }
}
