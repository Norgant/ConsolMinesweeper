package GamePackage.FieldPackage;

/**
 * Created by PEfremov on 24.11.2014.
 * Границы поля выделенны в отдельный класс
 */
public enum FieldBorder {
    HorizontalBorder("-"),
    VerticalBorder("|"),
    HorizontalAxisName("Y"),
    VerticalAxisName("X"),
    CornerSymbol("*");

    String value;

    FieldBorder(String value) {
        this.value = value;
    }
    public String toString(){
      return value;
    }
}
