package GamePackage;

import GamePackage.FieldPackage.Field;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by PEfremov on 05.11.2014.
 * В ходе рефакторинга сохранение и загрузка вынесены в отдельный класс
 */
public class SaveLoad {
    private static final String DEFAULT_SAVE_PATH = "C:\\FileTest\\MineSavedGame.txt";
    private static final String PREF_WIGHT          = "wight";
    private static final String PREF_HEIGHT         = "height";
    private static final String PREF_BOMBS          = "bombs";
    private static final String PREF_VISIBLE_COUNT  = "vis_count";
    private static final String PREF_VISIBLE        = "visible";
    private static final String PREF_BOMBS_COUNT    = "bomb_count";
    private static final String DEFAULT_READ_FILE_ERROR = "Ошибка загрузки файла!!!";
    private static final String DEFAULT_LOAD_FILE_ERROR = "Ошибка сохранения в файл!!!";
    private static final String FILE_STRUCTURE_ERROR = "Нарушена структура файла!!!";
    private static final String FILE_VALUE_ERROR     = "Неверные значения в файле!!!";
    private static final String DEFAULT_READ_FILE_SUCCESS = "Файл успешно загружен. Продолжим игру...";
    private static final String COORDS_SPLITTER = ";";
    private static final String WIGHT_HEIGHT_SPLITTER = ":";
    private static final String GLOBAL_SPLITTER = "!";
    private static final String RAVN = "=";
    private static final int MIN_ELEMENT_COUNT = 4;
    private static final int MAX_ELEMENT_COUNT = 6;

    public static Field loadFieldFromFile(){
        Field field = null;
        int wigth;
        int height;
        int bombCount;
        int visibleCount;
        int[][] bombs;
        int[][] visibleCells = null;

        try (FileInputStream inpFile = new FileInputStream(DEFAULT_SAVE_PATH)) {
            int flag = inpFile.read();
            String s = "";
            while (flag != -1){
                s += (char) flag;
                flag = inpFile.read();
            }

//            Получем массив загружемых элементов и проверяем его размер
            String[] loadedElements = s.split(GLOBAL_SPLITTER);
            if (loadedElements.length != MIN_ELEMENT_COUNT && loadedElements.length != MAX_ELEMENT_COUNT){
                throw new IOException(FILE_STRUCTURE_ERROR);
            }

//            Обработаем ширину
            wigth = Integer.valueOf(getValueFromString(loadedElements, PREF_WIGHT));

//            Обработаем высоту
            height = Integer.valueOf(getValueFromString(loadedElements, PREF_HEIGHT));

//            Обработаем количество бомб
            bombCount = Integer.valueOf(getValueFromString(loadedElements, PREF_BOMBS_COUNT));

//            Обработаем массив с бомбами
            String bombStr = getValueFromString(loadedElements, PREF_BOMBS);
            bombs = stringToCoordArray(bombStr, bombCount);

//            Обработаем открытиые ячейки если они есть
            if (loadedElements.length == MAX_ELEMENT_COUNT){
                visibleCount = Integer.valueOf(getValueFromString(loadedElements, PREF_VISIBLE_COUNT));
                String visibStr =getValueFromString(loadedElements, PREF_VISIBLE);
                visibleCells = stringToCoordArray(visibStr, visibleCount);
            }

            field = new Field(wigth, height, bombs, visibleCells);

            System.out.println(DEFAULT_READ_FILE_SUCCESS);

        } catch (NumberFormatException e){
            System.out.println(FILE_VALUE_ERROR);
        } catch (Exception e) {
            System.out.println(DEFAULT_READ_FILE_ERROR);
            System.out.println(e.getMessage());
        }
        return field;
    }

    private static String getValueFromString(String[] strArr, String pref) throws IOException{
        String result = null;
        boolean valueFinded = false;
        for (String elementStr : strArr) {
            if (elementStr.contains(pref)) {
                int ravnIndex = elementStr.indexOf(RAVN);
                if (ravnIndex == -1) {
                    throw new IOException(FILE_STRUCTURE_ERROR);
                } else {
                    result = elementStr.substring(ravnIndex + RAVN.length()).trim();
                    valueFinded = true;
                    break;
                }
            }
        }
        if (valueFinded) {
            return result;
        } else {
            throw new IOException(FILE_STRUCTURE_ERROR);
        }
    }

    private static int[][] stringToCoordArray (String coord, int intArrayIndex) throws IOException {
        int[][] result;
        String[] strArray = coord.split(COORDS_SPLITTER);

        if (strArray.length == 0 || strArray.length != intArrayIndex) {
            throw new IOException(FILE_STRUCTURE_ERROR);
        }
        result = new int[intArrayIndex][2];
        int resultIndex = 0;
        for (String coordStr: strArray){
            String[] coordArr = coordStr.split(WIGHT_HEIGHT_SPLITTER);
            if (coordArr.length != 2) {
                throw new IOException(FILE_STRUCTURE_ERROR);
            }
            result[resultIndex][0] = Integer.valueOf(coordArr[0].trim());
            result[resultIndex][1] = Integer.valueOf(coordArr[1].trim());
            resultIndex++;
        }

        return result;
    }

    public static void saveGame(Field field){
        boolean result = true;
        try (FileOutputStream fileOut = new FileOutputStream(DEFAULT_SAVE_PATH)) {

            fileOut.write((PREF_WIGHT       + RAVN +    field.getWight()     + GLOBAL_SPLITTER).getBytes());
            fileOut.write((PREF_HEIGHT      + RAVN +    field.getHeight()    + GLOBAL_SPLITTER).getBytes());
            fileOut.write((PREF_BOMBS_COUNT + RAVN +    field.getBombCount() + GLOBAL_SPLITTER).getBytes());
            fileOut.write((PREF_BOMBS       + RAVN +    field.getBombList()).getBytes());

            int visibleCount = field.countVisible();
            if (visibleCount != 0) {
                fileOut.write(GLOBAL_SPLITTER.getBytes());
                fileOut.write((PREF_VISIBLE_COUNT + RAVN + field.countVisible()   + GLOBAL_SPLITTER).getBytes());
                fileOut.write((PREF_VISIBLE       + RAVN + field.getVisibleList()).getBytes());
            }

        } catch (IOException e) {
            System.out.println(DEFAULT_LOAD_FILE_ERROR);
            result = false;
        }
        if (result) {
            System.out.println("Файл успешно сохранен в "   + DEFAULT_SAVE_PATH);
        } else {
            System.out.println("Не удалось сохранить файл " + DEFAULT_SAVE_PATH);
        }

    }

}
