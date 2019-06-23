package gt.com.gtnote.ViewModels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import org.json.JSONException;

import gt.com.gtnote.Adapters.AndroidFileIO;
import gt.com.gtnote.EditNoteActivity;
import gt.com.gtnote.GeneralSettingsActivity;
import gt.com.gtnote.Models.FileIO;
import gt.com.gtnote.Models.Note;
import gt.com.gtnote.Models.NoteManager;

public class MainActivityViewModel extends ViewModel
{
    private MutableLiveData<NoteManager> mNoteManager;

    public MainActivityViewModel()
    {
        mNoteManager = new MutableLiveData<>();
    }

    public void initNotes(Context context) {
        try {
            mNoteManager.setValue(new NoteManager(new AndroidFileIO(context)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createNewNote(View view)
    {
        Context context = view.getContext();
        Intent intent = new Intent(context, EditNoteActivity.class);
        intent.putExtra("typeId", 0);
        context.startActivity(intent);
    }

    public void openExistingNote(Context context, Note note)
    {
        Intent intent = new Intent(context, EditNoteActivity.class);
        intent.putExtra("typeId", 1);
        //TODO: When opening an existing Note, this note should be passed to the EditNoteActivityViewModel
        //intent.putExtra("note", note);
        context.startActivity(intent);
    }

    public void openSettings(Context context)
    {
        Intent intent = new Intent(context, GeneralSettingsActivity.class);
        context.startActivity(intent);
    }

    public LiveData<NoteManager> getNoteManager()
    {
        return mNoteManager;
    }
}
