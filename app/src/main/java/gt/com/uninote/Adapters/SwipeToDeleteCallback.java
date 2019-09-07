package gt.com.uninote.Adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class SwipeToDeleteCallback extends android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback {

    private NotesRecyclerViewAdapter m_Adapter;

    public SwipeToDeleteCallback (NotesRecyclerViewAdapter adapter)
    {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        m_Adapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        m_Adapter.deleteNote(position);
    }
}
