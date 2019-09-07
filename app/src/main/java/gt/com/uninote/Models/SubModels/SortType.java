package gt.com.uninote.Models.SubModels;

public enum SortType {
    CREATION_TIME(0),
    LAST_EDIT_TIME(1);
    
    public int id;
    
    SortType(int id) {
        this.id = id;
    }
    
    public static SortType fromId(int id) {
        for (SortType sortType : values()) {
            if (sortType.id == id) {
                return sortType;
            }
        }
        return CREATION_TIME;
    }
}
