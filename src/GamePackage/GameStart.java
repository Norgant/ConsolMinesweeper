package GamePackage;
import GamePackage.FieldPackage.Field;

import java.io.*;

/**
 * Created by PEfremov on 21.10.2014.
 * Да начнется игра!
 */
public class GameStart {
    private static final String DEFAULT_SAVE_PATH = "C:\\FileTest\\MineSavedGame.txt";
    private static final String PREF_WIGHT          = "wight=";
    private static final String PREF_HEIGHT         = "height=";
    private static final String PREF_BOMBS          = "bombs=";
    private static final String PREF_VISIBLE_COUNT  = "visiblecount=";
    private static final String PREF_VISIBLE        = "visible=";
    private static final String PREF_BOMBS_COUNT    = "bombscount=";
    private static final String DEFAULT_READ_FILE_ERROR = "Ошибка загрузки файла!!!";
    private static final String COORD_SPLITER           = ";";
    private static final String WIGHT_HEIGHT_SPLITER    = ":";
    private static final BufferedReader BR = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {


        boolean gameover = false;
        System.out.println("Игра началась!");
        while (!gameover) {
            System.out.println();
            Field gameField = makeNewField();
            //GameField.show();

            boolean flag = true;
            int step = 0;

            while (flag) {
                gameField.show();
                System.out.println("Шаг " + step);
                int[] inputArr = inputIndex(gameField);
                flag = gameField.openCell(inputArr[0], inputArr[1], true); //gameField.openRandomCell();
                if (gameField.isComplete()) break;
                step++;
            }

            if (flag) {
                gameField.openField();
                System.out.println("Поздравляем!!! Вы выйграли игру!!!");
            } else {
                System.out.println("Вы проиграли!!! Попробуйте снова!!!");
            }
            System.out.println("Шагов выполненно: " + step);
            System.out.println("Спасибо за игру!!!");
            System.out.println();

            gameover = isGameOver();

        }
    }

    private static boolean isGameOver(){

        boolean isOver = false;
        boolean inputCorrect = false;
        while (!inputCorrect) {
            System.out.print("Введите \"Повтор\" для новой игры или \"Выход\" для выхода из игры: ");
            try {
                String answer = BR.readLine();
                switch (answer.trim().toLowerCase()) {
                    case "повтор":
                        System.out.println();
                        System.out.println("-------Начнем новую игру!!!-----------");
                        inputCorrect = true;
                        break;
                    case "выход":
                        System.out.println();
                        System.out.println("Пока-пока!!!");
                        inputCorrect = true;
                        isOver = true;
                        break;
                    default:
                        System.out.println("Некорректный ввод. Повторите...");
                        break;
                }
            } catch (IOException exc) {
                System.out.println("Некорректный ввод. Повторите:");
            }
        }
        return isOver;

    }

    private static Field makeNewField() {
        Field field = new Field();
        System.out.println("Выберите вариант поля:");
        System.out.println("1) Маленькое:\t10х10 10 мин;");
        System.out.println("2) Среднее:\t\t15х30 40 мин;");
        System.out.println("3) Большое:\t\t25х40 99 мин;");
        System.out.println("4) Свое поле;");
        System.out.println("5) Загрузить игру из файла;");
        System.out.println();
        String c;

        boolean inputCorret = false;
        while (!inputCorret){
            System.out.print("Введите номер варианта: ");
            try {
                c = BR.readLine();
                switch (c) {
                    case "1":
                        field = new Field(10, 10, 10);
                        inputCorret = true;
                        break;
                    case "2":
                        field = new Field(15, 30, 40);
                        inputCorret = true;
                        break;
                    case "3":
                        field = new Field(25, 40, 99);
                        inputCorret = true;
                        break;
                    case "4":
                        field = new Field();
                        System.out.println("В разработке, пока играйте в 10х10 )))");
                        inputCorret = true;
                        break;
                    case "5":
                        inputCorret = true;
                        field = loadFieldFromFile();

                        break;
                    default:
                        System.out.println("Некорректный ввод!!! Повторите попытку...");
                        break;


                }
            } catch (IOException exc){
                System.out.println("Ввыедите значение!!!");
            }

        }

        return field;

    }

    private static Field loadFieldFromFile(){
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

            //Получем индексы ключевых слов, если их нет выводим ошибку
            int wightInd = s.indexOf(PREF_WIGHT);
            if (wightInd == -1) throw new IOException();

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
                bombChar = s.substring(bombCountInd + PREF_BOMBS_COUNT.length());
            } else {
                bombChar = s.substring(bombInd + PREF_BOMBS.length(), visibInd);
            }
            bombs = stringToCoordArray(bombChar, bombcount);


            //Обработаем массив с шагами
            String visibChar;
            if (visibInd != -1) {
                visibleCount = Integer.valueOf(s.substring(visibCountInd  + PREF_VISIBLE_COUNT.length(), visibInd));
                visibChar    = s.substring(visibInd + PREF_VISIBLE.length());
                visibleCells = stringToCoordArray(visibChar, visibleCount);
            }

            field = new Field(wigth, height, bombCountInd, bombs, visibleCells);

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
        String[] strArray = coord.split(COORD_SPLITER);

        if (strArray.length == 0 || strArray.length > intArrayIndex) {
            throw new IOException(DEFAULT_READ_FILE_ERROR);
        }
        result = new int[strArray.length][2];
        int resultIndex = 0;
        for (String coordStr: strArray){
            String[] coordArr = coordStr.split(WIGHT_HEIGHT_SPLITER);
            if (coordArr.length == 0) {
                throw new IOException(DEFAULT_READ_FILE_ERROR);
            }
            result[resultIndex][0] = Integer.valueOf(coordArr[0].trim());
            result[resultIndex][0] = Integer.valueOf(coordArr[1].trim());
        }

        return result;
    }

    private static int[] inputIndex(Field field){
        int[] indexArr = new int[2];
        try {
            String c;
            System.out.print("Введите координату Х (либо введите save чтобы сохранить игру): ");
            c = BR.readLine();
            if (c.trim().equalsIgnoreCase("save")){
                saveGame(field);
                System.out.print("Введите координату Х: ");
                c = BR.readLine();
            }
            indexArr[0] = Integer.parseInt(c);

            System.out.print("Введите координату Y: ");
            c = BR.readLine();
            indexArr[1] = Integer.parseInt(c);
        } catch (IOException exc) {
            System.out.println("Некорректный ввод");
        }


        return indexArr;
    }

    private static void saveGame(Field field){
        boolean result = true;
        try (FileOutputStream fileOut = new FileOutputStream(DEFAULT_SAVE_PATH)) {

            fileOut.write((PREF_WIGHT   +     field.getWight()      ).getBytes());
            fileOut.write((PREF_HEIGHT  +     field.getHeight()     ).getBytes());
            fileOut.write((PREF_BOMBS_COUNT + field.getBombCount()  ).getBytes());
            fileOut.write((PREF_BOMBS   +     field.getBombList()   ).getBytes());

            int visibleCount = field.countVisible();
            if (visibleCount != 0) {
                fileOut.write((PREF_VISIBLE_COUNT + field.countVisible()).getBytes());
                fileOut.write((PREF_VISIBLE       + field.getVisibleList()).getBytes());
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

