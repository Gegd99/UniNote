package gt.com.gtnote.dagger;

import android.content.Context;

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
    Context provideContext()
    {
        return m_Context;
    }
}
