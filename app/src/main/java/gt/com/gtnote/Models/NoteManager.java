package gt.com.gtnote.Models;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NoteManager {
    
    private static final String META_FILE_NAME = "meta.json";
    
    private FileIO fileIO;
    private NoteMetaParser metaParser = new NoteMetaParser();
    private ArrayList<Note> notes = new ArrayList<>();
    
    public NoteManager(FileIO fileIO) throws JSONException {
        this.fileIO = fileIO;
        loadAll();
    }
    
    /**
     * reads all notes from the storage directory
     * and stores them internally as Note objects.
     */
    private void loadAll() throws JSONException {
        
        if (fileIO.fileExists(META_FILE_NAME)) {
            String allMetasString = fileIO.read(META_FILE_NAME);  // meta data of all notes is stored in one file (faster reading)
            JSONArray allMetas = new JSONArray(allMetasString);  // could throw JSONException
    
            for (int i = 0; i < allMetas.length(); i++) {
        
                JSONObject meta = allMetas.getJSONObject(i);
                Note note = createNote(meta);
                notes.add(note);
            }
        } else {
            // create meta file
            String source = new JSONArray().toString();  // empty array
            fileIO.write(META_FILE_NAME, source);
        }
    }
    
    /**
     * Creates a Note object.
     * @param metaJSONObject a JSON object with the meta data
     * @return a fully initialized Note object (content will load when queried)
     * @throws JSONException if the json string couldn't be parsed
     */
    Note createNote(JSONObject metaJSONObject) throws JSONException {
        
        NoteMeta meta = metaParser.loadMeta(metaJSONObject);  // could throw JSONException
        
        // meta tells where the content lies
        FilePointer contentFilePointer = new FilePointer(filePathFromNoteId(meta.getNoteId()), fileIO);
        NoteContent content = new NoteContent(contentFilePointer);  // this will not read the content until queried
        
        return new Note(meta, content);
    }
    
    public ArrayList<Note> getAll() {
        return notes;
    }
    
    public void save(Note note) throws JSONException {
        saveMeta(note.getNoteMeta());
        saveContent(note);  // also needs to access meta
    }
    
    private void saveMeta(NoteMeta meta) throws JSONException {
        // update new meta in meta file
        
        // TODO: takes linear time -> make mapping for const. time, so that it's still fast with many notes
    
        String allMetasString = fileIO.read(META_FILE_NAME);
        JSONArray allMetas = new JSONArray(allMetasString);
    
        for (int i = 0; i < allMetas.length(); i++) {
            JSONObject metaJSONObject = allMetas.getJSONObject(i);
            NoteMeta otherMeta = metaParser.loadMeta(metaJSONObject);
        
            if (otherMeta.getNoteId() == meta.getNoteId()) {
                metaJSONObject = metaParser.dumpMeta(meta);  // may throw JSONException
                allMetas.put(i, metaJSONObject);
                break;
            }
        }
    
        allMetasString = allMetas.toString();
        fileIO.write(META_FILE_NAME, allMetasString);
    }
    
    private void saveContent(Note note) {
        
        Spanned spanned = note.getNoteContent().getSpanned();
        String source = null;
        if (Build.VERSION.SDK_INT >= 24) {
            source = Html.toHtml(spanned, Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE);
            // maybe Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL is better
        } else {
            source = Html.toHtml(spanned);
        }
        
        String contentFilePath = filePathFromNoteId(note.getNoteMeta().getNoteId());
        fileIO.write(contentFilePath, source);
    }
    
    private String filePathFromNoteId(int noteId) {
        return String.format("%d.html", noteId);
    }

    public List<Note> getNotes()
    {
        return notes;
    }
}
