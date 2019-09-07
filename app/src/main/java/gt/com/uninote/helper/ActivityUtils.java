package gt.com.uninote.helper;

import android.content.res.Resources;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import gt.com.uninote.Models.Note;
import gt.com.uninote.Models.NoteManager;
import gt.com.uninote.Models.NoteMeta;
import gt.com.uninote.Models.SubModels.Color;
import gt.com.uninote.R;

public class ActivityUtils {
    
    private static final String TAG = "GTNOTE";
    
    /**
     * You should not call this before Activity.onCreate()
     */
    public static String readRawTextFile(int resourceId, Resources res) {
        try (InputStream in = res.openRawResource(resourceId)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                builder.append("\n");
                line = reader.readLine();
            }
            return builder.toString();
        } catch (IOException e) {
            Log.e(TAG, "readRawTextFile: error while reading resource of id="+resourceId, e);
            return "";
        }
    }
    
    public static void generateDemoNote(NoteManager noteManager, Resources res) {
        Note note = noteManager.createNote();
        NoteMeta meta = note.getNoteMeta();
        meta.setColor(Color.GREEN);
        String text = ActivityUtils.readRawTextFile(R.raw.demo_note, res);
        String title = res.getString(R.string.demo_note_title);
        String preview = res.getString(R.string.demo_note_preview);
        note.getNoteContent().setText(text);
        meta.setTitle(title);
        meta.setPreviewNoteContent(preview);
        try {
            noteManager.save(note);
        } catch (JSONException e) {
            Log.e(TAG, "An error occured while saving the demo file", e);
        }
    }
}
