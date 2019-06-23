package gt.com.gtnote;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class EditNoteActivity extends AppCompatActivity {

    private final int EDIT_NOTE_TYPE_ID = 0;
    private final int PREVIEW_NOTE_TYPE_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        getIntent().getExtras().getString("typeId");
    }
}
