package gt.com.gtnote;

import android.app.Application;

import gt.com.gtnote.dagger.ContextModule;
import gt.com.gtnote.dagger.DaggerNoteManagerComponent;
import gt.com.gtnote.dagger.DaggerSettingsManagerComponent;
import gt.com.gtnote.dagger.NoteManagerComponent;
import gt.com.gtnote.dagger.SettingsManagerComponent;

/**
 * Custom Application class, to instantiate things over the whole lifetime of the app.
 */
public class ApplicationClass extends Application
{
    private NoteManagerComponent m_NoteManagerComponent;
    private SettingsManagerComponent m_SettingsManagerComponent;

    @Override
    public void onCreate()
    {
        super.onCreate();

        m_NoteManagerComponent = DaggerNoteManagerComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
        m_SettingsManagerComponent = DaggerSettingsManagerComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
    }

    public NoteManagerComponent getNoteManagerComponent()
    {
        return m_NoteManagerComponent;
    }
    public SettingsManagerComponent getSettingsManagerComponent()
    {
        return m_SettingsManagerComponent;
    }
}
