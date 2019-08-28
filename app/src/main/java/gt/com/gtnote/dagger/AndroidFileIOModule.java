package gt.com.gtnote.dagger;


import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gt.com.gtnote.Interfaces.FileIO;
import gt.com.gtnote.Models.AndroidFileIO;

@Module
public class AndroidFileIOModule
{
    @Provides
    @Singleton
    FileIO provideFileIO(Context context)
    {
        return new AndroidFileIO(context);
    }
}
