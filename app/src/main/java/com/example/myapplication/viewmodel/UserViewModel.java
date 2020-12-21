package com.example.myapplication.viewmodel;

import android.net.Uri;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.adapter.onAddNoteListner;
import com.example.myapplication.data.AuthRepository;
import com.example.myapplication.data.NotesRepository;
import com.example.myapplication.model.Notes;
import com.example.myapplication.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class UserViewModel extends ViewModel {
    public String name, mobile, email, title, description, priority;
    public onAddNoteListner onAddNoteListner;
    public OnAddNoteListener listener;
    public Uri imageUri;

    public LiveData<User> getUserProfile(String email) {
        return new AuthRepository().getUserDetails(email);
    }

    public LiveData<List<Notes>> getNoteList() {
        return new NotesRepository().getNotes();
    }

    public FirestoreRecyclerOptions<Notes> getNoteListOption() {
        return new NotesRepository().getNoteOption();
    }


    public void goToAddNewNote(View view) {
        onAddNoteListner.gotoAddnotes();
    }

    public void uploadImage(Uri imageUri) {
        this.imageUri = imageUri;
    }


    public interface OnAddNoteListener {
        public void addnote();

        void getImageFromCamera();

        void getImageFromGallery();
    }


    public void OnAddNote(View view) {
        Notes notes = new Notes();
        notes.setTitle(title);
        notes.setDes(description);
        notes.setPriority(priority);
        // new NotesRepository().addNewNote(notes);
        new NotesRepository().uploadImage(imageUri, notes);

        listener.addnote();
    }

    public void onChooseImage(View view) {
        String[] options = {"from camera", "from gallery", "cancel"};
        try {
            AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext())
                    .setTitle("Choose Image")
                    .setItems(options, (dialog1, which) -> {
                        switch (options[which]) {
                            case "from camera":
                                listener.getImageFromCamera();
                                break;
                            case "from gallery":
                                listener.getImageFromGallery();
                                break;
                            case "cancel":
                                dialog1.dismiss();
                                break;
                        }
                    });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
