package GamePackage.FieldPackage.Cells;

/**
 * Created by PEfremov on 21.10.2014.
 * Класс ячейки игрового поля
 */
public class Cell {
    private final static String DEFAULT_INVISIBLE_VALUE = "*";
    private final static String DEFAULT_VALUE = " ";
    private final static String DEFAULT_BOMB_VALUE = "B";

    private String value;
    private boolean visible = false;
    private CellType cellType;

    public Cell(){
        setValue();
    }

    public boolean isVisible(){
        return visible;
    }

    public CellType getCellType(){
        return cellType;
    }

    public String getValue(){
        if (visible) {
            return value;
        }
        else return DEFAULT_INVISIBLE_VALUE;
    }

    private void setValue(){
        value = DEFAULT_VALUE;
        cellType = CellType.Empty;
    }

    public void setValue(String val){
        value = val;
        cellType = CellType.Index;
    }

    public void setBomb(){
        value = DEFAULT_BOMB_VALUE;
        cellType = CellType.Bomb;
    }

    public void show(){
        visible = true;
    }
}
