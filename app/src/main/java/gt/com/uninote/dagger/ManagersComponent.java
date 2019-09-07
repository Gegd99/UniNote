package gt.com.uninote.dagger;

import javax.inject.Singleton;

import dagger.Component;
import gt.com.uninote.EditNoteActivity;
import gt.com.uninote.GeneralSettingsActivity;
import gt.com.uninote.LinkNoteActivity;
import gt.com.uninote.MainActivity;

@Singleton
@Component(modules = {ManagersModule.class, AndroidFileIOModule.class, ContextModule.class})
public interface ManagersComponent
{
    void inject(MainActivity mainActivity);
    void inject(EditNoteActivity editNoteActivity);
    void inject(LinkNoteActivity linkNoteActivity);
    void inject(GeneralSettingsActivity generalSettingsActivity);
}
