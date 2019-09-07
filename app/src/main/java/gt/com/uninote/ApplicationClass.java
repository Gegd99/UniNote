package gt.com.uninote;

import android.app.Application;

import gt.com.uninote.dagger.ContextModule;
import gt.com.uninote.dagger.DaggerManagersComponent;
import gt.com.uninote.dagger.ManagersComponent;

/**
 * Custom Application class, to instantiate things over the whole lifetime of the app.
 */
public class ApplicationClass extends Application
{
    private ManagersComponent m_ManagersComponent;

    @Override
    public void onCreate()
    {
        super.onCreate();

        m_ManagersComponent = DaggerManagersComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
    }

    public ManagersComponent getManagersComponent()
    {
        return m_ManagersComponent;
    }
}
