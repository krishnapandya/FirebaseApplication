package com.example.myapplication.data;

import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


public class AuthRepository {

    private static final String TAG = "AuthRepository";
    private MutableLiveData<User> userDetails = new MutableLiveData<>();
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public void createUser(String name, String mobile, String email, String pass) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Registered Successfully ");
                            User user = new User();
                            user.setName(name);
                            user.setMobile(mobile);
                            user.setEmail(email);
                            user.setPass(pass);
                            storeUser(user);
                        }
                        else
                            {
                            Log.d(TAG, "onComplete: " + task.getException());
                        }
                    }
                });
    }

    public void storeUser(User user) {
        FirebaseFirestore.getInstance().collection("USERS").document(user.getEmail()).set(user)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "storeUser: User Stored");

                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "storeUser: " + e.getMessage());
                });
    }

    public LiveData<User> loginUser(String email, String pass) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseFirestore.getInstance().collection("USERS").whereEqualTo("email", email)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (value != null)
                                {
                                    userDetails.setValue(value.getDocuments().get(0).toObject(User.class));

                                }
                                if (error != null)
                                {
                                    Log.e(TAG, "onEvent: ", error);
                                }
                            }
                        });
                Log.d(TAG, "loginUser: Login Success");
                //userDetails(email);
            } else {
                Log.d(TAG, "loginUser: " + task.getException());
            }
        });
        return userDetails;
    }


    public LiveData<User> getUserDetails(String email) {
        FirebaseFirestore.getInstance().collection("USERS").whereEqualTo("email", email).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (value!=null&&!value.isEmpty()){
                   userDetails.setValue(value.getDocuments().get(0).toObject(User.class));
                }
                if (error!=null){
                    Log.e(TAG, "onEvent: ",error );
                }

            }
        });
        return  userDetails;
    }

}

