package com.example.myapplication.adapter;

import androidx.lifecycle.LiveData;

import com.example.myapplication.model.User;

public interface OnLoginListener {
    void onLogin(LiveData<User> loginUser);
    void goToRegistraion();

}
