package gt.com.gtnote.Models;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

    private SortType m_SortType;

    private List<Color> m_FilterColors;

    @Inject
    public SettingsManager(FileIO fileIO)
    {
        m_FilterColors = new ArrayList<>();

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
        m_SortType = SortType.LAST_EDIT_TIME;
    }

    public SortType getSortType() {
        return m_SortType;
    }

    private void setSortType(SortType sortType) {
        m_SortType = sortType;
    }

    public List<Color> getFilterColors() {
        return m_FilterColors;
    }

    public void addFilterColors(Color color) {
        m_FilterColors.add(color);
    }
    public void removeFilterColors(Color color) {
        m_FilterColors.remove(color);
    }
}
