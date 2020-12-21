package com.example.myapplication.data;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapplication.model.Notes;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class NotesRepository {
    private static final String TAG = "NotesRepository";
    List<Notes> notesList;
    MutableLiveData<List<Notes>> data = new MutableLiveData<>();
    public LiveData<List<Notes>> getNotes(){
        FirebaseFirestore.getInstance().collection("NOTES").addSnapshotListener((value, error) -> {
            if (value!=null&&!value.isEmpty()){
              notesList = new ArrayList<>();
                for (int i=0;i<value.size();i++) {

                    Notes note=value.getDocuments().get(i).toObject(Notes.class);
                    notesList.add(note);
                }
                data.setValue(notesList);
            }
            if (error!=null){
                Log.e(TAG, "getNotes: ",error);
            }
            else {
                Log.e(TAG, "getNotes: ");
            }
        });
        return data;
    }

    public FirestoreRecyclerOptions<Notes> getNoteOption(){
        Query query=FirebaseFirestore.getInstance().collection("NOTES");

        return new FirestoreRecyclerOptions.Builder<Notes>()
                .setQuery(query,Notes.class).build();

    }

    public void addNewNote(Notes note){
        FirebaseFirestore.getInstance().collection("NOTES").add(note).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: Note Added successfully");
                }
                else Log.e(TAG, "onComplete: ",task.getException() );
            }
        });
    }

    public void uploadImage(Uri imageUri, Notes notes) {
        StorageReference storageRef=FirebaseStorage.getInstance().getReference().child("images/image"+System.currentTimeMillis());

        storageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        notes.setImageUrl(uri.toString());
                        FirebaseFirestore.getInstance().collection("NOTES").add(notes)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()){
                                            Log.d(TAG, "onComplete: Note Added successfully");
                                        }
                                        else Log.e(TAG, "onComplete: ",task.getException() );
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "uploadImage: ",e );
        });
    }
}
