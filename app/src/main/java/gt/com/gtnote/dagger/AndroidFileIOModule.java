package gt.com.gtnote.dagger;


import android.content.Context;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import gt.com.gtnote.Interfaces.FileIO;
import gt.com.gtnote.Models.AndroidFileIO;

@Module
public class AndroidFileIOModule
{
    @Provides
    FileIO provideFileIO(Context context)
    {
        return new AndroidFileIO(context);
    }
}
