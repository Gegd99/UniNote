package gt.com.gtnote.Models;

import java.sql.Timestamp;

import gt.com.gtnote.Models.SubModels.Color;

public class NoteMeta
{
    private String title;
    private Color color;
    private Timestamp creationTime;
    private Timestamp lastEditTime;
    //private int noteType
    private int contentSize;

    public NoteMeta()
    {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {
        this.creationTime = creationTime;
    }

    public Timestamp getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(Timestamp lastEditTime) {
        this.lastEditTime = lastEditTime;
    }

    public int getContentSize() {
        return contentSize;
    }

    public void setContentSize(int contentSize) {
        this.contentSize = contentSize;
    }
}
