package gt.com.uninote.Models;

import gt.com.uninote.Models.SubModels.Color;

public class NoteMeta
{
    private int noteId;
    private String title;
    private String previewNoteContent;
    private Color color;
    private long creationTime;  // unix timestamp (in milliseconds)
    private long lastEditTime;  // unix timestamp (in milliseconds)
    
    NoteMeta(int noteId, String title, String previewNoteContent ,Color color, long creationTime, long lastEditTime) {
        this.noteId = noteId;
        this.title = title;
        this.color = color;
        this.creationTime = creationTime;
        this.lastEditTime = lastEditTime;
        this.previewNoteContent = previewNoteContent;
    }
    
    public int getNoteId() {
        return noteId;
    }
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreviewNoteContent() {
        return previewNoteContent;
    }

    public void setPreviewNoteContent(String previewNoteContent) {
        this.previewNoteContent = previewNoteContent;
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

    public long getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(long lastEditTime) {
        this.lastEditTime = lastEditTime;
    }
}
