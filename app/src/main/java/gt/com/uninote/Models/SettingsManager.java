package gt.com.uninote.Models;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import gt.com.uninote.Interfaces.FileIO;
import gt.com.uninote.Models.SubModels.Color;
import gt.com.uninote.Models.SubModels.SortType;

import static gt.com.uninote.statics.Constants.PREFERENCE_SORT_TYPE;

@Singleton
public class SettingsManager
{
    private static final String TAG = "GTNOTE";
    
    private SharedPreferences sharedPreferences;
    
    public SettingsManager(SharedPreferences sharedPreferences) {
        
        this.sharedPreferences = sharedPreferences;
    }

    public SortType getSortType() {
        int sortTypeId = sharedPreferences.getInt(PREFERENCE_SORT_TYPE, SortType.LAST_EDIT_TIME.id);
        return SortType.fromId(sortTypeId);
    }

    public void setSortType(SortType sortType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREFERENCE_SORT_TYPE, sortType.id);
        editor.apply();
    }

    public List<Color> getFilterColors() {
        return new ArrayList<>(0);
    }
}
