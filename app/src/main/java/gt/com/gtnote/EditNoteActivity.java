package gt.com.gtnote;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import gt.com.gtnote.Models.Note;
import gt.com.gtnote.Models.NoteManager;
import gt.com.gtnote.Models.NoteMeta;

public class EditNoteActivity extends AppCompatActivity {
    
    private static final String TAG = "GTNOTE";

    private final int EDIT_NOTE_TYPE_ID = 0;
    private final int PREVIEW_NOTE_TYPE_ID = 1;
    
    private final int NOTE_NOT_EXISTENT_ID = -1;
    
    //private int noteId = NOTE_NOT_EXISTENT_ID;  // would be 0 if not initialized like this (which could overwrite note with ID=0 if something goes wrong)
    
    private NoteManager noteManager;
    private Note note;

    private Toolbar mToolbar;
    private LinearLayout noteViewLayout;
    private TextView noteTextView;
    private LinearLayout noteEditLayout;
    private EditText noteEditText;
    private EditText noteTitleEditText;
    private Button noteSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        //Get all View-Elements by their Id
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        
        findViews();
    
        noteManager = NoteManager.getInstance();
    
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int type = extras.getInt("typeId");
            int noteId = extras.getInt("noteId");
            
            // create / load note object
            if (noteId == NOTE_NOT_EXISTENT_ID) {
                note = noteManager.createNote();
                Log.d(TAG, "created new note with id="+note.getNoteMeta().getNoteId());
            } else {
                note = noteManager.getById(noteId);
            }
    
            // set proper activity layout
            switch (type) {
                case EDIT_NOTE_TYPE_ID:
                    Log.d(TAG, "opened EditNoteActivity with type=edit, id="+noteId);
                    
                    setTitle("Edit Note");
                    editText(note.getNoteContent().getSpanned());
                    
                    break;
                case PREVIEW_NOTE_TYPE_ID:
                    Log.d(TAG, "opened EditNoteActivity with type=preview, id="+noteId);
                    
                    setTitle("Preview Note");
                    displayText(note.getNoteContent().getSpanned());
                    
                    break;
                default:
                    Log.w(TAG, "opened EditNoteActivity with type="+type+", id="+noteId);
                    setTitle("Note");
            }
        } else {
            Log.w(TAG, "opened EditNoteActivity without any extras");
            setTitle("Note");
        }
    }
    
    private void findViews() {
        noteViewLayout = findViewById(R.id.noteViewLayout);
        noteTextView = findViewById(R.id.noteTextView);
        noteEditLayout = findViewById(R.id.noteEditLayout);
        noteEditText = findViewById(R.id.noteEditText);
        noteTitleEditText = findViewById(R.id.noteTitleEditText);
        noteSettingsButton = findViewById(R.id.noteSettingsButton);
        
        noteTextView.setTextIsSelectable(true);
        
        // handle double tap
        noteTextView.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(EditNoteActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent event) {
                    
                    Log.d(TAG, "onDoubleTap: switch to edit mode");
                    
                    // go to edit mode
                    editText(note.getNoteContent().getSpanned());
                    
                    //TODO: maybe use this later to position the cursor in edit text
                    Log.d(TAG, "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                    
                    return super.onDoubleTap(event);
                }
            });
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                
                if (noteViewLayout.getVisibility() == View.VISIBLE) {  // just making sure... (probably not necessary)
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
                return false;
            }
        });
        
        noteSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
    
                Intent intent = new Intent(getApplicationContext(), CurrentNoteSettings.class);
                //todo: intent.putExtra("colorHue", note.getNoteMeta().getColor().hue);
                startActivityForResult(intent, 1);
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                float colorHue = data.getFloatExtra("colorHue", 0);
                float[] hsv = new float[]{
                        colorHue,
                        1,
                        1
                };
                int noteColor = Color.HSVToColor(hsv);
                //todo: note.getNoteMeta().setColor(Color.red(noteColor), Color.green(noteColor), Color.blue(noteColor));
                Toast.makeText(this, String.format("rgb: (%d, %d, %d)", Color.red(noteColor), Color.green(noteColor), Color.blue(noteColor)), Toast.LENGTH_LONG).show();
                noteSettingsButton.getBackground().setColorFilter(noteColor, PorterDuff.Mode.MULTIPLY);
            }
        }
    }
    
    /**
     * Shows one layout and hides the respective other.
     * @param typeId either EDIT_NOTE_TYPE_ID or PREVIEW_NOTE_TYPE_ID
     */
    private void setLayoutType(int typeId) {
        switch (typeId) {
            case EDIT_NOTE_TYPE_ID:
                noteViewLayout.setVisibility(View.GONE);
                noteEditLayout.setVisibility(View.VISIBLE);
                break;
            case PREVIEW_NOTE_TYPE_ID:
                noteViewLayout.setVisibility(View.VISIBLE);
                noteEditLayout.setVisibility(View.GONE);
                break;
        }
    }
    
    private void displayText(Spanned spanned) {
        Log.d(TAG, String.format("display text: '''%s'''", spanned.toString()));
        
        setLayoutType(PREVIEW_NOTE_TYPE_ID);
        noteTextView.setText(spanned);
    }
    
    private void editText(Spanned spanned) {
        Log.d(TAG, String.format("edit text: '''%s'''", spanned.toString()));
    
        setLayoutType(EDIT_NOTE_TYPE_ID);
        noteEditText.setText(spanned);
    }
    
    @Override
    public void onBackPressed() {
        if (noteEditLayout.getVisibility() == View.VISIBLE) {
            finishEditing();
        } else {
            //TODO: MainActivity needs to update recyclerview when put into focus again ("resumed"?)
            super.onBackPressed();
        }
    }
    
    private void finishEditing() {
        try {
    
            // apply changes
    
            NoteMeta meta = note.getNoteMeta();
            //meta.setTitle();  //TODO: create view for title editing and read from it here
            //meta.setColor();  //TODO: create view for color editing and read from it here
            meta.setLastEditTime(System.currentTimeMillis());
            
            note.getNoteContent().setSpanned(new SpannableString(noteEditText.getText()));
            
            noteManager.save(note);
            
        } catch (JSONException e) {
            Log.e(TAG, "finished editing: saving note failed: ", e);
            Toast.makeText(this, "failed to save note", Toast.LENGTH_SHORT).show();
        }
        
        displayText(note.getNoteContent().getSpanned());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings2) {
            //Open CurrentNoteSettings
        }

        return super.onOptionsItemSelected(item);
    }

}
