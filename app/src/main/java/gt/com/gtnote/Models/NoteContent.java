package gt.com.gtnote.Models;

import android.text.Spanned;

import java.util.List;

import gt.com.gtnote.Models.SubModels.Resource;

public interface NoteContent {
    public Spanned getSpanned();
    public void setSpanned(Spanned spanned);
    void setResources(List<Resource> resources);
    List<Resource> getResources();
}
