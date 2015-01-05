package GamePackage;

import GamePackage.FieldPackage.Field;
import GamePackage.FieldPackage.FieldBorder;

import java.io.*;

/**
 * Created by PEfremov on 21.10.2014.
 * Да начнется игра!!!
 */
public class GameStart {
    private static final BufferedReader BR = new BufferedReader(new InputStreamReader(System.in));
    private static final String WIN_MESSAGE = "Поздравляем!!! Вы выйграли игру!!!";
    private static final String LOSE_MESSAGE = "Вы проиграли!!! Попробуйте снова!!!";
    private static final String START_MESSAGE = "Игра началась!";
    private static final String END_MESSAGE = "Спасибо за игру!!!";
    private static final String INPUT_ERROR_MESSAGE = "Некорректный ввод. Повторите...";
    private static final String SAVE_COMMAND = "save";
    private static final String EXIT_COMMAND = "exit";
    private static final String REPEAT_COMMAND = "repeat";
    private static final String SURRENDER_COMMAND = "die";
    private static final String FLAG_COMMAND = "flag";


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
                    String userCommand = getUserCommand();
                    executeUserCommand(userCommand, gameField);
                }

                if (gameField.isWin() && gameField.isComplete()) {
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

    private static String getUserCommand() {
        String command = null;
        while (command == null) {
            try {
                System.out.println("Чтобы открыть ячейку введите координаты: " + FieldBorder.VerticalAxisName +
                                   SaveLoad.WIGHT_HEIGHT_SPLITTER + FieldBorder.HorizontalAxisName + "\n" +
                                   "Чтобы установить флаг введите: " + FLAG_COMMAND + " " +
                                   FieldBorder.VerticalAxisName + SaveLoad.WIGHT_HEIGHT_SPLITTER +
                                   FieldBorder.HorizontalAxisName + "\n" +
                                   "Чтобы сохранить игру введите: " + SAVE_COMMAND + "; \n" +
                                   "Для завершения введите:  " + SURRENDER_COMMAND);

                command = BR.readLine();
                command = command.trim().toLowerCase();
                if (!command.equals(SAVE_COMMAND) && !command.equals(SURRENDER_COMMAND) &&
                        !command.contains(SaveLoad.WIGHT_HEIGHT_SPLITTER)) {
                    command = null;
                    throw new IOException();
                }
            } catch (IOException exc) {
                System.out.println(INPUT_ERROR_MESSAGE);
            }
        }
        return command;

    }

    private static void executeUserCommand(String command, Field field) {
        switch (command) {
            case SAVE_COMMAND:
                SaveLoad.saveGame(field);
                break;
            case SURRENDER_COMMAND:
                field.openField();

                break;
            default:
                if (command.contains(FLAG_COMMAND)) {
                    command = command.substring(command.indexOf(FLAG_COMMAND) + FLAG_COMMAND.length());
                    String[] coordArr = command.split(SaveLoad.WIGHT_HEIGHT_SPLITTER);
                    try {
                        field.setFlag(Integer.parseInt(coordArr[0].trim()), Integer.parseInt(coordArr[1].trim()));
                    } catch (Exception e) {
                        System.out.println(INPUT_ERROR_MESSAGE);
                    }
                } else {
                    String[] coordArr = command.split(SaveLoad.WIGHT_HEIGHT_SPLITTER);

                    try {
                        field.openCell(Integer.parseInt(coordArr[0].trim()), Integer.parseInt(coordArr[1].trim()), true);
                    } catch (Exception e) {
                        System.out.println(INPUT_ERROR_MESSAGE);
                    }
                }
                break;
        }
    }

    private static boolean isGameOver() { //TODO Change to case select: 1 - repeat, 2 - exit;

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
                field = inputFieldParam();
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

    private static Field inputFieldParam() {
        int wight = 0;
        while (wight <= 0) {
            try {
                System.out.print("Введите количесвто колонок (от 10 до 100): ");
                String inputValue = BR.readLine();
                wight = Integer.parseInt(inputValue);
                if (wight < 10 || wight > 101) {
                    wight = 0;
                    throw new Exception();
                }
            } catch (Exception e) {
                System.out.println(INPUT_ERROR_MESSAGE);
            }
        }
        int height = 0;
        while (height <= 0) {
            try {
                System.out.print("Введите количесвто строк (от 10 до 100): ");
                String inputValue = BR.readLine();
                height = Integer.parseInt(inputValue);
                if (height < 10 || height > 101) {
                    height = 0;
                    throw new Exception();
                }
            } catch (Exception e) {
                System.out.println(INPUT_ERROR_MESSAGE);
            }
        }

        int bombs = 0;
        int bombMaxCount = wight * height;
        while (bombs <= 0) {
            try {
                System.out.print("Введите количесвто бомб (от 10 до " + bombMaxCount + "): ");
                String inputValue = BR.readLine();
                bombs = Integer.parseInt(inputValue);
                if (bombs < 10 || bombs > bombMaxCount) {
                    bombs = 0;
                    throw new Exception();
                }
            } catch (Exception e) {
                System.out.println(INPUT_ERROR_MESSAGE);
            }
        }




        return new Field(height, wight, bombs);
    }
}

