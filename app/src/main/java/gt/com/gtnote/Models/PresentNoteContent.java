package gt.com.gtnote.Models;

import java.util.List;

import gt.com.gtnote.Interfaces.NoteContent;
import gt.com.gtnote.Models.SubModels.Resource;

/**
 * In contrast to LazyNoteContent, this class stores everything in RAM from the beginning on.<br>
 * That is useful when you want to create a note e.g. if you don't have any file yet to load it from.
 */
class PresentNoteContent implements NoteContent {
    
    private String text;
    private List<Resource> resources;
    
    PresentNoteContent(String text) {
        this.text = text;
    }
    
    @Override
    public String getText() {
        return text;
    }
    
    @Override
    public void setText(String text) {
        this.text = text;
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
