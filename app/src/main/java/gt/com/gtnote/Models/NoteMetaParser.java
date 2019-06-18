package gt.com.gtnote.Models;

import org.json.JSONException;
import org.json.JSONObject;

import gt.com.gtnote.Models.SubModels.Color;

class NoteMetaParser {
    
    // all keys for json object
    private static final String KEY_META_NOTE_ID = "id";
    private static final String KEY_META_TITLE = "title";
    private static final String KEY_META_CREATION_TIME = "creation";
    private static final String KEY_META_LAST_EDIT_TIME = "last_edit";
    private static final String KEY_META_COLOR = "color";
    
    NoteMeta loadMeta(JSONObject o) throws JSONException {
        
        int id = o.getInt(KEY_META_NOTE_ID);
        String title = o.getString(KEY_META_TITLE);
        Color color = Color.fromId(o.getInt(KEY_META_COLOR));
        long creationTime = o.getLong(KEY_META_CREATION_TIME);
        long lastEditTime = o.getLong(KEY_META_LAST_EDIT_TIME);
        
        return new NoteMeta(id, title, color, creationTime, lastEditTime);
    }
    
    JSONObject dumpMeta(NoteMeta meta) throws JSONException {
        JSONObject o = new JSONObject();
        
        o.put(KEY_META_NOTE_ID, meta.getNoteId());
        o.put(KEY_META_TITLE, meta.getTitle());
        o.put(KEY_META_COLOR, meta.getColor().id);
        o.put(KEY_META_CREATION_TIME, meta.getCreationTime());
        o.put(KEY_META_LAST_EDIT_TIME, meta.getLastEditTime());
        
        return o;
    }
}
