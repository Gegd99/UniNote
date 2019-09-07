package gt.com.uninote.dagger;

import javax.inject.Singleton;

import dagger.Component;
import gt.com.uninote.EditNoteActivity;
import gt.com.uninote.MainActivity;

@Singleton
@Component(modules = {ManagersModule.class, AndroidFileIOModule.class, ContextModule.class})
public interface ManagersComponent
{
    void inject(MainActivity mainActivity);
    void inject(EditNoteActivity editNoteActivity);
}
