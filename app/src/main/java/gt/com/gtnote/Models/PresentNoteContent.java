package gt.com.gtnote.Models;

import android.text.Spanned;

import java.util.List;

import gt.com.gtnote.Interfaces.NoteContent;
import gt.com.gtnote.Models.SubModels.Resource;

/**
 * In contrast to LazyNoteContent, this class stores everything in RAM from the beginning on.<br>
 * That is useful when you want to create a note e.g. if you don't have any file yet to load it from.
 */
class PresentNoteContent implements NoteContent {
    
    private Spanned spanned;
    private List<Resource> resources;
    
    PresentNoteContent(Spanned spanned) {
        this.spanned = spanned;
    }
    
    @Override
    public Spanned getSpanned() {
        return spanned;
    }
    
    @Override
    public void setSpanned(Spanned spanned) {
        this.spanned = spanned;
    }
    
    @Override
    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
    
    @Override
    public List<Resource> getResources() {
        return resources;
    }
}
