package gt.com.gtnote.Adapters;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gt.com.gtnote.Interfaces.NoteContent;
import gt.com.gtnote.Interfaces.OnNoteListener;
import gt.com.gtnote.Models.Note;
import gt.com.gtnote.Models.NoteMeta;
import gt.com.gtnote.Models.SubModels.Color;
import gt.com.gtnote.Models.TextEditOperations;
import gt.com.gtnote.R;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder>
{

    private List<Note> mNotes;
    private OnNoteListener mOnNoteListener;
    private TextEditOperations textEditOperations = new TextEditOperations();

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
        CardView cardView;
        OnNoteListener onNoteListener;

        public ViewHolder(View noteView, OnNoteListener onNoteListener)
        {
            super(noteView);
            title = noteView.findViewById(R.id.note_in_list_title);
            description = noteView.findViewById(R.id.note_in_list_description);
            layout = noteView.findViewById(R.id.note_in_list_layout);
            cardView = noteView.findViewById(R.id.note_in_list_cardview);
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
        Note note = mNotes.get(position);
        NoteMeta noteMeta = note.getNoteMeta();
        NoteContent noteContent = note.getNoteContent();
    
        String title = textEditOperations.cutToReasonableLength(noteMeta.getTitle(), 24, 35, 100);
        // todo: get preview from meta
        String descriptionText = textEditOperations.cutToReasonableLength(textEditOperations.getRandomString(5, 20), 50, 70, 10);
        Color backgroundColor = noteMeta.getColor();
        int androidColorBackground = android.graphics.Color.rgb(backgroundColor.red, backgroundColor.green, backgroundColor.blue);
        
        holder.title.setText(title);
        holder.cardView.setCardBackgroundColor(androidColorBackground);
        holder.description.setText(descriptionText);
    
        // remove title if empty
        if (holder.title.length() == 0) {
            holder.title.setVisibility(View.GONE);
        } else {
            holder.title.setVisibility(View.VISIBLE);  // this is important since elements will be recycled!
        }
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public void deleteNote(int position)
    {
        mOnNoteListener.onNoteSwipe(position);
    }
}
