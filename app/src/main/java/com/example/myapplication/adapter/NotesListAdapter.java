package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Notes;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NotesListAdapter extends FirestoreRecyclerAdapter<Notes,NotesListAdapter.NoteViewHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param notesListActivity
     * @param options
     */

    Context context;
    public NotesListAdapter(Context context, @NonNull FirestoreRecyclerOptions<Notes> options) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Notes model) {
        holder.tvTitle.setText(model.getTitle());
        holder.tvDes.setText(model.getDes());
        holder.tvPriority.setText(model.getPriority());
        Glide.with(context).load(model.getImageUrl()).into(holder.image);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item_view,parent,false));
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle,tvDes,tvPriority;
        ImageView image;
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle=itemView.findViewById(R.id.tv_title);
            tvDes=itemView.findViewById(R.id.tv_des);
            tvPriority=itemView.findViewById(R.id.tv_priority);
            image = itemView.findViewById(R.id.image);
        }


    }
}
