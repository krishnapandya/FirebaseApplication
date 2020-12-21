package com.example.myapplication.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityAddNotesBinding;
import com.example.myapplication.model.User;
import com.example.myapplication.viewmodel.UserViewModel;

public class AddNotesActivity extends AppCompatActivity implements UserViewModel.OnAddNoteListener{
    ActivityAddNotesBinding activityAddNotesBinding;
    UserViewModel u;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddNotesBinding = ActivityAddNotesBinding.inflate(getLayoutInflater());
        setContentView(activityAddNotesBinding.getRoot());
        u= new ViewModelProvider(this).get(UserViewModel.class);
        u.listener=this;
        activityAddNotesBinding.setViewModel(u);
    }

    @Override
    public void addnote() {
        startActivity(new Intent(AddNotesActivity.this,NotesListActivity.class));
    }

    @Override
    public void getImageFromCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, 0);
    }

    @Override
    public void getImageFromGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK&&data!=null){
            switch (requestCode){

                case 0:

                    Uri imageUri=data.getData();
                    Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                    activityAddNotesBinding.imageView.setImageBitmap(selectedImage);
                    uploadImageToStorage(imageUri);
                    break;

                case 1:

                    Uri selectedImageUri =  data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImageUri,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();

                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                    activityAddNotesBinding.imageView.setImageBitmap(bitmap);
                    uploadImageToStorage(selectedImageUri);
                    break;
            }
        }
    }

    private void uploadImageToStorage(Uri imageUri) {
        u.uploadImage(imageUri);
    }
}