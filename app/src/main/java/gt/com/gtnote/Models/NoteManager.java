package gt.com.gtnote.Models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import gt.com.gtnote.Interfaces.FileIO;
import gt.com.gtnote.Interfaces.NoteContent;
import gt.com.gtnote.Models.SubModels.Color;

@Singleton
public class NoteManager {
    
    //TODO: this ugly singleton is just a temporary solution. EditNoteActivity should access a ViewModel
    private static NoteManager instance;//TODO: remove
    public static NoteManager getInstance() {//TODO: remove
        return instance;
    }
    
    private static final String TAG = "GTNOTE";

    private static final String META_FILE_NAME = "meta.json";
    
    private FileIO fileIO;
    private NoteMetaParser metaParser = new NoteMetaParser();
    private LinkedList<Note> notes = new LinkedList<>();

    @Inject
    public NoteManager(FileIO fileIO) {
        this.fileIO = fileIO;
        try {
            loadAll();
        } catch (JSONException e) {
            Log.e(TAG, String.format("An error occurred while loading the notes."));
            e.printStackTrace();
        }
        NoteManager.instance = this;  //TODO: remove this ugly line of code
    }
    
    /**
     * reads all notes from the storage directory
     * and stores them internally as Note objects.
     */
    private void loadAll() throws JSONException {

        if (!fileIO.fileExists(META_FILE_NAME)) {
            createMetaFile();
        }

        String allMetasString = fileIO.read(META_FILE_NAME);  // meta data of all notes is stored in one file (faster reading)
        JSONArray allMetas = new JSONArray(allMetasString);  // could throw JSONException

        Log.d(TAG, String.format("Loaded %d metas from string '''%s'''", allMetas.length(), allMetasString));

        for (int i = 0; i < allMetas.length(); i++) {

            JSONObject meta = allMetas.getJSONObject(i);
            Note note = createNote(meta);
            notes.add(note);
        }
    }
    
    /**
     * Creates a Note object.
     * @param metaJSONObject a JSON object with the meta data
     * @return a fully initialized Note object (content will load when queried)
     * @throws JSONException if the json string couldn't be parsed
     */
    private Note createNote(JSONObject metaJSONObject) throws JSONException {
        
        NoteMeta meta = metaParser.loadMeta(metaJSONObject);  // could throw JSONException
        
        // meta tells where the content lies
        FilePointer contentFilePointer = new FilePointer(filePathFromNoteId(meta.getNoteId()), fileIO);
        NoteContent content = new LazyNoteContent(contentFilePointer);
        
        return new Note(meta, content);
    }
    
    /**
     * Use this if you want to create a new Note object that does not exist as a file yet.<br>
     * Creates a Note object with a new ID
     * and adds it to the internal list (received with .getAll()) automatically.<br>
     * The ID is picked by choosing the lowest natural number (incl. 0) available
     * @return a new Note object with empty fields (besides a unique id)
     */
    public Note createNote() {
        NoteMeta meta = new NoteMeta(
                getLowestAvailableId(),
                "",
                "",
                Color.UNKNOWN,
                System.currentTimeMillis(),
                System.currentTimeMillis()
        );
        NoteContent content = new PresentNoteContent("");
        Note note = new Note(meta, content);
        insertNote(note);
        return note;
    }

    private int getLowestAvailableId() {
        // assuming that notes are ordered by their ID (Note.getNoteId) (ascending)
        int lowestId = 0;
        for (Note note: notes) {
            int id = note.getNoteMeta().getNoteId();
            if (id > lowestId) {
                return lowestId;
            } else if (id == lowestId) {
                lowestId = id + 1;
            } else {
                Log.w(TAG, "getLowestAvailableId: Strange ordering detected!");
            }
        }
        return lowestId;
    }

    /**
     * Inserts the note into the internal list so that the list ist ordered by IDs (ascending).
     * @param note the note to insert
     */
    private void insertNote(Note note) {
        int id = note.getNoteMeta().getNoteId();
        int index = 0;
        for (Note otherNote: notes) {
            if (otherNote.getNoteMeta().getNoteId() > id) {
                notes.add(index, note);
                return;
            }
            index++;
        }
        notes.add(note);  // no element with a higher ID was found (-> note has the highest id)
    }

    public List<Note> getAll() {
        return notes;
    }
    
    public Note getById(int noteId) {
        // TODO: do a binary search? (but then LinkedList is wrong data structure, but LinkedList is good for inserting...)
        for (Note note: notes) {
            if (note.getNoteMeta().getNoteId() == noteId) {
                return note;
            }
        }
        return null;
    }
    
    /**
     * creates or updates respective files
     * @param note
     * @throws JSONException
     */
    public void save(Note note) throws JSONException {
        saveMeta(note.getNoteMeta());
        saveContent(note);  // also needs to access meta
    }
    
    /**
     * Creates meta file if not existent. Updates meta object file if existent, otherwise adds one.
     * @param meta
     * @throws JSONException
     */
    private void saveMeta(NoteMeta meta) throws JSONException {
        // TODO: takes linear time -> make mapping for const. time?

        int length = 0;
        for (Note note: notes) {
            length++;
        }
        Log.d(TAG, String.format("saveMetas: %d internal note objects", length));
        
        if (!fileIO.fileExists(META_FILE_NAME)) {
            Log.w(TAG, "saveMeta: Meta file was deleted since NoteManager instance was created.");
            createMetaFile();
        }

        String allMetasString = fileIO.read(META_FILE_NAME);
        JSONArray allMetas = new JSONArray(allMetasString);

        JSONObject metaJSONObject = metaParser.dumpMeta(meta);  // may throw JSONException

        // if meta with id is already in this list: update it.
        // otherwise, add a new entry

        boolean metaFound = false;

        for (int i = 0; i < allMetas.length(); i++) {
            try
            {
                JSONObject otherMetaJSONObject = allMetas.getJSONObject(i);
                NoteMeta otherMeta = metaParser.loadMeta(otherMetaJSONObject);

                if (otherMeta.getNoteId() == meta.getNoteId()) {
                    allMetas.put(i, metaJSONObject);
                    metaFound = true;
                    break;
                }
            }
            catch (JSONException e)
            {
                Log.d(TAG, "A NoteMeta object is missing an entry: ", e);
                break;
            }
        }

        if (!metaFound) {
            // add a new entry
            allMetas.put(metaJSONObject);
        }

        allMetasString = allMetas.toString();

        Log.d(TAG, String.format("saveMetas: '''%s'''", allMetasString));

        fileIO.write(META_FILE_NAME, allMetasString);
    }
    
    /**
     * creates or updates respective content file
     * @param note
     */
    private void saveContent(Note note) {
        
        String source = note.getNoteContent().getText();
        
        String contentFilePath = filePathFromNoteId(note.getNoteMeta().getNoteId());
        fileIO.write(contentFilePath, source);
    }
    
    /**
     * Creates the meta file with an empty JSON array.
     */
    private void createMetaFile() {
        String emptyJSONArrayString = new JSONArray().toString();
        fileIO.write(META_FILE_NAME, emptyJSONArrayString);
    }

    private String filePathFromNoteId(int noteId) {
        return String.format("%d.html", noteId);
    }

    public void deleteAllFiles(String confirmationPhrase) {
        if (confirmationPhrase.equals("I know what I'm doing.")) {
            for (String fileOrDirName: fileIO.list()) {
                fileIO.delete(fileOrDirName);
            }
        }
    }

    public List<Note> getNotes()
    {
        return notes;
    }
}
