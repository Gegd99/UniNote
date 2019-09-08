package gt.com.uninote;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import javax.inject.Inject;

import gt.com.uninote.Models.Managers;
import gt.com.uninote.Models.NoteManager;
import gt.com.uninote.Models.SettingsManager;
import gt.com.uninote.Models.SubModels.SortType;
import gt.com.uninote.dagger.ManagersComponent;
import gt.com.uninote.helper.ActivityUtils;

import static gt.com.uninote.statics.Constants.GENERAL_PREFERENCES_NAME;
import static gt.com.uninote.statics.Constants.PREFERENCE_SORT_TYPE;

public class GeneralSettingsActivity extends AppCompatActivity {
    
    private Button generateDemoNoteButton;
    private boolean demoNoteGenerated = false;
    
    @Inject
    Managers m_Managers;
    private NoteManager m_NoteManager;
    
    private SettingsManager m_SettingsManager;
    
    private RadioGroup sortTypeRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_settings);
    
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
    
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.settings_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        
        findViews();
        injectManagers();
        m_SettingsManager = new SettingsManager(getSharedPreferences(GENERAL_PREFERENCES_NAME, MODE_PRIVATE));
        setInitalState();
        attachListeners();
    }
    
    private void findViews() {
        generateDemoNoteButton = findViewById(R.id.generate_demo_note_button);
        sortTypeRadioGroup = findViewById(R.id.settings_sort_type_radio_group);
    }
    
    private void attachListeners() {
        generateDemoNoteButton.setOnClickListener(view -> {
            if (!demoNoteGenerated) {
                ActivityUtils.generateDemoNote(m_NoteManager, getResources());
                Toast.makeText(this, R.string.generate_demo_note_button_confirmation, Toast.LENGTH_SHORT).show();
                demoNoteGenerated = true;
            }
        });
        
        sortTypeRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (sortTypeRadioGroup.getCheckedRadioButtonId()) {
                
                case R.id.settings_sort_type_creation_time_radio_button:
                    m_SettingsManager.setSortType(SortType.CREATION_TIME);
                    break;
                case R.id.settings_sort_type_edit_time_radio_button:
                    m_SettingsManager.setSortType(SortType.LAST_EDIT_TIME);
                    break;
            }
        });
    }
    
    public void injectManagers() {
        ManagersComponent managersComponent = ((ApplicationClass) getApplication()).getManagersComponent();
        
        managersComponent.inject(this);
        
        m_NoteManager = m_Managers.getNoteManager();
    }
    
    private void setInitalState() {
        switch (m_SettingsManager.getSortType()) {
            case CREATION_TIME:
                sortTypeRadioGroup.check(R.id.settings_sort_type_creation_time_radio_button);
                break;
            case LAST_EDIT_TIME:
                sortTypeRadioGroup.check(R.id.settings_sort_type_edit_time_radio_button);
                break;
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
