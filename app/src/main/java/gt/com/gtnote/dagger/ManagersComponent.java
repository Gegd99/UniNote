package gt.com.gtnote.dagger;

import javax.inject.Singleton;

import dagger.Component;
import gt.com.gtnote.EditNoteActivity;
import gt.com.gtnote.MainActivity;

@Singleton
@Component(modules = {ManagersModule.class, AndroidFileIOModule.class, ContextModule.class})
public interface ManagersComponent
{
    void inject(MainActivity mainActivity);
    void inject(EditNoteActivity editNoteActivity);
}
