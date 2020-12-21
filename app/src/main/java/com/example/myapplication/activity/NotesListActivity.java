package com.example.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.adapter.NotesListAdapter;
import com.example.myapplication.adapter.onAddNoteListner;
import com.example.myapplication.databinding.ActivityNotesListBinding;
import com.example.myapplication.model.Notes;
import com.example.myapplication.viewmodel.UserViewModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

public class NotesListActivity extends AppCompatActivity implements onAddNoteListner {

    private ActivityNotesListBinding viewBinding;
    private LiveData<List<Notes>> notesLiveList;
    private FirestoreRecyclerOptions<Notes> rvOptions;
    private NotesListAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding=ActivityNotesListBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());

        UserViewModel viewModel= new ViewModelProvider(this).get(UserViewModel.class);

        notesLiveList=viewModel.getNoteList();
        viewBinding.setViewModel(viewModel);
        viewBinding.rvNotes.setLayoutManager(new LinearLayoutManager(this));

        rvOptions=viewModel.getNoteListOption();

        noteAdapter=new NotesListAdapter(this,rvOptions);
        viewBinding.rvNotes.setAdapter(noteAdapter);
        viewModel.onAddNoteListner =this;


    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    public void gotoAddnotes() {
        startActivity(new Intent(NotesListActivity.this,AddNotesActivity.class));
    }
}