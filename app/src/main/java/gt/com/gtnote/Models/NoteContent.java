package gt.com.gtnote.Models;


import android.text.Html;

import java.util.List;

import gt.com.gtnote.Models.SubModels.Resource;

public class NoteContent
{
    private Html text;
    private List<Resource> resources;

    public  NoteContent()
    {

    }

    public Html getText() {
        return text;
    }

    public void setText(Html text) {
        this.text = text;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
}
