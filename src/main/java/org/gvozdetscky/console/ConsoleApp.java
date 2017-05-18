package org.gvozdetscky.console;

import org.gvozdetscky.logic.CryptographerOFB;

import java.io.Console;

public class ConsoleApp {

    private CryptographerOFB cryptographerOFB;

    private boolean isArch;
    private boolean isBase64;
    private String password;

    public ConsoleApp() {
        cryptographerOFB = new CryptographerOFB();
    }

    public void launchConsoleApp(String args[]) throws Exception {
        Console console = System.console();
        System.out.print("Использовать пароль? ");
        if (console.readLine().equals("y")) {
            System.out.print("Введите пароль ");
            password = new String(console.readPassword());
        } else {
            password = null;
        }
        System.out.print("Использовать архивацию? ");
        isArch = (console.readLine().equals("y")) ? true : false;
        System.out.print("Использовать трансп. кодирование? ");
        isBase64 = (console.readLine().equals("y")) ? true : false;

        if (args[1].equals("e")) {
            cryptographerOFB.encrypt(args[0], password, isArch, isBase64);
        } else if (args[1].equals("d")) {
            cryptographerOFB.decrypt(args[0], password, isArch, isBase64);
        }
    }
}
