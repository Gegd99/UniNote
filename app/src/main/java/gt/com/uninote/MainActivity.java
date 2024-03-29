package gt.com.uninote;

import android.app.SearchManager;
import android.content.Context;
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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
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
        mFilterLinearLayout.setVisibility(View.GONE);
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
        fillFilterColors();
        initFilterLayout();

        //Setup RecyclerView
        mFilteredAndSortedNotes = sortAndFilterList(m_NoteManager.getNotes(), m_FilterColors, m_SettingsManager.getSortType(), m_TextToSearch);
        mAdapter = new NotesRecyclerViewAdapter(mFilteredAndSortedNotes, this);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback((NotesRecyclerViewAdapter)mAdapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void fillFilterColors()
    {
        Color[] allColors = Color.getAllNormal();
        m_FilterColors.addAll(Arrays.asList(allColors));
    }

    private void initFilterLayout() {

        for (Color color: Color.getAllNormal()) {
            ImageView image = new ImageView(this);

            image.setPadding(5,5,5,5);
            int fullColor = android.graphics.Color.rgb(color.red, color.green, color.blue);
            int greyedColor = android.graphics.Color.rgb(color.red / 2, color.green / 2, color.blue / 2);

            image.setImageResource(R.drawable.color_bubble);

            if (m_FilterColors.contains(color))
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
                    if (m_FilterColors.size() == Color.getAllNormal().length)
                    {
                        m_FilterColors = new ArrayList<>();
                        m_FilterColors.add(color);
                        mFilterLinearLayout.removeAllViews();
                        initFilterLayout();
                    }
                    else
                    {
                        m_FilterColors.remove(color);
                        image.setColorFilter(greyedColor, PorterDuff.Mode.ADD);
                    }
                    if (m_FilterColors.isEmpty())
                    {
                        mFilterLinearLayout.removeAllViews();
                        fillFilterColors();
                        initFilterLayout();
                    }
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
    }

    private void updateFilteredAndSortedNotes()
    {
        mFilteredAndSortedNotes = sortAndFilterList(m_NoteManager.getNotes(), m_FilterColors, m_SettingsManager.getSortType(), m_TextToSearch);
        ((NotesRecyclerViewAdapter)mAdapter).updateNotes(mFilteredAndSortedNotes);
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
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.open_search);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                m_TextToSearch = newText;
                updateFilteredAndSortedNotes();
                return false;
            }
        });

        /*
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

         searchView = null;
        if (searchItem != null) {

        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }
        */

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
