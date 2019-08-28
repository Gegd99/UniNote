package gt.com.gtnote.Models;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Managers
{
    private NoteManager m_NoteManager;
    private SettingsManager m_SettingsManager;

    @Inject
    public Managers(NoteManager noteManager, SettingsManager settingsManager)
    {
        m_NoteManager = noteManager;
        m_SettingsManager = settingsManager;
    }

    public NoteManager getNoteManager()
    {
        return m_NoteManager;
    }

    public SettingsManager getSettingsManager()
    {
        return m_SettingsManager;
    }
}

