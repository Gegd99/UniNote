package gt.com.gtnote;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import gt.com.gtnote.Adapters.NotesRecyclerViewAdapter;
import gt.com.gtnote.Models.FileIO;
import gt.com.gtnote.Models.Note;
import gt.com.gtnote.Models.NoteContent;
import gt.com.gtnote.Models.NoteManager;
import gt.com.gtnote.ViewModels.MainActivityViewModel;

import gt.com.gtnote.Models.NoteMeta;
import gt.com.gtnote.Models.SubModels.Color;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GTNOTE";


    private Toolbar mToolbar;
    private FloatingActionButton mFab;
    private TextView mExampleText;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private MainActivityViewModel mMainActivityViewModel;
    private NoteManager noteManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get all View-Elements by their Id
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mExampleText = (TextView) findViewById(R.id.exampleText);
        mRecyclerView = (RecyclerView) findViewById(R.id.notes_recycler_view);

        mMainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        mMainActivityViewModel.getMessage().observe(this, new Observer<String>() {

            @Override
            public void onChanged(@Nullable String s) {
                mExampleText.setText(s);
            }
        });

        initNotes();
        initRecyclerView();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainActivityViewModel.createNewNote(view);
            }
        });

    }

    private void initNotes() {
        try {
            noteManager = new NoteManager(new AndroidFileIO());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String test() throws JSONException {

        String titleString = "Hello World";
        String contentString = "Einen schönen guten Morgen!";

        // create a NoteManager instance for this test
        NoteManager noteManager = new NoteManager(new AndroidFileIO());

        Note note = noteManager.createNote();
        NoteMeta meta = note.getNoteMeta();
        NoteContent content = note.getNoteContent();

        // set data
        meta.setColor(Color.BLUE);
        meta.setTitle(titleString);
        content.setSpanned(new SpannableString(contentString));

        // write to file
        noteManager.save(note);

        // remember id
        int id = meta.getNoteId();

        // create another NoteManager instance
        noteManager = new NoteManager(new AndroidFileIO());

        // get note instance and read content from file
        note = noteManager.getById(id);
        if (note != null) {
            Spanned spanned = note.getNoteContent().getSpanned();  // reads from file
            String loadedContentString = spanned.toString();
            Log.d(TAG, String.format("test: loaded content string: '''%s'''", loadedContentString));

            if (loadedContentString.equals(contentString)) {
                return "Test was successfull: contents match!";
            }

            if (loadedContentString.contains(contentString)) {
                return "Test was successfull: contents match not fully but string is contained (probably due to formatting).";
            }

            return "Test failed: loaded content is: "+loadedContentString;
        } else {
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

    private void initRecyclerView()
    {
        mAdapter = new NotesRecyclerViewAdapter(noteManager.getNotes(), this);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class AndroidFileIO implements FileIO {

        @Override
        public String read(String filename) {

            String source = null;

            FileInputStream fis = null;
            try {
                fis = openFileInput(filename);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                source = sb.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }

            return source;
        }

        @Override
        public void write(String filename, String source) {

            FileOutputStream fos = null;
            try {
                fos = openFileOutput(filename, MODE_PRIVATE);
                fos.write(source.getBytes());// todo: maybe use source.getBytes("utf-8")?

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        }

        @Override
        public boolean fileExists(String path) {
            return new File(getFilesDir(), path).exists();
        }

        @Override
        public void delete(String path) {
            deleteFile(path);
        }

        @Override
        public String[] list() {
            return getFilesDir().list();
        }
    }
}
