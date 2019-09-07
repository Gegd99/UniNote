package gt.com.uninote.dagger;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gt.com.uninote.Interfaces.FileIO;
import gt.com.uninote.Models.NoteManager;
import gt.com.uninote.Models.SettingsManager;

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
