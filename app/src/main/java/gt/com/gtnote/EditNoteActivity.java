package gt.com.gtnote;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.markdown4j.Markdown4jProcessor;

import java.io.IOException;

import javax.inject.Inject;

import gt.com.gtnote.Models.TextEditOperations;
import gt.com.gtnote.Models.Note;
import gt.com.gtnote.Models.NoteManager;
import gt.com.gtnote.Models.NoteMeta;
import gt.com.gtnote.Models.SubModels.Color;
import gt.com.gtnote.dagger.NoteManagerComponent;

import static gt.com.gtnote.statics.Constants.COLOR_PICK_INTENT_KEY;
import static gt.com.gtnote.statics.Constants.EDIT_NOTE_TYPE_ID;
import static gt.com.gtnote.statics.Constants.MAIN_EDIT_INTENT_NOTE_ID_KEY;
import static gt.com.gtnote.statics.Constants.MAIN_EDIT_INTENT_TYPE_ID_KEY;
import static gt.com.gtnote.statics.Constants.PREVIEW_NOTE_TYPE_ID;

public class EditNoteActivity extends AppCompatActivity {
    
    private static final String TAG = "GTNOTE";
    
    private final int NOTE_NOT_EXISTENT_ID = -1;
    
    //private int noteId = NOTE_NOT_EXISTENT_ID;  // would be 0 if not initialized like this (which could overwrite note with ID=0 if something goes wrong)

    @Inject NoteManager m_NoteManager;
    private Note note;

    private Toolbar mToolbar;
    private View baseView;
    private LinearLayout noteViewLayout;
    private WebView noteWebView;
    private LinearLayout noteEditLayout;
    private EditText noteEditText;
    private EditText noteTitleEditText;
    private TextView noteTitleTextView;
    private ImageButton noteColorButtonEdit;
    private ImageButton noteColorButtonView;
    private Button markdownButtonBold;
    private Button markdownButtonItalique;
    private Button markdownButtonLink;
    private Button markdownButtonLinkNote;
    
    private TextEditOperations textEditOperations = new TextEditOperations();
    
    private Markdown4jProcessor markdown4jProcessor = new Markdown4jProcessor();
    private String cssStyleSource;  // this style will be applied to the WebView showing the parsed NoteContent
    private String baseUrl = "gtnote://gtnote.com/";  // random prefix for all links to fix detection of link clicks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
    
        cssStyleSource = getString(R.string.note_webview_css);

        findViews();
        attachListeners();
        initNoteManager();
    
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int type = extras.getInt(MAIN_EDIT_INTENT_TYPE_ID_KEY);
            int noteId = extras.getInt(MAIN_EDIT_INTENT_NOTE_ID_KEY);
            
            //load note object
            note = m_NoteManager.getById(noteId);
    
            // set proper activity layout
            if (type == EDIT_NOTE_TYPE_ID || type == PREVIEW_NOTE_TYPE_ID)
            {
                Log.d(TAG, "opened EditNoteActivity with type="+type+", id="+noteId);

                setMode(type);
            }
            else
            {
                Log.w(TAG, "opened EditNoteActivity with type="+type+", id="+noteId);
                setTitle("Note");
            }
        }
        else {
            Log.w(TAG, "opened EditNoteActivity without any extras");
            setTitle("Note");
        }
        
        onNoteColorChanged();
    }

    /**
     * Method in every activity, which finds all View-Elements by their Id
     */
    private void findViews(){
        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        
        // Base View (whole activity)
        baseView = findViewById(R.id.editNoteActivityBaseView);
        
        //Layouts
        noteViewLayout = findViewById(R.id.noteViewLayout);
        noteEditLayout = findViewById(R.id.noteEditLayout);
        
        //TextViews
        noteTitleTextView = findViewById(R.id.noteTitleTextView);
        
        // WebView
        noteWebView = findViewById(R.id.noteWebView);
        
        //EditTexts
        noteEditText = findViewById(R.id.noteEditText);
        noteTitleEditText = findViewById(R.id.noteTitleEditText);
        //Button
        noteColorButtonEdit = findViewById(R.id.noteColorButtonEdit);
        noteColorButtonView = findViewById(R.id.noteColorButtonView);
        
        // Markdown Menu
        markdownButtonBold = findViewById(R.id.editNoteMarkdownButtonBold);
        markdownButtonItalique = findViewById(R.id.editNoteMarkdownButtonItalique);
        markdownButtonLink = findViewById(R.id.editNoteMarkdownButtonLink);
        markdownButtonLinkNote = findViewById(R.id.editNoteMarkdownButtonLinkNote);
    }

    /**
     * Method in every activity which sets all ViewListeners.
     */
    private void attachListeners()
    {
        // TextViewListener which handles double tap
        View.OnTouchListener doubleTabEditListener = new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(EditNoteActivity.this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent event) {

                    Log.d(TAG, "onDoubleTap: switch to edit mode");

                    // go to edit mode
                    setMode(EDIT_NOTE_TYPE_ID);

                    //TODO: maybe use this later to position the cursor in edit text
                    Log.d(TAG, "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");

                    return super.onDoubleTap(event);
                }
            });
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if (noteViewLayout.getVisibility() == View.VISIBLE) {  // just making sure... (probably not necessary)
                    gestureDetector.onTouchEvent(event);
                }
                return false;
            }
        };
    
        baseView.setOnTouchListener(doubleTabEditListener);
        noteWebView.setOnTouchListener(doubleTabEditListener);  // for some reason baseView doesn't catch events on that WebView

        //ButtonListener for switching to NoteSettings
        noteColorButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ColorPickingActivity.class);
                //todo: intent.putExtra("colorHue", note.getNoteMeta().getColor().hue);
                startActivityForResult(intent, 1);
            }
        });
        
        // Force WebView to open links in external browser
        // instead of displaying them inside the WebView.
        // Use different methods for different api levels.
        if (android.os.Build.VERSION.SDK_INT >= 24) {
            // use today's method
            noteWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    String url = request.getUrl().toString();
                    handleUrl(url);
                    return true;
                }
            });
        } else {
            // use deprecated method
            noteWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    handleUrl(url);
                    return true;
                }
            });
        }
        
        markdownButtonBold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surroundWithElements(noteEditText, "**", "**");
            }
        });
        
        markdownButtonItalique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surroundWithElements(noteEditText, "*", "*");
            }
        });
        
        markdownButtonLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surroundWithElements(noteEditText, "[", "](www.example.com)");
            }
        });
        
        markdownButtonLinkNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo: implement inserting note dialogue
                Toast.makeText(EditNoteActivity.this, "not implemented", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Surrounds the selection in the given EditText with these elements,
     * or places them at the cursor's position.
     * Also adjusts the cursor's position after inserting.
     *
     * @param editText
     * @param elementBefore
     * @param elementAfter
     */
    private void surroundWithElements(EditText editText, String elementBefore, String elementAfter) {
        if (editText.hasSelection()) {
            
            // surround the selected part with the elements and
            // place the cursor at the end of the original selected text
            
            String text = editText.getText().toString();
            int start = editText.getSelectionStart();
            int end = editText.getSelectionEnd();
            
            editText.setText(textEditOperations.surroundSelection(text, start, end, elementBefore, elementAfter));
            
            int cursorPosition = end + elementBefore.length();
            editText.setSelection(cursorPosition, cursorPosition);
            
        } else {  // no selection, just cursor in text
            
            // insert the two elements and place the cursor between them
            
            String text = editText.getText().toString();
            int start = editText.getSelectionStart();
            
            editText.setText(textEditOperations.insert(text, start, elementBefore+elementAfter));
            
            int cursorPosition = start + elementBefore.length();
            editText.setSelection(cursorPosition, cursorPosition);
        }
    }
    
    /**
     * Decides what to do when the user clicked a link with that url<br>
     * Four possible outcomes:
     * <ol>
     *     <li>The link is a Note ID and the Note exists → show that Note</li>
     *     <li>The link is a Note ID and the Note doesn't exist → prompt to create that Note</li>
     *     <li>The link is a Website → open in external browser</li>
     *     <li>The link is none of the above</li>
     * </ol>
     *
     * Note that 1. and 2. are note implemented yet.
     * @param url the URL or Note ID behind the link the user clicked
     */
    private void handleUrl(String url) {
        
        url = stripBaseUrlIfPresent(url);  // baseUrl is not part of the actual link
        
        // test whether the user linked a Note or a website
        if (looksLikeNoteId(url)) {  // assume user linked a note
            
            int noteId = parseNoteId(url);
            if (noteId != -1) {
                
                if (noteId != note.getNoteMeta().getNoteId()) {
                    Note linkedNote = getNoteById(noteId);
                    if (linkedNote != null) {
        
                        Log.d(TAG, String.format(
                                "handleUrl: open linked note #%d with title '%s'",
                                noteId, linkedNote.getNoteMeta().getTitle()
                        ));
        
                        openLinkedNote(linkedNote);
        
                    } else {
                        //todo: get align from clicked link as default title
                        Log.d(TAG, String.format(
                                "handleUrl: create non-existent linked note #%d",
                                noteId
                        ));
                        //todo: prompt to create note
                    }
                } else {  // this note is already shown here
                    Toast.makeText(this, "This note is already here.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // ignore
                Log.d(TAG, "handleUrl: note ID could not be parsed: '"+url+"'");
            }
            
        } else {  // assume the user linked a website
            
            url = addProtocolIfMissing(url);  // android decides based on the protocol which application will handle this
    
            Log.d(TAG, "handleUrl: open link: "+url);
            
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    }
    
    /**
     * baseUrl only exists to catch clicks on links with URLs
     * without absolute links (== without protocol).
     * This is of course not part of the actual link,
     * which is why it has to be removed before any further processing.
     *
     * @param url the URL as it was passed to the WebClient
     * @return
     */
    private String stripBaseUrlIfPresent(String url) {
        if (url.startsWith(baseUrl)) {
            return url.substring(baseUrl.length());
        }
        return url;
    }
    
    /**
     * iterates over all notes returned by NoteManager.getAll() and
     * returns first note with given ID or null if not found.
     * @param id
     * @return
     */
    private Note getNoteById(int id) {
        for (Note note: m_NoteManager.getAll()) {
            if (note.getNoteMeta().getNoteId() == id) {
                return note;
            }
        }
        return null;
    }
    
    /**
     * Returns whether the given URL could be interpreted as a Note ID
     * @param url
     * @return
     */
    private boolean looksLikeNoteId(String url) {
        try {
            Integer.parseInt(url);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    /**
     * tries to parse the given string to a Note ID,
     * otherwise returns -1
     * @param url
     * @return the parsed Note ID or -1
     */
    private int parseNoteId(String url) {
        try {
            return Integer.parseInt(url);
        } catch (NumberFormatException e) {
            // ignore
        }
        return -1;
    }
    
    /**
     * When a new Intent is called as ACTION_VIEW,
     * android decides based on the protocol of the passed URI
     * which application will handle this. In order to open
     * all links in a browser, a web protocol needs to be in the URL.
     *
     * @param url the URL as it was written by the user inside the markdown link
     * @return the URL with a protocol in front
     */
    private String addProtocolIfMissing(String url) {
        boolean hasProtocol = url.startsWith("http://") || url.startsWith("https://");
        if (!hasProtocol) {
            return "https://" + url;
        }
        return url;
    }
    
    /**
     * Creates another instance of this activity on top
     * of this activity and shows the linked note.
     *
     * @param note the linked note to display
     */
    private void openLinkedNote(Note note) {
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(MAIN_EDIT_INTENT_TYPE_ID_KEY, PREVIEW_NOTE_TYPE_ID);  // open in preview mode
        intent.putExtra("noteId", note.getNoteMeta().getNoteId());  // tell which note to show
        startActivity(intent);
    }

    public void initNoteManager()
    {
        NoteManagerComponent noteManagerComponent = ((ApplicationClass) getApplication()).getNoteManagerComponent();

        noteManagerComponent.inject(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {  // user picked a color
            
            // get color from intent
            int colorId = data.getIntExtra(COLOR_PICK_INTENT_KEY, Color.UNKNOWN.id);
            Color color = Color.fromId(colorId);
            
            // set color in note object
            note.getNoteMeta().setColor(color);
            
            // update UI
            onNoteColorChanged();
        }
    }
    
    /**
     * This method should be called once in the beginning
     * and then every time the note color changes.
     */
    private void onNoteColorChanged() {
        
        // get color from note (or UNKNOWN if no note available)
        Color color = note != null ? note.getNoteMeta().getColor() : Color.UNKNOWN;
        
        int androidColor = android.graphics.Color.rgb(color.red, color.green, color.blue);
        
        // change *both* buttons (edit mode and preview mode)
        noteColorButtonEdit.getBackground().setColorFilter(androidColor, PorterDuff.Mode.MULTIPLY);
        noteColorButtonView.getBackground().setColorFilter(androidColor, PorterDuff.Mode.MULTIPLY);
    }

    /**
     * Handles what should be visible.
     * @param typeID either EDIT_NOTE_TYPE_ID or PREVIEW_NOTE_TYPE_ID
     */
    private void setMode(int typeID)
    {
        if(typeID == EDIT_NOTE_TYPE_ID)
        {
            noteTitleEditText.setText(note.getNoteMeta().getTitle());
            noteEditText.setText(note.getNoteContent().getText());
        }
        else
        {
            noteTitleTextView.setText(note.getNoteMeta().getTitle());

            String htmlString = getHTMLFromMarkdown(
                    note.getNoteContent().getText().toString(),
                    cssStyleSource);

            // baseUrl is necessary in order to catch links in simpler format
            // otherwise, only links like https://www.google.com would be caught, but not www.google.de
            noteWebView.loadDataWithBaseURL(baseUrl, htmlString, "text/html", "utf-8", null);
        }

        setLayoutType(typeID);
    }
    
    private String getHTMLFromMarkdown(String markdownSource, String cssSource) {
        try {
            String htmlString = markdown4jProcessor.process(markdownSource);
            htmlString = String.format(
                    "<html><head><style>%s</style></head><body>%s</body></html>",  // build a website with styling
                    cssSource, htmlString);
            return htmlString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
    
    @Override
    public void onBackPressed() {
        if (noteEditLayout.getVisibility() == View.VISIBLE) {
            finishEditing();
        } else {
            super.onBackPressed();
        }
    }
    
    private void finishEditing() {
        try {
    
            // apply changes
    
            NoteMeta meta = note.getNoteMeta();
            meta.setTitle(noteTitleEditText.getText().toString());
            //meta.setColor();  //TODO
            meta.setLastEditTime(System.currentTimeMillis());
            
            note.getNoteContent().setText(new SpannableString(noteEditText.getText()));

            m_NoteManager.save(note);
            
        } catch (JSONException e) {
            Log.e(TAG, "finished editing: saving note failed: ", e);
            Toast.makeText(this, "failed to save note", Toast.LENGTH_SHORT).show();
        }

        setMode(PREVIEW_NOTE_TYPE_ID);
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
            //Open ColorPickingActivity
        }

        return super.onOptionsItemSelected(item);
    }

}
