package gt.com.gtnote.Interfaces;

import android.text.Spanned;

import java.util.List;

import gt.com.gtnote.Models.SubModels.Resource;

public interface NoteContent {
    Spanned getText();
    void setText(Spanned spanned);
    void setResources(List<Resource> resources);
    List<Resource> getResources();
}
