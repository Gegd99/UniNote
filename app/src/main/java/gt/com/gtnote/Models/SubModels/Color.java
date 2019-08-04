package gt.com.gtnote.Models.SubModels;

public enum Color
{
    UNKNOWN(-1, 200, 200, 200),  // #
    RED(0, 248, 13, 27),  // #F80D1B
    ORANGE(1, 255, 160, 0),  // #FFA000
    YELLOW(2, 255, 227, 2),  // #FFE302
    GREEN(3, 0, 250, 34),  // #00FA22
    BLUE(4, 0, 169, 238),  // #00A9EE
    VIOLET(5, 159, 0, 255),  // #9F00FF
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
    
    /**
     * @return all colors except UNKNOWN
     */
    public static Color[] getAllNormal() {
        Color[] allColors = values();
        Color[] colors = new Color[allColors.length - 1];
        int i = 0;
        for (Color color : allColors) {
            if (color != UNKNOWN) {
                colors[i] = color;
                i++;
            }
        }
        return colors;
    }
}
