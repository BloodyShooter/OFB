package org.gvozdetscky.console;

import org.gvozdetscky.exception.KeyException;
import org.gvozdetscky.logic.CryptographerOFB;

import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;

public class ConsoleApp {

    private final static int COUNT_ARGS = 2;

    private CryptographerOFB cryptographerOFB;
    private Console console;

    private boolean isArch;
    private boolean isBase64;
    private String password;
    //dfhdfhdf

    public ConsoleApp() {
        cryptographerOFB = new CryptographerOFB();
        console = System.console();
    }

    public void launchConsoleApp(String args[]) throws Exception {

        validation(args);

        consoleDialogForUser();

        action(args);

        showInfo();
    }

    private void showInfo() {
        System.out.println("Операция прошла успешно");
    }

    private void action(String[] args) throws FileNotFoundException, KeyException {
        if (args[1].equals("e")) {
            encrypt(args[0]);
        } else if (args[1].equals("d")) {
            decrypt(args[0]);
        }
    }

    private void consoleDialogForUser() {
        isUsePassword();
        isUseArch();
        isUseBase64();
    }

    private void isUseBase64() {
        System.out.print("Использовать трансп. кодирование? ");
        isBase64 = console.readLine().equals("y");
    }

    private void isUseArch() {
        System.out.print("Использовать архивацию? ");
        isArch = console.readLine().equals("y");
    }

    private void isUsePassword() {
        System.out.print("Использовать пароль? ");
        if (console.readLine().equals("y")) {
            System.out.print("Введите пароль ");
            password = new String(console.readPassword());
        } else {
            password = null;
        }
    }

    private void decrypt(String arg) throws KeyException, FileNotFoundException {
        cryptographerOFB.decrypt(arg, password, isArch, isBase64);
    }

    private void encrypt(String arg) throws FileNotFoundException {
        cryptographerOFB.encrypt(arg, password, isArch, isBase64);
    }

    private void validation(String args[]) {
        if (isCountArgsValidation(args)) {
            System.err.println("Неправильное количество аргументов");
            showPrompt();
            System.exit(1);
        } else if (isNotExistFile(args[0])) {
            System.err.println("Неправильный путь к файлу");
            showPrompt();
            System.exit(1);
        } else if (!(args[1].equals("e") || args[1].equals("d"))) {
            System.err.println("Неправильный второй аргумент");
            showPrompt();
            System.exit(1);
        }
    }

    private boolean isNotExistFile(String pathFile) {
        return !(new File(pathFile).exists());
    }

    private boolean isCountArgsValidation(String args[]) {
        return args.length < COUNT_ARGS;
    }

    private void showPrompt() {
        System.out.println();
        System.out.println("Использование: OFB.jar полный_путь_к_файлу e | d");
        System.out.println("Пример");
        System.out.println("OFB.jar C:/test.txt e");
        System.out.println("OFB.jar C:/test.txt.enc d");
        System.out.println();
    }
}
