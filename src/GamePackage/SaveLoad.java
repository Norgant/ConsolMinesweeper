package GamePackage;

import GamePackage.FieldPackage.*;

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
    private static final String PREF_VISIBLE        = "visible";
    private static final String PREF_FLAG           = "flag";
    private static final String DEFAULT_READ_FILE_ERROR = "Ошибка загрузки файла!!!";
    private static final String DEFAULT_LOAD_FILE_ERROR = "Ошибка сохранения в файл!!!";
    private static final String FILE_STRUCTURE_ERROR = "Нарушена структура файла!!!";
    private static final String FILE_VALUE_ERROR     = "Неверные значения в файле!!!";
    private static final String DEFAULT_READ_FILE_SUCCESS = "Файл успешно загружен. Продолжим игру...";
    public static final String COORDS_SPLITTER = ";";
    public static final String WIGHT_HEIGHT_SPLITTER = ":";
    private static final String GLOBAL_SPLITTER = "!";
    private static final String RAVN = "=";
    private static final int MIN_ELEMENT_COUNT = 3;
    private static final int MAX_ELEMENT_COUNT = 5;

    public static Field loadFieldFromFile(){
        Field field = null;
        int wigth;
        int height;
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
            wigth = Integer.parseInt(getValueFromString(loadedElements, PREF_WIGHT));

//            Обработаем высоту
            height = Integer.parseInt(getValueFromString(loadedElements, PREF_HEIGHT));

//            Обработаем массив с бомбами
            String bombStr = getValueFromString(loadedElements, PREF_BOMBS);
            bombs = stringToCoordArray(bombStr);

//            Обработаем открытиые ячейки если они есть
            if (loadedElements.length == MAX_ELEMENT_COUNT){
                String visibleStr =getValueFromString(loadedElements, PREF_VISIBLE);
                visibleCells = stringToCoordArray(visibleStr);
            }


//            Обработаем флаги если они есть
            if (loadedElements.length > MIN_ELEMENT_COUNT){
                String visibleStr =getValueFromString(loadedElements, PREF_VISIBLE);
                if (!visibleStr.equals("")) {
                    visibleCells = stringToCoordArray(visibleStr);
                }
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

    private static int[][] stringToCoordArray (String coord) throws IOException {
        int[][] result;
        String[] strArray = coord.split(COORDS_SPLITTER);

        if (strArray.length == 0) {
            throw new IOException(FILE_STRUCTURE_ERROR);
        }
        result = new int[strArray.length][2];
        int resultIndex = 0;
        for (String coordStr: strArray){
            String[] coordArr = coordStr.split(WIGHT_HEIGHT_SPLITTER);
            if (coordArr.length != 2) {
                throw new IOException(FILE_STRUCTURE_ERROR);
            }
            result[resultIndex][0] = Integer.parseInt(coordArr[0].trim());
            result[resultIndex][1] = Integer.parseInt(coordArr[1].trim());
            resultIndex++;
        }

        return result;
    }

    public static void saveGame(Field field){
        boolean result = true;
        try (FileOutputStream fileOut = new FileOutputStream(DEFAULT_SAVE_PATH)) {

            fileOut.write((PREF_WIGHT  + RAVN + field.getWight()  + GLOBAL_SPLITTER).getBytes());
            fileOut.write((PREF_HEIGHT + RAVN + field.getHeight() + GLOBAL_SPLITTER).getBytes());
            fileOut.write((PREF_BOMBS  + RAVN + field.getBombList()).getBytes());

            String visibleList = field.getVisibleList();
            if (!visibleList.equals("")) {
                fileOut.write(GLOBAL_SPLITTER.getBytes());
                fileOut.write((PREF_VISIBLE + RAVN + visibleList).getBytes());
            }


            String flagList = field.getFlagList();
            if (!flagList.equals("")) {
                fileOut.write(GLOBAL_SPLITTER.getBytes());
                fileOut.write((PREF_FLAG + RAVN + visibleList).getBytes());
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
