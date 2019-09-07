package gt.com.uninote.Models;

import android.content.Context;
import android.content.ContextWrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.inject.Inject;

import gt.com.uninote.Interfaces.FileIO;

import static android.content.Context.MODE_PRIVATE;

public class AndroidFileIO implements FileIO
{
    private ContextWrapper mContextWrapper;

    @Inject
    public AndroidFileIO(Context context)
    {
        mContextWrapper = new ContextWrapper(context);
    }

    @Override
    public String read(String filename) {

        String source = null;

        FileInputStream fis = null;
        try {
            fis = mContextWrapper.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            source = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        return source;
    }

    @Override
    public void write(String filename, String source) {

        FileOutputStream fos = null;
        try {
            fos = mContextWrapper.openFileOutput(filename, MODE_PRIVATE);
            fos.write(source.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    @Override
    public boolean fileExists(String path) {
        return new File(mContextWrapper.getFilesDir(), path).exists();
    }

    @Override
    public void delete(String path) {
        mContextWrapper.deleteFile(path);
    }

    @Override
    public String[] list() {
        return mContextWrapper.getFilesDir().list();
    }
}
