package gt.com.gtnote.Models.SubModels;

public enum Color
{
    UNKNOWN(-1, 200, 200, 200),  // #
    RED(0, 253,80,81),  // #F80D1B
    ORANGE(1, 255,169,89),  // #F80D1B
    YELLOW(2, 255,246,97),  // #FFE302
    GREEN(3, 97,239,148),  // #00FA22
    BLUE(4, 111,190,251),  // #00A9EE
    VIOLET(5, 144,99,233),  // #9F00FF
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
