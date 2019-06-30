package gt.com.gtnote;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class CurrentNoteSettings extends AppCompatActivity {
    
    private float hue;
    private SeekBar colorSeekBar;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_note_settings);
    
        colorSeekBar = findViewById(R.id.noteColorSeekBar);
        confirmButton = findViewById(R.id.noteSettingsConfirmButton);
        
        updateHue(0);
        
        colorSeekBar.setMax(360);
        colorSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateHue(hue = 360 * i / (float) colorSeekBar.getMax());
            }
    
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
        
            }
    
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
        
            }
        });
        
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("colorHue", hue);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });
    }
    
    private void updateHue(float hue) {
        this.hue = hue;
        float[] hsv = new float[]{
                hue,
                1,
                1
        };
        confirmButton.getBackground().setColorFilter(Color.HSVToColor(hsv), PorterDuff.Mode.MULTIPLY);
    }
}
