package gt.com.uninote.helper;

import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ActivityUtils {
    
    private static final String TAG = "GTNOTE";
    
    /**
     * You should not call this before Activity.onCreate()
     */
    public static String readRawTextFile(int resourceId, Resources res) {
        try (InputStream in = res.openRawResource(resourceId)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                builder.append("\n");
                line = reader.readLine();
            }
            return builder.toString();
        } catch (IOException e) {
            Log.e(TAG, "readRawTextFile: error while reading resource of id="+resourceId, e);
            return "";
        }
    }
}
