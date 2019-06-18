package gt.com.gtnote.Models;


import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import java.util.List;

import gt.com.gtnote.Models.SubModels.Resource;

public class NoteContent
{
    private Spanned spanned;
    private List<Resource> resources;
    private FilePointer filePointer;
    private boolean textLoaded = true;
    
    NoteContent(FilePointer filePointer) {
        this.filePointer = filePointer;
    }
    
    public Spanned getSpanned() {
        if (!textLoaded) {
            loadText();
            textLoaded = true;
        }
        return spanned;
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

    public void setSpanned(Spanned spanned) {
        this.spanned = spanned;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
}
