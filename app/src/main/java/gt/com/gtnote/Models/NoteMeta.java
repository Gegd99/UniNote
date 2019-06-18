package gt.com.gtnote.Models;

import gt.com.gtnote.Models.SubModels.Color;

public class NoteMeta
{
    private int noteId;
    private String title;
    private Color color;
    private long creationTime;  // unix timestamp
    private long lastEditTime;  // unix timestamp
    //private int noteType
    private int contentSize;  // todo: provide this value
    
    NoteMeta(int noteId, String title, Color color, long creationTime, long lastEditTime) {
        this.noteId = noteId;
        this.title = title;
        this.color = color;
        this.creationTime = creationTime;
        this.lastEditTime = lastEditTime;
    }
    
    public int getNoteId() {
        return noteId;
    }
    
    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(long lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public int getContentSize() {
        return contentSize;
    }

    public void setContentSize(int contentSize) {
        this.contentSize = contentSize;
    }
}
