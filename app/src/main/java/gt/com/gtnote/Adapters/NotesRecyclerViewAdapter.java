package gt.com.gtnote.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gt.com.gtnote.Models.Note;
import gt.com.gtnote.R;

public class NotesRecyclerViewAdapter extends RecyclerView.Adapter<NotesRecyclerViewAdapter.ViewHolder>
{

    private List<Note> mNotes = new ArrayList<>();
    private Context mContext;

    public NotesRecyclerViewAdapter(List<Note> notes, Context context)
    {
        mNotes = notes;
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        TextView description;
        RelativeLayout layout;

        public ViewHolder(View noteView)
        {
            super(noteView);
            title = noteView.findViewById(R.id.note_in_list_title);
            description = noteView.findViewById(R.id.note_in_list_description);
            layout = noteView.findViewById(R.id.note_in_list_layout);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_note_in_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(mNotes.get(position).getNoteMeta().getTitle());
        holder.description.setText("Hier sollte eine Vorschau der Note stehen.");
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
