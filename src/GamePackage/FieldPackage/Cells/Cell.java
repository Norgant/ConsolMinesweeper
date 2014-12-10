package GamePackage.FieldPackage.Cells;

import java.io.Serializable;

/**
 * Created by PEfremov on 21.10.2014.
 * Класс ячейки игрового поля
 */
public class Cell implements Serializable {
    private final static String DEFAULT_INVISIBLE_VALUE = "*";//TODO Move all default symbols to enum CellType
    private final static String DEFAULT_VALUE = " ";
    private final static String DEFAULT_BOMB_VALUE = "B";
    private final static String DEFAULT_FLAG_VALUE = "F";
    private final static String FLAG_ERROR_MESSAGE = "На открытой ячейке нельзя установить флаг!";

    private String value;
    private boolean visible = false;
    private CellType cellType;
    private boolean flag = false;

    public void setFlag() {
        if (visible) {
            System.out.println(FLAG_ERROR_MESSAGE);
        } else {
            flag = !flag;
        }
    }

    public Cell(){
        setValue();
    }

    public boolean isVisible(){
        return visible;
    }

    public boolean isFlag() {
        return flag;
    }

    public CellType getCellType(){
        return cellType;
    }

    public String getValue(){
        if (flag && !visible) {
            return DEFAULT_FLAG_VALUE;
        } else {
            if (visible) {
                return value;
            }  else {
                return DEFAULT_INVISIBLE_VALUE;
            }
        }
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
