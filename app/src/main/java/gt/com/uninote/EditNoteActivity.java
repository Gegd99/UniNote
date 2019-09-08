package gt.com.uninote;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.markdown4j.Markdown4jProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Inject;

import gt.com.uninote.Models.Managers;
import gt.com.uninote.Adapters.ColorSpinnerAdapter;
import gt.com.uninote.Models.Note;
import gt.com.uninote.Models.NoteManager;
import gt.com.uninote.Models.NoteMeta;
import gt.com.uninote.Models.SubModels.Color;
import gt.com.uninote.Models.TextEditOperations;
import gt.com.uninote.dagger.ManagersComponent;
import gt.com.uninote.helper.ActivityUtils;

import static gt.com.uninote.statics.Constants.COLOR_PICK_INTENT_KEY;
import static gt.com.uninote.statics.Constants.EDIT_NOTE_TYPE_ID;
import static gt.com.uninote.statics.Constants.LINK_NOTE_INTENT_KEY;
import static gt.com.uninote.statics.Constants.MAIN_EDIT_INTENT_NOTE_ID_KEY;
import static gt.com.uninote.statics.Constants.MAIN_EDIT_INTENT_TYPE_ID_KEY;
import static gt.com.uninote.statics.Constants.PREVIEW_NOTE_TYPE_ID;

public class EditNoteActivity extends AppCompatActivity {
    
    private static final String TAG = "GTNOTE";

    @Inject Managers m_Managers;
    private NoteManager m_NoteManager;
    private Note note;
    
    private View baseView;
    private LinearLayout noteViewLayout;
    private WebView noteWebView;
    private LinearLayout noteEditLayout;
    private EditText noteEditText;
    private EditText noteTitleEditText;
    private TextView noteTitleTextView;
    private Spinner colorSpinner;
    private Spinner colorSpinnerPreview;
    private ViewGroup noteHeaderEditMode;
    private ViewGroup noteHeaderViewMode;
    private View bottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheetPeekView;
    private FloatingActionButton bottomSheetFAB;
    
    private TextEditOperations textEditOperations = new TextEditOperations();
    
    private Markdown4jProcessor markdown4jProcessor = new Markdown4jProcessor();
    private String cssStyleSource;  // this style will be applied to the WebView showing the parsed NoteContent
    private String syntaxHighlightingJavascriptSource;
    private String syntaxHighlightingCssSource;
    private String baseUrl = "uninote://uninote.com/";  // random prefix for all links to fix detection of link clicks

    private int requestCodeLinkNote = 1;

    // font scaling:
    private int currentFontSize = 24;
    private float requestedFontSize = currentFontSize;
    private int minRequestableFontSize = 8;
    private int maxRequestableFontSize = 64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
    
        // make it possible to change the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    
        cssStyleSource = getString(R.string.note_webview_css);
        syntaxHighlightingJavascriptSource = ActivityUtils.readRawTextFile(R.raw.prism_js, getResources());
        syntaxHighlightingCssSource = ActivityUtils.readRawTextFile(R.raw.prism_css, getResources());

        findViews();
        buildBottomSheet();
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
    
        attachListeners();
        initColorSpinner();
        
        onNoteColorChanged();
    }

    /**
     * Method in every activity, which finds all View-Elements by their Id
     */
    private void findViews(){
        //Toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        
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
        
        colorSpinner = findViewById(R.id.noteEditColorSpinner);
        colorSpinnerPreview = findViewById(R.id.noteEditColorSpinnerPreview);
    
        noteHeaderEditMode = findViewById(R.id.noteHeaderEditMode);
        noteHeaderViewMode = findViewById(R.id.noteHeaderViewMode);
        
        bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setSkipCollapsed(true);
    
        bottomSheetPeekView = findViewById(R.id.bottom_sheet_peek_view);
        
        // wait for views to inflate, then query height
        bottomSheetPeekView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                bottomSheetBehavior.setPeekHeight(bottomSheetPeekView.getMeasuredHeight());
                
                bottomSheetPeekView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        
        bottomSheetFAB = findViewById(R.id.bottom_sheet_fab);
    }

    /**
     * Method in every activity which sets all ViewListeners.
     */
    private void attachListeners()
    {
        // listener for touch events
        View.OnTouchListener doubleTabEditListener = new View.OnTouchListener() {
            
            // detect double tap
            private GestureDetector gestureDetector = new GestureDetector(EditNoteActivity.this, new GestureDetector.SimpleOnGestureListener() {
                
                // this method only triggers when too much time passed for a double tap to happen
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    Toast.makeText(EditNoteActivity.this, R.string.double_tap_to_edit, Toast.LENGTH_SHORT).show();
                    return super.onSingleTapConfirmed(e);
                }
    
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
            
            // detect pinch (zoom)
            private ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(EditNoteActivity.this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    
                    requestedFontSize *= detector.getScaleFactor();
                    if (requestedFontSize < minRequestableFontSize) {
                        requestedFontSize = minRequestableFontSize;
                    } else if (requestedFontSize > maxRequestableFontSize) {
                        requestedFontSize = maxRequestableFontSize;
                    }
                    
                    int requestedFontSizeResult = (int) requestedFontSize;  // only whole numbers
                    
                    // only update UI if necessary
                    if (requestedFontSizeResult != currentFontSize) {
                        currentFontSize = requestedFontSizeResult;
                        onFontSizeChanged();
                    }
                    
                    return true;
                }
            });
            
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if (noteViewLayout.getVisibility() == View.VISIBLE) {  // just making sure... (probably not necessary)
                    gestureDetector.onTouchEvent(event);
                    scaleGestureDetector.onTouchEvent(event);
                }
                return false;
            }
        };
    
        baseView.setOnTouchListener(doubleTabEditListener);
        noteWebView.setOnTouchListener(doubleTabEditListener);  // for some reason baseView doesn't catch events on that WebView
    
        AdapterView.OnItemSelectedListener colorSpinnerListener = new AdapterView.OnItemSelectedListener() {
    
            private int selectionCount = 0;
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (selectionCount++ > 0) {  // avoid trigger during initialization
                    Color color = (Color) adapterView.getItemAtPosition(i);
                    note.getNoteMeta().setColor(color);
                    onNoteColorChanged();
                    
                    // only necessary for respective other colorSpinner (but conveniently just set both):
                    colorSpinner.setSelection(i, false);
                    colorSpinnerPreview.setSelection(i, false);
                }
            }
    
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        };
        colorSpinner.setOnItemSelectedListener(colorSpinnerListener);
        colorSpinnerPreview.setOnItemSelectedListener(colorSpinnerListener);
        
        // allow javascript execution for syntax highlighting
        noteWebView.getSettings().setJavaScriptEnabled(true);
        
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
        
        bottomSheetPeekView.setOnClickListener(
                view -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)
        );
        
        bottomSheetFAB.setOnClickListener(view -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }
    
    private void initColorSpinner() {
        Color[] colors = Color.getAllNormal();
        ColorSpinnerAdapter adapter = new ColorSpinnerAdapter(this, colors);
        
        colorSpinner.setAdapter(adapter);
        colorSpinnerPreview.setAdapter(adapter);
    
        int position = adapter.getPosition(note.getNoteMeta().getColor());
        if (position == -1) {
            // should actually never happen,
            // but will happen during dev since we earlier used UNKNOWN as default value
            note.getNoteMeta().setColor(m_NoteManager.getDefaultNoteColor());
            Toast.makeText(this, "set to default color", Toast.LENGTH_SHORT).show();
        }
        colorSpinner.setSelection(position, false);
        colorSpinnerPreview.setSelection(position, false);
    }
    
    /**
     * Dynamically creates all the buttons etc. in bottom sheet
     */
    private void buildBottomSheet() {
        
        BottomSheetLayoutCreator b = new BottomSheetLayoutCreator(
                getResources(),
                getLayoutInflater(),
                findViewById(R.id.bottom_sheet_root),
                view -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)
        );
    
        buildBottomSheetMarkdown(b);
        buildBottomSheetLaTeX(b);
        buildBottomSheetCodeSnippetsJava(b);
    }
    
    private void buildBottomSheetMarkdown(BottomSheetLayoutCreator b) {
        b.beginCategory("Markdown");
        b.addButton(R.drawable.icon_format_bold, null, view -> surroundWithElements(noteEditText, "**", "**"));
        b.addButton(R.drawable.icon_format_italic, null, view -> surroundWithElements(noteEditText, "*", "*"));
        b.addButton(R.drawable.icon_format_bullet_list, "List", view -> surroundWithElements(noteEditText, "- ", ""));
        b.addButton(R.drawable.icon_format_headline, "Headline", view -> surroundWithElements(noteEditText, "# ", ""));
        b.addButton(R.drawable.icon_format_link_website, "Link", view -> surroundWithElements(noteEditText, "[", "](www.example.com)"));
        b.addButton(R.drawable.icon_format_quote, "Quote", view -> surroundWithElements(noteEditText, "> ", ""));
        b.addButton(R.drawable.icon_format_code, "Code", view -> surroundWithElements(noteEditText, "```lang-", "\n```"));
        b.addButton(R.drawable.icon_format_link_note, "Note", view -> {
            if (m_NoteManager.getNotes().size() == 1)
            {
                Toast.makeText(this, "This is your only note", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(this, LinkNoteActivity.class);
                intent.putExtra("noteId", note.getNoteMeta().getNoteId());
                startActivityForResult(intent, requestCodeLinkNote);
            }
        });
    }
    
    /**
     * only as design demonstration
     * @param b
     */
    private void buildBottomSheetLaTeX(BottomSheetLayoutCreator b) {
        b.beginCategory("LaTeX");
        b.addButton(R.drawable.icon_format_code, "Formula", null);
        b.addButton(R.drawable.icon_format_link_website, "Math Stuff", null);
    }
    
    /**
     * only as design demonstration
     * @param b
     */
    private void buildBottomSheetCodeSnippetsJava(BottomSheetLayoutCreator b) {
        b.beginCategory("Code Snippets: Java");
        b.addButton(R.drawable.icon_format_code, "for i", null);
        b.addButton(R.drawable.icon_format_code, "try, catch, finally", null);
        b.addButton(R.drawable.icon_format_code, "System.out.println", null);
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
    
        String text = editText.getText().toString();
        int start = editText.getSelectionStart();
        
        if (editText.hasSelection()) {
            
            // surround the selected part with the elements and
            // place the cursor at the end of the original selected text
            
            int end = editText.getSelectionEnd();
            
            editText.setText(textEditOperations.surroundSelection(text, start, end, elementBefore, elementAfter));
            
            int cursorPosition = end + elementBefore.length();
            editText.setSelection(cursorPosition, cursorPosition);
            
        } else {  // no selection, just cursor in text
            // "start" is cursor position
            
            // check whether cursor is inside a word
            if (textEditOperations.isCursorTouchingWord(text, start)) {
                
                // find surrounding indices and treat them like selection start & end
                
                int end = textEditOperations.findWordEnd(text, start) + 1;
                start = textEditOperations.findWordBeginning(text, start);
    
                editText.setText(textEditOperations.surroundSelection(text, start, end, elementBefore, elementAfter));
    
                int cursorPosition = end + elementBefore.length();
                editText.setSelection(cursorPosition, cursorPosition);
                
            } else {
                // insert the two elements and place the cursor between them
    
                editText.setText(textEditOperations.insert(text, start, elementBefore+elementAfter));
    
                int cursorPosition = start + elementBefore.length();
                editText.setSelection(cursorPosition, cursorPosition);
            }
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
                        Toast.makeText(this, "The linked note does not exist", Toast.LENGTH_SHORT).show();
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
        ManagersComponent managersComponent = ((ApplicationClass) getApplication()).getManagersComponent();

        managersComponent.inject(this);

        m_NoteManager = m_Managers.getNoteManager();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        if (requestCode == 1 && resultCode == RESULT_OK) {  // user picked a color
            
            // get color from intent
            int colorId = data.getIntExtra(COLOR_PICK_INTENT_KEY, Color.UNKNOWN.id);
            Color color = Color.fromId(colorId);
            
            // set color in note object
            note.getNoteMeta().setColor(color);
            
            // update UI
            onNoteColorChanged();
        }
        */
        if (requestCode == requestCodeLinkNote && resultCode == RESULT_OK) {

            int noteId = data.getIntExtra(LINK_NOTE_INTENT_KEY, -1);
            Note linkedNote = m_NoteManager.getById(noteId);
            String noteTitle = linkedNote.getNoteMeta().getTitle();

            String textToInsert = String.format("[%s](%d)", noteTitle.isEmpty() ? "unnamed" : noteTitle, noteId);

            surroundWithElements(noteEditText, textToInsert, "");
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
        
        // change layout in edit mode and in preview mode
        noteHeaderEditMode.getBackground().setColorFilter(androidColor, PorterDuff.Mode.MULTIPLY);
        noteHeaderViewMode.getBackground().setColorFilter(androidColor, PorterDuff.Mode.MULTIPLY);
        bottomSheet.getBackground().setColorFilter(androidColor, PorterDuff.Mode.MULTIPLY);
        // force android to render changes
        noteHeaderViewMode.invalidate();
        noteHeaderEditMode.invalidate();
        bottomSheet.invalidate();
    
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(androidColor);
        }
    }

    /**
     * Handles what should be visible.
     * @param typeID either EDIT_NOTE_TYPE_ID or PREVIEW_NOTE_TYPE_ID
     */
    private void setMode(int typeID) {
        
        if (typeID == EDIT_NOTE_TYPE_ID) {
            
            noteTitleEditText.setText(note.getNoteMeta().getTitle());
            noteEditText.setText(note.getNoteContent().getText());
            noteEditText.requestFocus();
            
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            bottomSheetFAB.setVisibility(View.VISIBLE);
            
        } else {
            
            noteTitleTextView.setText(note.getNoteMeta().getTitle());

            String htmlString = getHTMLFromMarkdown(note.getNoteContent().getText());

            // baseUrl is necessary in order to catch links in simpler format
            // otherwise, only links like https://www.google.com would be caught, but not www.google.de
            noteWebView.loadDataWithBaseURL(baseUrl, htmlString, "text/html", "utf-8", null);
            
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            bottomSheetFAB.setVisibility(View.GONE);
        }

        setLayoutType(typeID);
    }
    
    private String getHTMLFromMarkdown(String markdownSource) {
        try {
            String htmlString = markdown4jProcessor.process(markdownSource);
            
            boolean syntaxHighlightingRequired = htmlString.contains("</code>");
    
            // build html website with CSS and JS
            String fullHtml = "<html>"
                    + "<head>"
                    + "<style>" + cssStyleSource + "</style>"
                    + (syntaxHighlightingRequired ? "<style>" + syntaxHighlightingCssSource + "</style>" : "")
                    + "</head>"
                    + "<body>"
                    + htmlString
                    + "<script>" + getFontSizeJavascript() + "</script>"
                    + (syntaxHighlightingRequired ? "<script>" + syntaxHighlightingJavascriptSource + "</script>" : "")
                    + "</body>"
                    + "</html>";
            
            return fullHtml;
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void onFontSizeChanged() {
        
        if (noteViewLayout.getVisibility() == View.VISIBLE) {
            
            // inject javascript code for changing font size
            noteWebView.loadUrl("javascript:"+getFontSizeJavascript());
        }
    }
    
    /**
     * This can be injected into the WebView in
     * order to adapt the font of the body element.
     *
     * @return the javascript code that will change the font size upon execution
     */
    private String getFontSizeJavascript() {
        String template= "(function() {document.getElementsByTagName(\"body\")[0].style.fontSize = \"%spx\";})()";
        return String.format(template, String.valueOf(currentFontSize));
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
        
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            
        } else if (noteEditLayout.getVisibility() == View.VISIBLE) {
            
            finishEditing();
            
        } else {
            if(note.getNoteMeta().getTitle().equals("") && note.getNoteContent().getText().equals(""))
            {
                m_NoteManager.delete(note);
                Toast.makeText(this, "Empty note discarded", Toast.LENGTH_SHORT).show();
            }
            super.onBackPressed();
        }
    }
    
    private void finishEditing() {
        try {

            // apply changes


            //Update NoteMeta
            NoteMeta meta = note.getNoteMeta();
            meta.setTitle(noteTitleEditText.getText().toString());
            meta.setLastEditTime(System.currentTimeMillis());
            meta.setPreviewNoteContent(textEditOperations.cutToRandomLength(noteEditText.getText().toString(), meta.getNoteId()));

            //Update NoteContent
            note.getNoteContent().setText(noteEditText.getText().toString());

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
}
