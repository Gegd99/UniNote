package gt.com.gtnote;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

public class EditNoteActivity extends AppCompatActivity {

    private final int EDIT_NOTE_TYPE_ID = 0;
    private final int PREVIEW_NOTE_TYPE_ID = 1;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        //Get all View-Elements by their Id
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        int type = getIntent().getExtras().getInt("typeId");

        if (type == 0)
        {
            setTitle("Edit Note");
        }
        else
        {
            setTitle("Preview Note");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings2) {
            //Open CurrentNoteSettings
        }

        return super.onOptionsItemSelected(item);
    }

}
