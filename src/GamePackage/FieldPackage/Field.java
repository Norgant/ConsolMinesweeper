package GamePackage.FieldPackage;

import GamePackage.FieldPackage.Cells.*;
import GamePackage.SaveLoad;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by PEfremov on 21.10.2014.
 * Игровое поле
 */
public class Field implements Serializable{
    private final static int DEFAULT_FIELD_HEIGHT = 10;
    private final static int DEFAULT_FIELD_WIGHT = 10;
    private final static int DEFAULT_BOMB_COUNT = 10;
    private final static Random RAND = new Random();

//    private int bombCount;
    private int wight;
    private int height;
    private boolean win = false;
    private boolean complite  = false;
    private Cell[][] field;
    private int bombArray[][];


    public Field() {
        this(DEFAULT_FIELD_HEIGHT, DEFAULT_FIELD_WIGHT, DEFAULT_BOMB_COUNT);
    }

    public Field(int heightSize, int wightSize, int bombC) {
        wight = wightSize;
        height = heightSize;
        field = new Cell[height][wight];
        bombArray = new int[bombC][2];
        generateEmptyField();
        if (setBombs()) {
            generateField();
        }
    }

    public Field(int heightSize, int wightSize, int[][] bombsArr, int[][] cellsArr) {
        this(heightSize, wightSize, bombsArr);
        showCellsArray(cellsArr);
    }

    public Field(int heightSize, int wightSize, int[][] bombsArr) {
        wight = wightSize;
        height = heightSize;
        field = new Cell[height][wight];
        generateEmptyField();
        setBombs(bombsArr);
        generateField();
    }

    private void showCellsArray(int[][] cellsArr){
        for (int[] cell: cellsArr){
            field[cell[0]][cell[1]].show();
        }
    }

    public String getBombList() {
        String bombsList = "";
        for (int[] coords: bombArray){
            bombsList += coords[0] + SaveLoad.WIGHT_HEIGHT_SPLITTER + coords[1] + SaveLoad.COORDS_SPLITTER;
        }

        return bombsList;
    }

    public String getVisibleList() {
        String visibleList = "";
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                if (field[i][j].isVisible()) {
                    visibleList += i +  SaveLoad.WIGHT_HEIGHT_SPLITTER + j + SaveLoad.COORDS_SPLITTER;
                }
            }
        }
        return visibleList;
    }

    public String getFlagList() {
        String flagList = "";
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                if (field[i][j].isFlag()) {
                    flagList += i +  SaveLoad.WIGHT_HEIGHT_SPLITTER + j + SaveLoad.COORDS_SPLITTER;
                }
            }
        }
        return flagList;
    }

    public int getWight() {
        return wight;
    }

    public int getHeight() {
        return height;
    }

    public void show() {
        showTopIndex();
        showLine(FieldBorder.HorizontalBorder);

        for (int i = 0; i < height; i++) {
            System.out.print(getPrefValue(i,true));
            System.out.print(FieldBorder.VerticalBorder);
            showRow(i);
            System.out.print(getPrefValue(FieldBorder.VerticalBorder, false));
            System.out.println();
        }
        showLine(FieldBorder.VerticalAxisName);
        System.out.println();
    }

    private <T> String getPrefValue(T value, boolean append){
        return getPrefValue(value, append, " ");
    }

    private <T> String getPrefValue(T value, boolean append, T pref){
        int wigthLenght = String.valueOf(wight).length() + 1;
        StringBuilder prefIndex = new StringBuilder(value.toString());
        while (prefIndex.length() < wigthLenght) {
            if (append) {
                prefIndex.append(pref);
            } else {
                prefIndex.insert(0, pref);
            }
        }
        return prefIndex.toString();
    }

    private void showTopIndex(){
        String outString = "";
        System.out.print(getPrefValue(FieldBorder.CornerSymbol, true));
        outString += FieldBorder.VerticalBorder;
        for (int i = 0; i < wight; i++) {
            outString += getPrefValue(i, false);
        }
        outString += getPrefValue(FieldBorder.HorizontalAxisName, false);
        System.out.println(outString);
    }



    private <T> void showLine(T val) {
        String outString = "";
        System.out.print(getPrefValue(val, true));

        outString += FieldBorder.VerticalBorder;
        for (int i = 0; i < (wight); i++) {
            outString += getPrefValue(FieldBorder.HorizontalBorder, false, FieldBorder.HorizontalBorder);
        }
        outString += getPrefValue(FieldBorder.VerticalBorder, false, FieldBorder.HorizontalBorder);
        System.out.println(outString);

    }

    private void showRow(int rowIndex) {
        String fieldRow = "";
        for (Cell a : field[rowIndex]) {
            fieldRow += getPrefValue(a.getValue(), false);
        }
        System.out.print(fieldRow);
    }

    private void setBombs(int[][] bombArr){
        bombArray = bombArr;
        for (int[] bomb: bombArray){
            setBomb(bomb[0], bomb[1]);
        }
    }
    private boolean setBombs() {
            int bombsPlanted = 0;
            while (bombsPlanted < bombArray.length) {
                int row = RAND.nextInt(height);
                int column = RAND.nextInt(wight);
                if (!(field[row][column].getCellType() == CellType.Bomb)) {
                    setBomb(row, column);
                    putInBombArray(bombsPlanted, row, column);
                    bombsPlanted++;
                }
            }
            return true;
    }

    private void setBomb(int row, int column) {
        field[row][column].setBomb();
    }

    private void putInBombArray(int index, int x, int y){
        bombArray[index][0] = x;
        bombArray[index][1] = y;
    }

    private void generateField() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < wight; j++) {
                generateCellValue(i, j);
            }
        }
    }

    private void generateEmptyField() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < wight; j++) {
                field[i][j] = new Cell();
            }
        }
    }

    private void generateCellValue(int rowIndex, int colIndex) {
        if (!(field[rowIndex][colIndex].getCellType() == CellType.Bomb)) {
            int roundBombsCount = 0;
            for (int i = rowIndex - 1; i <= rowIndex + 1; i++) {
                if (i >= 0 && i < height) {
                    for (int j = colIndex - 1; j <= colIndex + 1; j++) {
                        if (j >= 0 && j < wight) {
                            if (field[i][j].getCellType() == CellType.Bomb) roundBombsCount++;
                        }
                    }
                }
            }
            if (roundBombsCount > 0) {
                field[rowIndex][colIndex].setValue(String.valueOf(roundBombsCount));
            }
        }
    }

    public boolean isWin(){
        return win;
    }

    public void openCell(int rowIndex, int colIndex, boolean showError) {

        System.out.println("Открываем ячейку [" + rowIndex + "][" + colIndex + "]");
        if (checkIndex(rowIndex, colIndex)) {
            if (!field[rowIndex][colIndex].isVisible() && !field[rowIndex][colIndex].isFlag()) {
                switch (field[rowIndex][colIndex].getCellType()) {
                    case Bomb:
                        openField();
                        System.out.println("Бомба в ячейке [" + rowIndex + "][" + colIndex + "] !!!");
                        win = false;
                        break;
                    case Index:
                        field[rowIndex][colIndex].show();
                        break;
                    case Empty:
                        showAround(rowIndex, colIndex);
                        break;
                }
                if (!complite){
                    checkComplete();
                }

            }
        } else if (showError) {
            System.out.println("Неверная ячейка [" + rowIndex + "][" + colIndex + "] !!!");
        }
    }

    private boolean checkIndex(int rowIndex, int colIndex) {
        return rowIndex >= 0 && rowIndex < height && colIndex >= 0 && colIndex < wight;
    }

    private  void showAround(int rowIndex,int colIndex){
        int[][] showArray = new int[countEmpty()][2];
        showArray[0][0] = rowIndex;
        showArray[0][1] = colIndex;
        Integer showArrayIndex = 1;
        for (int i = 0; i < showArrayIndex; i++) {
            showArrayIndex = addToShow(showArray[i][0], showArray[i][1], showArrayIndex, showArray);
        }
    }

    private int addToShow(int rowIndex, int colIndex, Integer index, int[][] array){
        field[rowIndex][colIndex].show();
        for (int i = rowIndex - 1; i <= rowIndex + 1; i++) {
            for (int j = colIndex - 1; j <= colIndex + 1; j++) {
                if (checkIndex(i, j)) {
                    if (field[i][j].getCellType() == CellType.Index)
                        field[i][j].show();
                    else if (field[i][j].getCellType() == CellType.Empty && !field[i][j].isVisible()) {
                        field[i][j].show();
                        array[index][0] = i;
                        array[index][1] = j;
                        index++;
                    }
                }
            }
        }
        return index;
    }
    public void setFlag(int row, int column){
        field[row][column].setFlag();
    }

    public void setFlag(int[][] flags){
        for (int[] flag: flags)
        field[flag[0]][flag[1]].setFlag();
    }

    private int countEmpty(){
        int count = 0;
        for (Cell[] rows: field){
            for (Cell cell: rows){
                if (cell.getCellType() == CellType.Empty)
                    count++;
            }
        }
        return count;
    }

    public boolean isComplete(){
        return  complite;
    }

    private void checkComplete(){
        int countInvisible = 0;
        for (Cell[] rowCell: field){
            for (Cell cell: rowCell){
                if (!cell.isVisible()) countInvisible++;
            }
        }
        if (countInvisible == bombArray.length) {
            complite = true;
            win = true;
        } else if (countInvisible == 0){
            complite = true;
            win = false;
        }
    }

    public void openField(){
        for (Cell[] rows: field){
            for (Cell cell: rows){
                cell.show();
            }
        }
        show();
        checkComplete();
    }

}
