package gt.com.uninote.Models;

import java.util.List;

import gt.com.uninote.Interfaces.NoteContent;
import gt.com.uninote.Models.SubModels.Resource;

/**
 * Lazy-loads the content (only when queried by getter).<br>
 */
class LazyNoteContent implements NoteContent {
    
    private String text;
    private List<Resource> resources;
    private FilePointer filePointer;
    private boolean textLoaded;
    
    LazyNoteContent(FilePointer filePointer) {
        this.filePointer = filePointer;
        this.textLoaded = false;
    }
    
    /**
     * Loads content from file if not loaded yet, so only on the first time.<br>
     * Then returns the loaded content.
     * @return the loaded content
     */
    @Override
    public String getText() {
        if (!textLoaded) {
            loadText();
            textLoaded = true;
        }
        return text;
    }
    
    /**
     * be careful: when you use this, the FilePointer's content will be ignored
     * @param text
     */
    @Override
    public void setText(String text) {
        this.text = text;
        textLoaded = true;
    }
    
    @Override
    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
    
    @Override
    public List<Resource> getResources() {
        return resources;
    }
    
    private void loadText() {
        // https://stackoverflow.com/questions/37899856/html-fromhtml-is-deprecated-what-is-the-alternative/37899914
        text = filePointer.read();
    }
}
