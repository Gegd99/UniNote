package gt.com.gtnote.Interfaces;

import java.util.List;

import gt.com.gtnote.Models.SubModels.Resource;

public interface NoteContent {
    String getText();
    void setText(String text);
    void setResources(List<Resource> resources);
    List<Resource> getResources();
}
