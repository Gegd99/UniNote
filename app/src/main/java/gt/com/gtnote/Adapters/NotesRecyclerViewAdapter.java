package gt.com.gtnote.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gt.com.gtnote.Interfaces.OnNoteListener;
import gt.com.gtnote.Models.Note;
import gt.com.gtnote.R;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder>
{

    private List<Note> mNotes;
    private OnNoteListener mOnNoteListener;

    public NotesRecyclerViewAdapter(List<Note> notes, OnNoteListener onNoteListener)
    {
        if (notes == null)
        {
            mNotes = new ArrayList<>();
        }
        else
        {
            mNotes = notes;
        }
        mOnNoteListener = onNoteListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener
    {
        TextView title;
        TextView description;
        LinearLayout layout;
        OnNoteListener onNoteListener;

        public ViewHolder(View noteView, OnNoteListener onNoteListener)
        {
            super(noteView);
            title = noteView.findViewById(R.id.note_in_list_title);
            description = noteView.findViewById(R.id.note_in_list_description);
            layout = noteView.findViewById(R.id.note_in_list_layout);
            this.onNoteListener = onNoteListener;

            noteView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_note_in_list, parent, false);
        ViewHolder holder = new ViewHolder(view, mOnNoteListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(mNotes.get(position).getNoteMeta().getTitle());
        holder.description.setText("Hier sollte eine Vorschau der Note stehen.");
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }
}
