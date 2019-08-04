package gt.com.gtnote.Adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import gt.com.gtnote.Models.SubModels.Color;
import gt.com.gtnote.R;

public class ColorSpinnerAdapter extends ArrayAdapter<Color> {
    
    private final Color[] colors;
    
    public ColorSpinnerAdapter(Context context, Color[] colors) {
        super(context, 0, colors);
        this.colors = colors;
    }
    
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }
    
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }
    
    private View initView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.color_spinner_row, parent, false);
        }
        
        Color color = colors[position];
        int androidColor = android.graphics.Color.rgb(color.red, color.green, color.blue);
        convertView.findViewById(R.id.colorSpinnerBubbleLayoutContainer).getBackground().setColorFilter(androidColor, PorterDuff.Mode.ADD);
        
        return convertView;
    }
}
