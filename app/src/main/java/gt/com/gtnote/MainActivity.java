package gt.com.gtnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;

import java.util.List;

import javax.inject.Inject;

import gt.com.gtnote.Adapters.SwipeToDeleteCallback;
import gt.com.gtnote.Models.AndroidFileIO;
import gt.com.gtnote.Adapters.NotesRecyclerViewAdapter;
import gt.com.gtnote.Interfaces.OnNoteListener;
import gt.com.gtnote.Models.Managers;
import gt.com.gtnote.Models.Note;
import gt.com.gtnote.Interfaces.NoteContent;
import gt.com.gtnote.Models.NoteManager;

import gt.com.gtnote.Models.NoteMeta;
import gt.com.gtnote.Models.SettingsManager;
import gt.com.gtnote.Models.SubModels.Color;
import gt.com.gtnote.dagger.ManagersComponent;

import static gt.com.gtnote.helper.SortAndFilter.sortAndFilterList;
import static gt.com.gtnote.statics.Constants.EDIT_NOTE_TYPE_ID;
import static gt.com.gtnote.statics.Constants.MAIN_EDIT_INTENT_TYPE_ID_KEY;
import static gt.com.gtnote.statics.Constants.PREVIEW_NOTE_TYPE_ID;

public class MainActivity extends AppCompatActivity implements OnNoteListener {

    private static final String TAG = "GTNOTE";

    private CoordinatorLayout mCoordinatorLayout;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Note> mFilteredAndSortedNotes;

    @Inject Managers m_Managers;
    private NoteManager m_NoteManager;
    private SettingsManager m_SettingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        injectManagers();

        attachListeners();
    }

    /**
     * Method in every activity, which finds all View-Elements by their Id
     */
    private void findViews()
    {
        //Layout
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainLayout);
        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //Button
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        //Lists
        mRecyclerView = (RecyclerView) findViewById(R.id.notes_recycler_view);
    }

    private void attachListeners()
    {
        //ButtonListener for creating a new note
        mFab.setOnClickListener(view -> createNewNote());

        //Setup RecyclerView
        mFilteredAndSortedNotes = sortAndFilterList(m_NoteManager.getNotes(), m_SettingsManager.getFilterColors(), m_SettingsManager.getSortType());
        mAdapter = new NotesRecyclerViewAdapter(mFilteredAndSortedNotes, this);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback((NotesRecyclerViewAdapter)mAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void testSettingsManager()
    {
        m_SettingsManager.addFilterColors(Color.GREEN);
        m_SettingsManager.addFilterColors(Color.RED);
        m_SettingsManager.addFilterColors(Color.UNKNOWN);
    }

    private String test() throws JSONException {

        String titleString = "Hello World";
        String contentString = "Einen schönen guten Morgen!";

        // create a NoteManager instance for this test
        NoteManager noteManager = new NoteManager(new AndroidFileIO(this));

        Note note = noteManager.createNote();
        NoteMeta meta = note.getNoteMeta();
        NoteContent content = note.getNoteContent();

        // set data
        meta.setColor(Color.BLUE);
        meta.setTitle(titleString);
        content.setText(contentString);

        // write to file
        noteManager.save(note);

        // remember id
        int id = meta.getNoteId();

        // create another NoteManager instance
        noteManager = new NoteManager(new AndroidFileIO(this));

        // get note instance and read content from file
        note = noteManager.getById(id);
        if (note != null) {
            String loadedContentString = note.getNoteContent().getText();  // reads from file
            Log.d(TAG, String.format("test: loaded content string: '''%s'''", loadedContentString));

            if (loadedContentString.equals(contentString)) {
                return "Test was successfull: contents match!";
            }

            if (loadedContentString.contains(contentString)) {
                return "Test was successfull: contents match not fully but string is contained (probably due to formatting).";
            }

            return "Test failed: loaded content is: "+loadedContentString;
        }
        else {
            int length = 0;
            for (Note n : noteManager.getAll()) {
                length++;
            }
            return String.format(
                    "Test failed: note with same id could not be found (notes: %d)",
                    length
            );
        }
    }

    /**
     * Injects NoteManager and SettingsManager.
     */
    public void injectManagers()
    {
        ManagersComponent managersComponent = ((ApplicationClass) getApplication()).getManagersComponent();

        managersComponent.inject(this);

        m_NoteManager = m_Managers.getNoteManager();
        m_SettingsManager = m_Managers.getSettingsManager();
    }

    private void createNewNote()
    {
        Note createdNote = m_NoteManager.createNote();
        Log.d(TAG, "created new note with id="+createdNote.getNoteMeta().getNoteId());

        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(MAIN_EDIT_INTENT_TYPE_ID_KEY, EDIT_NOTE_TYPE_ID);
        //intent.putExtra("note", note);
        intent.putExtra("noteId", createdNote.getNoteMeta().getNoteId());
        startActivity(intent);
    }

    private void openExistingNote(Note note)
    {
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(MAIN_EDIT_INTENT_TYPE_ID_KEY, PREVIEW_NOTE_TYPE_ID);
        //intent.putExtra("note", note);
        intent.putExtra("noteId", note.getNoteMeta().getNoteId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        mFilteredAndSortedNotes = sortAndFilterList(m_NoteManager.getNotes(), m_SettingsManager.getFilterColors(), m_SettingsManager.getSortType());
        ((NotesRecyclerViewAdapter)mAdapter).updateNotes(mFilteredAndSortedNotes);
        //mAdapter.notifyDataSetChanged();
        super.onResume();

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

    /**
     * Opens the note which was clicked on in the NotesRecyclerView
     * @param position int represent which note was clicked in the NotesRecyclerView
     */
    @Override
    public void onNoteClick(int position) {
        openExistingNote(mFilteredAndSortedNotes.get(position));
    }

    @Override
    public void onNoteSwipe(int position) {
        m_NoteManager.delete(m_NoteManager.getNotes().get(position));
        mAdapter.notifyDataSetChanged();
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Deleted note", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", v -> undoDelete());
        snackbar.show();
    }

    private void undoDelete()
    {
        m_NoteManager.undoDelete();
        mAdapter.notifyDataSetChanged();
    }
}
