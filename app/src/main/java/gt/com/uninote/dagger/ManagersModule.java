package gt.com.uninote.dagger;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gt.com.uninote.Interfaces.FileIO;
import gt.com.uninote.Models.NoteManager;
import gt.com.uninote.Models.SettingsManager;

@Module
public class ManagersModule {

    @Provides
    @Singleton
    NoteManager provideNoteManager(FileIO fileIO)
    {
        return new NoteManager(fileIO);
    }


}
