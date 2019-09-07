package gt.com.uninote.dagger;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule
{
    private Context m_Context;

    public ContextModule(Context context)
    {
        m_Context = context;
    }

    @Provides
    @Singleton
    Context provideContext()
    {
        return m_Context;
    }
}
