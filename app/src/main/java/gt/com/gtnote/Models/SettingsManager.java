package gt.com.gtnote.Models;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.util.LinkedList;

import javax.inject.Inject;
import javax.inject.Singleton;

import gt.com.gtnote.Interfaces.FileIO;
import gt.com.gtnote.Models.SubModels.Color;
import gt.com.gtnote.Models.SubModels.SortType;

@Singleton
public class SettingsManager
{
    private static final String TAG = "GTNOTE";

    private static final String META_FILE_NAME = "settings.json";

    private FileIO fileIO;

    private SortType sortType;

    @Inject
    public SettingsManager(FileIO fileIO)
    {
        this.fileIO = fileIO;
        try {
            loadSettings();
        } catch (JSONException e)
        {
            //TODO: If there are no settings they should be created/initialized.
            Log.e(TAG, String.format("An error occurred while loading the settings."));
            e.printStackTrace();
        }
    }


    private void loadSettings() throws JSONException
    {
        //TODO:Implement

        //load SortType

        //load other settings
    }

    public SortType getSortType() {
        return sortType;
    }

    private void setSortType(SortType sortType) {
        this.sortType = sortType;
    }
}
