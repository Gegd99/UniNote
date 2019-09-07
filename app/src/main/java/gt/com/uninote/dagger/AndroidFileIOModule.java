package gt.com.uninote.dagger;


import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import gt.com.uninote.Interfaces.FileIO;
import gt.com.uninote.Models.AndroidFileIO;

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
