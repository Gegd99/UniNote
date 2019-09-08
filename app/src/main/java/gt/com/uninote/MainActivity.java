package gt.com.uninote;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.transition.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import gt.com.uninote.Adapters.NotesRecyclerViewAdapter;
import gt.com.uninote.Adapters.SwipeToDeleteCallback;
import gt.com.uninote.Interfaces.NoteContent;
import gt.com.uninote.Interfaces.OnNoteListener;
import gt.com.uninote.Models.AndroidFileIO;
import gt.com.uninote.Models.Managers;
import gt.com.uninote.Models.Note;
import gt.com.uninote.Models.NoteManager;
import gt.com.uninote.Models.NoteMeta;
import gt.com.uninote.Models.SettingsManager;
import gt.com.uninote.Models.SubModels.Color;
import gt.com.uninote.dagger.ManagersComponent;
import gt.com.uninote.helper.ActivityUtils;

import static gt.com.uninote.helper.SortAndFilter.sortAndFilterList;
import static gt.com.uninote.statics.Constants.EDIT_NOTE_TYPE_ID;
import static gt.com.uninote.statics.Constants.GENERAL_PREFERENCES_NAME;
import static gt.com.uninote.statics.Constants.MAIN_EDIT_INTENT_TYPE_ID_KEY;
import static gt.com.uninote.statics.Constants.PREVIEW_NOTE_TYPE_ID;

public class MainActivity extends AppCompatActivity implements OnNoteListener {

    private static final String TAG = "GTNOTE";

    private CoordinatorLayout mCoordinatorLayout;
    private LinearLayout mFilterLinearLayout;
    private EditText mSearchEditText;
    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Note> mFilteredAndSortedNotes;
    private List<Color> m_FilterColors = new ArrayList<>();
    private String m_TextToSearch = "";

    @Inject Managers m_Managers;
    private NoteManager m_NoteManager;
    private SettingsManager m_SettingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        injectManagers();
        m_SettingsManager = new SettingsManager(getSharedPreferences(GENERAL_PREFERENCES_NAME, MODE_PRIVATE));
        attachListeners();
        
        if (m_NoteManager.handleFirstLaunch()) {
            ActivityUtils.generateDemoNote(m_NoteManager, getResources());
        }
    }

    /**
     * Method in every activity, which finds all View-Elements by their Id
     */
    private void findViews()
    {
        //Layout
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainLayout);
        mFilterLinearLayout = findViewById(R.id.linear_layout_filter);
        //EditText
        mSearchEditText = findViewById(R.id.search_edit_text);
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

        initFilterLayout();

        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                m_TextToSearch = s.toString();
                updateFilteredAndSortedNotes();
            }

            @Override
            public void afterTextChanged(Editable s) {
                return;
            }
        });

        //Setup RecyclerView
        mFilteredAndSortedNotes = sortAndFilterList(m_NoteManager.getNotes(), m_FilterColors, m_SettingsManager.getSortType(), m_TextToSearch);
        mAdapter = new NotesRecyclerViewAdapter(mFilteredAndSortedNotes, this);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback((NotesRecyclerViewAdapter)mAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void initFilterLayout() {
        Color[] allColors = Color.getAllNormal();

        for (Color color: allColors) {
            ImageView image = new ImageView(this);

            image.setPadding(5,5,5,5);
            int fullColor = android.graphics.Color.rgb(color.red, color.green, color.blue);
            int greyedColor = android.graphics.Color.rgb(color.red / 2, color.green / 2, color.blue / 2);

            image.setImageResource(R.drawable.color_bubble);

            if(m_FilterColors.contains(color))
            {
                image.setColorFilter(fullColor, PorterDuff.Mode.ADD);
            }
            else
            {
                image.setColorFilter(greyedColor, PorterDuff.Mode.ADD);
            }

            image.setOnClickListener(view -> {
                if(m_FilterColors.contains(color))
                {
                    m_FilterColors.remove(color);
                    image.setColorFilter(greyedColor, PorterDuff.Mode.ADD);
                }
                else
                {
                    m_FilterColors.add(color);
                    image.setColorFilter(fullColor, PorterDuff.Mode.ADD);
                }
                updateFilteredAndSortedNotes();
            });

            mFilterLinearLayout.addView(image);
        }
        mFilterLinearLayout.setVisibility(View.GONE);
    }

    private void updateFilteredAndSortedNotes()
    {
        mFilteredAndSortedNotes = sortAndFilterList(m_NoteManager.getNotes(), m_FilterColors, m_SettingsManager.getSortType(), m_TextToSearch);
        ((NotesRecyclerViewAdapter)mAdapter).updateNotes(mFilteredAndSortedNotes);
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
        updateFilteredAndSortedNotes();
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
        else if (id == R.id.open_filter){
            changeFilterVisibility();
        }
        else if (id == R.id.open_search){
            changeSearchVisibility();
        }

        return super.onOptionsItemSelected(item);
    }

    private void changeFilterVisibility()
    {
        if (mFilterLinearLayout.getVisibility() == View.GONE){
            mFilterLinearLayout.setVisibility(View.VISIBLE);
        }
        else {
            mFilterLinearLayout.setVisibility(View.GONE);
        }
    }

    private void changeSearchVisibility()
    {
        if (mSearchEditText.getVisibility() == View.GONE){
            mSearchEditText.setVisibility(View.VISIBLE);
        }
        else {
            mSearchEditText.setVisibility(View.GONE);
        }
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
        m_NoteManager.delete(mFilteredAndSortedNotes.get(position));
        updateFilteredAndSortedNotes();
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Deleted note", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", v -> undoDelete());
        snackbar.show();
    }

    @Override
    public void onNoteLongClick() {
        Toast.makeText(this, "Swipe to delete a note", Toast.LENGTH_SHORT).show();
    }

    private void undoDelete()
    {
        m_NoteManager.undoDelete();
        updateFilteredAndSortedNotes();
    }
}
