package gt.com.uninote.Models;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Managers
{
    private NoteManager m_NoteManager;

    @Inject
    public Managers(NoteManager noteManager)
    {
        m_NoteManager = noteManager;
    }

    public NoteManager getNoteManager()
    {
        return m_NoteManager;
    }
}

