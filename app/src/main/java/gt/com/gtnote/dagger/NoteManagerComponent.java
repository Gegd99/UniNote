package gt.com.gtnote.dagger;

import javax.inject.Singleton;

import dagger.Component;
import gt.com.gtnote.EditNoteActivity;
import gt.com.gtnote.MainActivity;
import gt.com.gtnote.Models.NoteManager;

@Singleton
@Component(modules = {AndroidFileIOModule.class, ContextModule.class})
public interface NoteManagerComponent
{
    void inject(MainActivity mainActivity);

    void inject(EditNoteActivity editNoteActivity);
}
