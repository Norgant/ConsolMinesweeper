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
    private static final String PREF_WIGHT          = "wight=";
    private static final String PREF_HEIGHT         = "height=";
    private static final String PREF_BOMBS          = "bombs=";
    private static final String PREF_VISIBLE_COUNT  = "visiblecount=";
    private static final String PREF_VISIBLE        = "visible=";
    private static final String PREF_BOMBS_COUNT    = "bombscount=";
    private static final String DEFAULT_READ_FILE_ERROR   = "Ошибка загрузки файла!!!";
    private static final String DEFAULT_READ_FILE_SUCCESS = "Файл успешно загружен. Продолжим игру...";
    private static final String COORS_SPLITTER        = ";";
    private static final String WIGHT_HEIGHT_SPLITTER = ":";
    private static final String GLOBAL_SPLITTER = " \\\\ ";
    private static final String RAVN = "=";
    private static final int MIN_ELEMENT_COUNT  = 4;
    private static final int MMAX_ELEMENT_COUNT = 6;

    public static Field loadFieldFromFile(){
        Field field = new Field();
        int wigth;
        int height;
        int bombcount;
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
            if (loadedElements.length != MIN_ELEMENT_COUNT || loadedElements.length != MMAX_ELEMENT_COUNT){
                throw new IOException();
            }

//            Обработаем ширину
            if (loadedElements[0].indexOf(PREF_WIGHT) == -1){
                throw new IOException();
            } else {
                int ravnIndex = loadedElements[0].indexOf(RAVN);
                if (ravnIndex == -1) {
                    throw new IOException();
                } else {
                    wigth = Integer.valueOf(loadedElements[0].substring(ravnIndex).trim());
                }
            }


            //Получем индексы ключевых слов, если их нет выводим ошибку
            int wightInd = s.indexOf(PREF_WIGHT);
            if (wightInd == -1) {
                throw new IOException();
            }

            int heightInd = s.indexOf(PREF_HEIGHT);
            if (heightInd == -1) throw new IOException();

            int bombCountInd = s.indexOf(PREF_BOMBS_COUNT);
            if (bombCountInd == -1) throw new IOException();

            int bombInd = s.indexOf(PREF_BOMBS);
            if (bombInd == -1) throw new IOException();

            //Файл может быть без видимых ячеек, поэтому проcто получим индекс видимых, но проверять не будем.
            int visibInd = s.indexOf(PREF_VISIBLE);
            int visibCountInd = s.indexOf(PREF_VISIBLE_COUNT);

            //Получим основные параметры: Ширина, Высота, Количетсво бомб
            wigth       = Integer.valueOf(s.substring(wightInd      + PREF_WIGHT.length(),       heightInd));
            height      = Integer.valueOf(s.substring(heightInd     + PREF_HEIGHT.length(),      bombCountInd));
            bombcount   = Integer.valueOf(s.substring(bombCountInd  + PREF_BOMBS_COUNT.length(), bombInd));

            //Обработаем массив с бомбами
            String bombChar;
            if (visibInd == -1) {
                bombChar = s.substring(bombInd + PREF_BOMBS.length());
            } else {
                bombChar = s.substring(bombInd + PREF_BOMBS.length(), visibCountInd);
            }
            bombs = stringToCoordArray(bombChar, bombcount);


            //Обработаем массив с шагами
            String visibChar;
            if (visibInd != -1) {
                visibleCount = Integer.valueOf(s.substring(visibCountInd  + PREF_VISIBLE_COUNT.length(), visibInd));
                visibChar    = s.substring(visibInd + PREF_VISIBLE.length());
                visibleCells = stringToCoordArray(visibChar, visibleCount);
            }

            field = new Field(wigth, height, bombs, visibleCells);

            System.out.println(DEFAULT_READ_FILE_SUCCESS);

//            //Посмотрим что получилось бомбами
//            for (int[] bo: bombs){
//                System.out.println("Бомба " + bo[0] + ":" + bo[1]);
//            }
//            System.out.println();
//
//            //Проверим что с видимыми ячейками
//            if (visibInd != -1) {
//                for (int[] cell: visibleCells){
//                    System.out.println("Ячейка " + cell[0] + ":" + cell[1]);
//                }
//            }
//
//
//            //Проверка заполнения
//            System.out.println("Width: "     + wigth);
//            System.out.println("Height: "    + height);
//            System.out.println("Bobmcount: " + bombcount);
//            System.out.println("CellCount: " + visibleCount);


        } catch (Exception e) {
            System.out.println(DEFAULT_READ_FILE_ERROR);
        }
        return field;
    }

    private static int[][] stringToCoordArray (String coord, int intArrayIndex) throws IOException {
        int[][] result;
        String[] strArray = coord.split(COORS_SPLITTER);

        if (strArray.length == 0 || strArray.length != intArrayIndex) {
            throw new IOException(DEFAULT_READ_FILE_ERROR);
        }
        result = new int[intArrayIndex][2];
        int resultIndex = 0;
        for (String coordStr: strArray){
            String[] coordArr = coordStr.split(WIGHT_HEIGHT_SPLITTER);
            if (coordArr.length != 2) {
                throw new IOException(DEFAULT_READ_FILE_ERROR);
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

            fileOut.write((PREF_WIGHT  + RAVN +    field.getWight()     + GLOBAL_SPLITTER).getBytes());
            fileOut.write((PREF_HEIGHT + RAVN +    field.getHeight()    + GLOBAL_SPLITTER).getBytes());
            fileOut.write((PREF_BOMBS_COUNT + RAVN + field.getBombCount() + GLOBAL_SPLITTER).getBytes());
            fileOut.write((PREF_BOMBS + RAVN + field.getBombList() + GLOBAL_SPLITTER).getBytes());

            int visibleCount = field.countVisible();
            if (visibleCount != 0) {
                fileOut.write((PREF_VISIBLE_COUNT + RAVN + field.countVisible()   + GLOBAL_SPLITTER).getBytes());
                fileOut.write((PREF_VISIBLE       + RAVN + field.getVisibleList() + GLOBAL_SPLITTER).getBytes());
            }

        } catch (IOException e) {
            System.out.println("Ошибка сохранения в файл!");
            e.printStackTrace();
            result = false;
        }
        if (result) {
            System.out.println("Файл успешно сохранен в "   + DEFAULT_SAVE_PATH);
        } else {
            System.out.println("Не удалось сохранить файл " + DEFAULT_SAVE_PATH);
        }

    }

}
