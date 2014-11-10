package GamePackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by PEfremov on 07.11.2014.
 * Вывел работу с меню в отдельный класс
 */
public enum Menu {
    SmallField  ("Маленькое:\t10х10 10 мин;"),
    MediumField ("Среднее:\t\t15х30 40 мин;"),
    BigField    ("Большое:\t\t25х40 99 мин;"),
    MyField     ("Свое поле;"),
    LoadFile    ("Загрузить игру из файла;"),
    Exit        ("Выход");

    private static final String MENU_HEADER      = "Выберите один из вариантов:";
    private static final String INDEX_DECORATION = ") ";
    private static final int    INDEX_INCREASE   = 1;
    private static final String INPUT_ERROR      = "Неверный вариант, попробуйте, снова!\n";

    private String description;

    Menu(String description) {
        this.description  = description;
    }

    private static void showMenu() {
        System.out.println(MENU_HEADER);
        Menu[] menuArr = Menu.values();
        for (int i = 0; i < menuArr.length; i++){
            System.out.println((i + INDEX_INCREASE) + INDEX_DECORATION + menuArr[i].description);
        }
        System.out.println();
    }

    public static Menu selectFromMenu(BufferedReader BR) {
        Menu selectedElement = null;
        while (selectedElement == null) {
            showMenu();
            try {
                selectedElement = Menu.values()[Integer.valueOf(BR.readLine()) - INDEX_INCREASE];
            } catch (Exception e) {
                System.out.println(INPUT_ERROR);
            }
        }
        return selectedElement;
    }
}
