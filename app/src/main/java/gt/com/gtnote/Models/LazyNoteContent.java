package gt.com.gtnote.Models;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import java.util.List;

import gt.com.gtnote.Interfaces.NoteContent;
import gt.com.gtnote.Models.SubModels.Resource;

/**
 * Lazy-loads the content (only when queried by getter).<br>
 */
class LazyNoteContent implements NoteContent {
    
    private Spanned spanned;
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
    public Spanned getText() {
        if (!textLoaded) {
            loadText();
            textLoaded = true;
        }
        return spanned;
    }
    
    /**
     * be careful: when you use this, the FilePointer's content will be ignored
     * @param spanned
     */
    @Override
    public void setText(Spanned spanned) {
        this.spanned = spanned;
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
        // TODO: use an ImageGetter in Html.fromHtml()
        // https://stackoverflow.com/questions/37899856/html-fromhtml-is-deprecated-what-is-the-alternative/37899914
        String source = filePointer.read();
        if (Build.VERSION.SDK_INT >= 24) {
            spanned = Html.fromHtml(source, Html.FROM_HTML_MODE_COMPACT);
        } else {
            spanned = Html.fromHtml(source);
        }
    }
}
