package gt.com.uninote.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gt.com.uninote.Models.Note;
import gt.com.uninote.Models.SubModels.Color;
import gt.com.uninote.Models.SubModels.SortType;

public class SortAndFilter
{
    public static List<Note> sortAndFilterList(List<Note> notes, List<Color> colorToFilterBy, SortType sortType, String textToSearch)
    {
        notes = filterList(notes, colorToFilterBy);
        notes = searchList(notes, textToSearch);
        notes = sortList(notes, sortType);

        return notes;
    }

    public static List<Note> filterList(List<Note> notes, List<Color> colorToFilterBy)
    {
        List<Note> filteredNotes = new ArrayList<>();

        for (Note note : notes)
        {
            if (colorToFilterBy.contains(note.getNoteMeta().getColor()))
            {
                filteredNotes.add(note);
            }
        }
        return filteredNotes;
    }

    private static List<Note> searchList(List<Note> notes, String textToSearch) {
        if(textToSearch.isEmpty())
            return notes;

        List<Note> filteredNotes = new ArrayList<>();

        textToSearch = textToSearch.toLowerCase();
        for (Note note : notes)
        {
            if (note.getNoteMeta().getTitle().toLowerCase().contains(textToSearch) || note.getNoteContent().getText().toLowerCase().contains(textToSearch))
            {
                filteredNotes.add(note);
            }
        }
        return filteredNotes;
    }

    private static List<Note> sortList(List<Note> notes, SortType sortType)
    {
        switch (sortType)
        {
            case CREATION_TIME:
                Collections.sort(notes, (note1, note2) -> Long.compare(note1.getNoteMeta().getCreationTime(), note2.getNoteMeta().getCreationTime()));
                Collections.reverse(notes);
                break;
            case LAST_EDIT_TIME:
                Collections.sort(notes, (note1, note2) -> Long.compare(note1.getNoteMeta().getLastEditTime(), note2.getNoteMeta().getLastEditTime()));
                Collections.reverse(notes);
                break;
            default:
                break;
        }

        return notes;
    }
}
