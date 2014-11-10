package GamePackage.FieldPackage;

import GamePackage.FieldPackage.Cells.*;

import java.util.Random;

/**
 * Created by PEfremov on 21.10.2014.
 * Игровое поле
 */
public class Field {
    private final static int DEFAULT_FIELD_SIZE = 10;
    private final static int DEFAULT_BOMB_COUNT = 10;
    private final static Random RAND = new Random();

//    private int bombCount;
    private int wight;
    private int height;
    private Cell[][] field;
    private int bombArray[][];


    public Field() {
        this(DEFAULT_FIELD_SIZE, DEFAULT_FIELD_SIZE, DEFAULT_BOMB_COUNT);
    }

    public Field(int wightSize, int heightSize, int bombC) {
        wight = wightSize;
        height = heightSize;
        field = new Cell[wight][height];
        bombArray = new int[bombC][2];
        generateEmptyField();
        if (setBombs()) {
            generateField();
        }
    }

    public Field(int wightSize, int heightSize, int[][] bombsArr, int[][] cellsArr) {
        wight = wightSize;
        height = heightSize;
        field = new Cell[wight][height];
        generateEmptyField();
        setBombs(bombsArr);
        generateField();
        for (int[] cell: cellsArr){
            field[cell[0]][cell[1]].show();
        }
    }

    public String getBombList() {
        String bombsList = "";
        for (int[] coords: bombArray){
            bombsList += coords[0] + ":" + coords[1] + ";";
        }

        return bombsList;
    }

    public String getVisibleList() {
        String visibleList = "";
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[0].length; j++) {
                if (field[i][j].isVisible()) {
                    visibleList += i + ":" + j + ";";
                }

            }
        }

        return visibleList;
    }

    public int getWight() {
        return wight;
    }

    public int getHeight() {
        return height;
    }

    public int getBombCount() {
        return bombArray.length;
    }

    public void show() {
        showTopIndex();
        showLine("-");
        for (int i = 0; i < height; i++) {
            System.out.print(i);
            for (int j = i; String.valueOf(j).length() < String.valueOf(wight).length(); j = j * 10 + 10) {
                System.out.print(" ");
            }
            System.out.print("|");
            showRow(i);
            System.out.print("|");
            System.out.println();
        }
        showLine("X");
        System.out.println();
    }

    private void showTopIndex(){
        String outString = "*";
        for (int i = 1; String.valueOf(i).length() < String.valueOf(wight).length(); i *= 10) {
            outString += " ";
        }
        outString += "|";
        for (int i = 0; i < wight; i++) {
            outString += " " + i + " ";
        }
        outString += "Y";
        System.out.println(outString);
    }



    private void showLine(String val) {
        String outString = val;
        for (int i = 1; String.valueOf(i).length() < String.valueOf(wight).length(); i *= 10) {
            outString += " ";
        }
        outString += "|";
        for (int i = 0; i < (wight * 2 + wight); i++) {
            outString += "-";
        }
        outString += "|";
        //outString += "Надо переписать вывод линии";
        System.out.println(outString);

    }

    private void showRow(int rowIndex) {
        String fieldRow = "";
        for (Cell a : field[rowIndex]) {
            fieldRow += a.getValue();
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
//        System.out.println("Устанавливаем бомбы...");
//        if (bombCount > wight * height) {
//            System.out.println("Невозможно разместить столько бомб на таком маленьком поле!!!");
//            System.out.println("Бомбы не были установлены!");
//            return false;
//        } else {
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
//            System.out.println("Бомбы установлены!");
            return true;
//        }
    }

    private void setBomb(int row, int column) {
        field[row][column].setBomb();
    }

    private void putInBombArray(int index, int x, int y){
        bombArray[index][0] = x;
        bombArray[index][1] = y;
    }

    public int countVisible(){
        int count = 0;
        for (Cell[] row: field){
            for (Cell cell:row){
                if (cell.isVisible()) {
                    count++;
                }
            }
        }
        return count;
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
                field[rowIndex][colIndex].setValue(" " + String.valueOf(roundBombsCount) + " ");
            }
        }
    }

    public boolean openCell(int rowIndex, int colIndex, boolean showError) {
        boolean retrn = true;

        System.out.println("Открываем ячейку [" + rowIndex + "][" + colIndex + "]");
        if (checkIndex(rowIndex, colIndex)) {
            if (!field[rowIndex][colIndex].isVisible()) {
                switch (field[rowIndex][colIndex].getCellType()) {
                    case Bomb:
                        openField();
                        System.out.println("Бомба в ячейке [" + rowIndex + "][" + colIndex + "] !!!");
                        retrn = false;
                        break;
                    case Index:
                        field[rowIndex][colIndex].show();
                        break;
                    case Empty:
                        showAround(rowIndex, colIndex);
                        break;
                }
            }
        } else if (showError) {
            System.out.println("Неверная ячейка [" + rowIndex + "][" + colIndex + "] !!!");
        }
        return retrn;
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

//    public boolean openRandomCell() {
//
//        int rowIndex = RAND.nextInt(height);
//        int colIndex = RAND.nextInt(wight);
//
//        while (field[rowIndex][colIndex].isVisible()) {
//            rowIndex = RAND.nextInt(height);
//            colIndex = RAND.nextInt(wight);
//        }
//
//        System.out.println("Открой мне ячейку [" + rowIndex + "][" + colIndex + "]");
//        return openCell(rowIndex, colIndex, false);
//    }

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
        int countInvisible = 0;
        for (Cell[] rowCell: field){
            for (Cell cell: rowCell){
                if (!cell.isVisible()) countInvisible++;
            }
        }
        return  countInvisible == bombArray.length;
    }

    public void openField(){
        for (Cell[] rows: field){
            for (Cell cell: rows){
                cell.show();
            }
        }
        show();
    }

}
