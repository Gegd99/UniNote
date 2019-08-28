package gt.com.gtnote.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import gt.com.gtnote.Models.Note;
import gt.com.gtnote.Models.SettingsManager;
import gt.com.gtnote.Models.SubModels.Color;
import gt.com.gtnote.Models.SubModels.SortType;

public class SortAndFilter
{
    public static List<Note> sortAndFilterList(List<Note> notes, List<Color> colorToFilterBy, SortType sortType)
    {
        notes = filterList(notes, colorToFilterBy);
        notes = sortList(notes, sortType);

        return notes;
    }

    public static List<Note> filterList(List<Note> notes, List<Color> colorToFilterBy)
    {
        if (colorToFilterBy.isEmpty())
            return notes;

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
