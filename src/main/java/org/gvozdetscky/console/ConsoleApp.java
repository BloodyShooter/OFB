package org.gvozdetscky.console;

import org.gvozdetscky.exception.KeyException;
import org.gvozdetscky.logic.CryptographerOFB;

import java.io.Console;
import java.io.FileNotFoundException;

public class ConsoleApp {

    private CryptographerOFB cryptographerOFB;
    private Console console;

    private boolean isArch;
    private boolean isBase64;
    private String password;

    public ConsoleApp() {
        cryptographerOFB = new CryptographerOFB();
        console = System.console();
    }

    public void launchConsoleApp(String args[]) throws Exception {

        validation(args);

        consoleDialogForUser();

        action(args);
    }

    private void action(String[] args) throws FileNotFoundException, KeyException {
        if (args[1].equals("e")) {
            encrypt(args[0]);
        } else if (args[1].equals("d")) {
            decrypt(args[0]);
        }
    }

    private void consoleDialogForUser() {
        System.out.print("Использовать пароль? ");
        if (console.readLine().equals("y")) {
            System.out.print("Введите пароль ");
            password = new String(console.readPassword());
        } else {
            password = null;
        }
        System.out.print("Использовать архивацию? ");
        isArch = console.readLine().equals("y");
        System.out.print("Использовать трансп. кодирование? ");
        isBase64 = console.readLine().equals("y");
    }

    private void decrypt(String arg) throws KeyException, FileNotFoundException {
        cryptographerOFB.decrypt(arg, password, isArch, isBase64);
    }

    private void encrypt(String arg) throws FileNotFoundException {
        cryptographerOFB.encrypt(arg, password, isArch, isBase64);
    }

    private void validation(String args[]) {
        if (args.length < 2) {
            System.err.println("Неправильное количество аргументов");
            System.exit(1);
        }
    }
}
