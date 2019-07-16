package gt.com.gtnote.Models.SubModels;

public enum Color
{
    UNKNOWN(-1, 128, 128, 128),
    RED(0, 255, 0, 0),
    ORANGE(1, 255, 128, 0),
    YELLOW(2, 255, 255, 0),
    GREEN(3, 0, 255, 0),
    CYAN(4, 0, 255, 255),
    BLUE(5, 0, 0, 255),
    VIOLET(6, 128, 0, 255),
    PINK(7, 255, 0, 255),
    BLACK(8, 0, 0, 0),
    WHITE(9, 255, 255, 255),
    ;
    
    public final int id;
    public final int red;
    public final int green;
    public final int blue;
    
    Color(int id, int red, int green, int blue) {
        this.id = id;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    
    public static Color fromId(int id) {
        for (Color color: values()) {
            if (color.id == id) {
                return color;
            }
        }
        return UNKNOWN;
    }
}
