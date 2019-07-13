package gt.com.gtnote.dagger;

import javax.inject.Singleton;

import dagger.Component;
import gt.com.gtnote.MainActivity;
import gt.com.gtnote.Models.NoteManager;

@Component(modules = {AndroidFileIOModule.class, ContextModule.class})
public interface NoteManagerComponent
{
    void inject(MainActivity mainActivity);
}
