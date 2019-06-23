package gt.com.gtnote.ViewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.view.View;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import gt.com.gtnote.EditNoteActivity;
import gt.com.gtnote.MainActivity;
import gt.com.gtnote.Models.FileIO;
import gt.com.gtnote.Models.NoteManager;

import static android.content.Context.MODE_PRIVATE;

public class MainActivityViewModel extends ViewModel
{
    private MutableLiveData<String> message = new MutableLiveData<>();

    public MainActivityViewModel()
    {
        message = new MutableLiveData<>();
        message.setValue("Don't smoke");
    }

    public void createNewNote(View view)
    {
        Context context = view.getContext();
        Intent intent = new Intent(context, EditNoteActivity.class);
        intent.putExtra("typeId", 0);
        context.startActivity(intent);
    }



    public LiveData<String> getMessage() {
        return message;
    }

}
