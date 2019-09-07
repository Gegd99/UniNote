package gt.com.uninote.Models;

import gt.com.uninote.Interfaces.NoteContent;

public class Note
{
    private NoteContent noteContent;
    private NoteMeta noteMeta;

    Note(NoteMeta noteMeta, NoteContent noteContent)
    {
        this.noteContent = noteContent;
        this.noteMeta = noteMeta;
    }

    public NoteContent getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(NoteContent noteContent) {
        this.noteContent = noteContent;
    }

    public NoteMeta getNoteMeta() {
        return noteMeta;
    }

    public void setNoteMeta(NoteMeta noteMeta) {
        this.noteMeta = noteMeta;
    }
}
