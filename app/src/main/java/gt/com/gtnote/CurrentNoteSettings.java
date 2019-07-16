package gt.com.gtnote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import gt.com.gtnote.Models.SubModels.Color;

import static gt.com.gtnote.statics.Constants.COLOR_PICK_INTENT_KEY;
import static gt.com.gtnote.statics.Constants.NUMBER_OF_COLORS;

public class CurrentNoteSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_note_settings);
        
        setTitle("Color");  //todo: title looks ugly → remove title from layout, replace with textview
    
        GridLayout colorGrid = findViewById(R.id.colorGrid);
    
        // fill the grid with one view for each color
        for (int i = 0; i < NUMBER_OF_COLORS; i++) {
    
            View view = makeColorElement(Color.fromId(i));
            colorGrid.addView(view, i);
        }
    }
    
    private View makeColorElement(final Color color) {
        
        Button button = new Button(this);
        
        // set color
        button.setBackgroundColor(android.graphics.Color.rgb(color.red, color.green, color.blue));
        
        // set size
        // todo: use layout from an xml file here instead of setting it programmatically
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200, 200);
        params.setMargins(8, 8, 8, 8);
        button.setLayoutParams(params);
    
        // attach onclick listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onColorSelected(color.id);
            }
        });
        
        return button;
    }
    
    private void onColorSelected(int colorId) {
        
        Intent returnIntent = new Intent();
        returnIntent.putExtra(COLOR_PICK_INTENT_KEY, colorId);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
