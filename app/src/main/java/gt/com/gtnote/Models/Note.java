package gt.com.gtnote.Models;

public class Note
{
    private NoteContent noteContent;
    private NoteMeta noteMeta;

    public Note()
    {
        noteContent = new NoteContent();
        noteMeta = new NoteMeta();
    }

    public Note(NoteContent noteContent, NoteMeta noteMeta)
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
