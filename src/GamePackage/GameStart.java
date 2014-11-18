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
    private static final String END_MESSAGE   = "Спасибо за игру!!!";
    private static final String INPUT_ERROR_MESSAGE   = "Некорректный ввод. Повторите...";
    private static final String SAVE_COMMAND    = "save";
    private static final String EXIT_COMMAND    = "exit";
    private static final String REPEAT_COMMAND  = "repeat";
    private static final String MENU_COMMAND    = "menu";



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
                while (!gameField.isComplete()) {
                    gameField.show();
                    String userCommand  = getUserCommand();
                    executeUserCommand(userCommand, gameField);
                }

                if (gameField.isWin()) {
                    gameField.openField();
                    System.out.println(WIN_MESSAGE);
                } else {
                    System.out.println(LOSE_MESSAGE);
                }
                System.out.println(END_MESSAGE);
                System.out.println();
                gameOver = isGameOver();
            }
        }
    }

    private static String getUserCommand(){
        String command = null;
        while (command == null) {
            try {
                System.out.println("Чтобы открыть ячейку введите координаты: Х:Y; \n" +
                                   "Чтобы сохранить игру введите: " + SAVE_COMMAND + "; \n" +
                                   "Для возврата в меню введите:  " + MENU_COMMAND);

                command = BR.readLine();
                command = command.trim().toLowerCase();
                if (!command.equals(SAVE_COMMAND) && !command.equals(MENU_COMMAND) &&
                        !command.contains(":")) {
                    command = null;
                    throw new IOException();
                }
            } catch (IOException exc) {
                System.out.println(INPUT_ERROR_MESSAGE);
            }
        }
        return command;

    }

    private static void executeUserCommand(String command, Field field){
        switch (command){
            case SAVE_COMMAND:
                SaveLoad.saveGame(field);
                break;
            case EXIT_COMMAND:
                break;
            default:
                String[] coordArr = command.split(":");
                try {
                    field.openCell(Integer.valueOf(coordArr[0]), Integer.valueOf(coordArr[1]), true);
                } catch (Exception e) {
                    System.out.println(INPUT_ERROR_MESSAGE);
                }
                break;
        }
    }

    private static boolean isGameOver(){

        boolean isOver = false;
        boolean inputCorrect = false;
        while (!inputCorrect) {
            System.out.print("Введите \"" + REPEAT_COMMAND + "\" для новой игры или \"" + EXIT_COMMAND
                                                                                        + "\" для выхода из игры: ");
            try {
                String answer = BR.readLine();
                switch (answer.trim().toLowerCase()) {
                    case REPEAT_COMMAND:
                        System.out.println();
                        System.out.println("-------Начнем новую игру!!!-----------");
                        inputCorrect = true;
                        break;
                    case EXIT_COMMAND:
                        System.out.println();
                        System.out.println("Пока-пока!!!");
                        inputCorrect = true;
                        isOver = true;
                        break;
                    default:
                        System.out.println(INPUT_ERROR_MESSAGE);
                        break;
                }
            } catch (IOException exc) {
                System.out.println(INPUT_ERROR_MESSAGE);
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
}

