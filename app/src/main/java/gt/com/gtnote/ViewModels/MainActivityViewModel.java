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

import static gt.com.gtnote.statics.Constants.EDIT_NOTE_TYPE_ID;
import static gt.com.gtnote.statics.Constants.MAIN_EDIT_INTENT_NOTE_ID_KEY;
import static gt.com.gtnote.statics.Constants.MAIN_EDIT_INTENT_TYPE_ID_KEY;
import static gt.com.gtnote.statics.Constants.NOTE_NOT_EXISTENT_ID;
import static gt.com.gtnote.statics.Constants.PREVIEW_NOTE_TYPE_ID;

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
        intent.putExtra(MAIN_EDIT_INTENT_TYPE_ID_KEY, EDIT_NOTE_TYPE_ID);
        intent.putExtra(MAIN_EDIT_INTENT_NOTE_ID_KEY, NOTE_NOT_EXISTENT_ID);  //TODO: should be passed to the EditNoteActivityViewModel
        context.startActivity(intent);
    }

    public void openExistingNote(Context context, Note note)
    {
        Intent intent = new Intent(context, EditNoteActivity.class);
        intent.putExtra("typeId", PREVIEW_NOTE_TYPE_ID);
        //TODO: When opening an existing Note, this note should be passed to the EditNoteActivityViewModel
        //intent.putExtra("note", note);
        intent.putExtra("noteId", note.getNoteMeta().getNoteId());  //TODO: should be passed to the EditNoteActivityViewModel
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
