package gt.com.gtnote.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gt.com.gtnote.Interfaces.FileIO;
import gt.com.gtnote.Models.NoteManager;
import gt.com.gtnote.Models.SettingsManager;

@Module
public class ManagersModule
{
    @Provides
    @Singleton
    SettingsManager provideSettingsManager(FileIO fileIO)
    {
        return new SettingsManager(fileIO);
    }

    @Provides
    @Singleton
    NoteManager provideNoteManager(FileIO fileIO)
    {
        return new NoteManager(fileIO);
    }


}
