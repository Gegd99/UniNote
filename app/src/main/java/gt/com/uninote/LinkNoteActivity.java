package gt.com.uninote;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import gt.com.uninote.Adapters.NotesRecyclerViewAdapter;
import gt.com.uninote.Interfaces.OnNoteListener;
import gt.com.uninote.Models.Managers;
import gt.com.uninote.Models.Note;
import gt.com.uninote.Models.NoteManager;
import gt.com.uninote.dagger.ManagersComponent;

import static gt.com.uninote.statics.Constants.LINK_NOTE_INTENT_KEY;
import static gt.com.uninote.statics.Constants.MAIN_EDIT_INTENT_NOTE_ID_KEY;

public class LinkNoteActivity extends AppCompatActivity implements OnNoteListener {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    @Inject
    Managers m_Managers;
    private NoteManager m_NoteManager;

    private Note m_CurrentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_note);

        findViews();

        initNoteManager();

        Bundle extras = getIntent().getExtras();
        if(extras != null)
        {
            int noteId = extras.getInt(MAIN_EDIT_INTENT_NOTE_ID_KEY);

            m_CurrentNote = m_NoteManager.getById(noteId);

            attachListeners();
        }
        else
        {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }

    }

    /**
     * Method in every activity, which finds all View-Elements by their Id
     */
    private void findViews()
    {
        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.link_toolbar);
        mToolbar.setTitle("Pick a Note");
        //Lists
        mRecyclerView = (RecyclerView) findViewById(R.id.link_notes_recycler_view);
    }

    public void initNoteManager()
    {
        ManagersComponent managersComponent = ((ApplicationClass) getApplication()).getManagersComponent();

        managersComponent.inject(this);

        m_NoteManager = m_Managers.getNoteManager();
    }

    private void attachListeners()
    {
        //Setup RecyclerView
        List<Note> notes = new ArrayList<>(m_NoteManager.getNotes());
        notes.remove(m_CurrentNote);
        mAdapter = new NotesRecyclerViewAdapter(notes, this);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, GeneralSettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNoteClick(int position) {
        Intent returnIntent = new Intent();
        Note selectedNote = m_NoteManager.getNotes().get(position);
        returnIntent.putExtra(LINK_NOTE_INTENT_KEY, selectedNote.getNoteMeta().getNoteId());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onNoteSwipe(int position) {
        return;
    }
}
