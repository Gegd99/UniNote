package gt.com.uninote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import javax.inject.Inject;

import gt.com.uninote.Models.Managers;
import gt.com.uninote.Models.NoteManager;
import gt.com.uninote.Models.SettingsManager;
import gt.com.uninote.dagger.ManagersComponent;
import gt.com.uninote.helper.ActivityUtils;

public class GeneralSettingsActivity extends AppCompatActivity {
    
    private Button generateDemoNoteButton;
    private boolean demoNoteGenerated = false;
    
    @Inject
    Managers m_Managers;
    private NoteManager m_NoteManager;
    private SettingsManager m_SettingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_settings);

        setTitle("Settings");
        
        findViews();
        injectManagers();
        attachListeners();
    }
    
    private void findViews() {
        generateDemoNoteButton = findViewById(R.id.generate_demo_note_button);
    }
    
    private void attachListeners() {
        generateDemoNoteButton.setOnClickListener(view -> {
            if (!demoNoteGenerated) {
                ActivityUtils.generateDemoNote(m_NoteManager, getResources());
                demoNoteGenerated = true;
            }
        });
    }
    
    public void injectManagers() {
        ManagersComponent managersComponent = ((ApplicationClass) getApplication()).getManagersComponent();
        
        managersComponent.inject(this);
        
        m_NoteManager = m_Managers.getNoteManager();
        m_SettingsManager = m_Managers.getSettingsManager();
    }
}
