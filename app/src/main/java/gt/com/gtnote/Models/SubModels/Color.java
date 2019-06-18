package gt.com.gtnote.Models.SubModels;

public enum Color
{
    UNKNOWN(-1),
    RED(1),
    GREEN(2),
    BLUE(3),
    YELLOW(4),
    CYAN(5),
    ORANGE(6);
    
    public final int id;
    
    Color(int id) {
        this.id = id;
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
