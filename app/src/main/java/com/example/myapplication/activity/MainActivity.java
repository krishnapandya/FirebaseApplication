package com.example.myapplication.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.adapter.OnLoginListener;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.model.User;
import com.example.myapplication.viewmodel.AuthViewModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.GoogleAuthProvider;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements OnLoginListener {
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 0;
    private ActivityMainBinding binding;
    private LiveData<User> loginResponse;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private GoogleSignInClient signInClient;
    private GoogleSignInOptions gso;
    private static final String EMAIL = "email";
    private CallbackManager callbackManager;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = getSharedPreferences("user", MODE_PRIVATE);
        AuthViewModel viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        binding.setViewModel(viewModel);

        viewModel.loginListener = this;

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(this, gso);

        binding.googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

        binding.btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
        });
        binding.loginButton.setReadPermissions(Arrays.asList(EMAIL));
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: "+loginResult.toString());
                firebaseLoginWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: "+"is cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "onError: "+error.getMessage());

            }
        });

        if (preferences.contains("email")) {
            startActivity(new Intent(MainActivity.this, NotesListActivity.class));
            finish();
        }


    }

    public void googleSignIn() {
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onLogin(LiveData<User> loginResponse) {
        loginResponse.observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Log.d(TAG, "onChanged: " + user.getName());
                editor = preferences.edit();
                editor.putString("email", user.getEmail());
                editor.putString("name", user.getName());
                editor.putString("mobile", user.getMobile());
                editor.commit();
                startActivity(new Intent(MainActivity.this, NotesListActivity.class));
                finish();
            }
        });
    }

    @Override
    public void goToRegistraion() {
        startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode != RESULT_CANCELED) {
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            }
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Toast.makeText(getApplicationContext(), "Signing Success" + account, Toast.LENGTH_SHORT).show();
            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode() + e.getMessage());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null)
            Toast.makeText(this, "Signed in as " + account.getDisplayName(), Toast.LENGTH_SHORT).show();
        firebaseLoginWithGoogle(account.getIdToken());
    }

    private void firebaseLoginWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(this, NotesListActivity.class));
                Toast.makeText(this, "" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
            } else Log.e(TAG, "firebaseLoginWithGoogle: ", task.getException());
        });
    }

    private void firebaseLoginWithFacebook(AccessToken idToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(idToken.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                    startActivity(new Intent(this, NotesListActivity.class));
                    Toast.makeText(this, "" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();

            } else {
                FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(credential).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        startActivity(new Intent(this, NotesListActivity.class));
                        Toast.makeText(this, "" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                    } else {

                    }

                });
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI(account);
    }
}