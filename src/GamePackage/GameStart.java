package GamePackage;
import GamePackage.FieldPackage.Field;

import java.io.*;

/**
 * Created by PEfremov on 21.10.2014.
 * Да начнется игра!!!
 */
public class GameStart {
    private static final BufferedReader BR   = new BufferedReader(new InputStreamReader(System.in));
    private static final String WIN_MESSAGE   = "Поздравляем!!! Вы выйграли игру!!!";
    private static final String LOSE_MESSAGE  = "Вы проиграли!!! Попробуйте снова!!!";
    private static final String START_MESSAGE = "Игра началась!";


    public static void main(String[] args) throws IOException {
        Menu menuElement;
        boolean gameOver = false;
        System.out.println(START_MESSAGE);
        System.out.println();
        while (!gameOver) {
            Field gameField = null;
            do {
                menuElement = Menu.selectFromMenu(BR);
                if (menuElement == Menu.Exit) {
                    gameOver = true;
                    break;
                }
                gameField = makeNewField(menuElement);
                System.out.println();
            } while (gameField == null);

            if (!gameOver) {
                boolean flag = true;
                int step = 0;
                while (flag) {
                    gameField.show();
                    System.out.println("Шаг " + step);
                    int[] inputArr = inputIndex(gameField);
                    flag = gameField.openCell(inputArr[0], inputArr[1], true);
                    if (gameField.isComplete()) break;
                    step++;
                }

                if (flag) {
                    gameField.openField();
                    System.out.println(WIN_MESSAGE);
                } else {
                    System.out.println(LOSE_MESSAGE);
                }
                System.out.println("Шагов выполненно: " + step);
                System.out.println("Спасибо за игру!!!");
                System.out.println();
                gameOver = isGameOver();
            }
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

    private static Field makeNewField(Menu menuElement) {
        Field field;
        switch (menuElement) {
            case SmallField:
                field = new Field(10, 10, 10);
                break;
            case MediumField:
                field = new Field(15, 30, 40);
                break;
            case BigField:
                field = new Field(25, 40, 99);
                break;
            case MyField:
                field = new Field();
                System.out.println("В разработке, пока играйте в 10х10 )))");
                break;
            case LoadFile:
                field = SaveLoad.loadFieldFromFile();
                break;
            default:
                System.out.println("Такого варианта нет!!! пока играйте в 10х10");
                field = new Field(10, 10, 10);
                break;
        }

        return field;
    }

    private static int[] inputIndex(Field field){ //TODO Refactor input CODE
        int[] indexArr = new int[2];
        try {
            String c;
            System.out.print("Введите координату Х (либо введите save чтобы сохранить игру): ");
            c = BR.readLine();
            if (c.trim().equalsIgnoreCase("save")){
                SaveLoad.saveGame(field);
                System.out.print("Введите координату Х: ");
                c = BR.readLine();
            }
            indexArr[0] = Integer.valueOf(c);

            System.out.print("Введите координату Y: ");
            c = BR.readLine();
            indexArr[1] = Integer.valueOf(c);
        } catch (IOException exc) {
            System.out.println("Некорректный ввод");
        }


        return indexArr;
    }
}

