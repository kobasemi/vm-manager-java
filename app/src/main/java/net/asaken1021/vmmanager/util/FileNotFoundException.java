package net.asaken1021.vmmanager.util;

public class FileNotFoundException extends Exception {
    public FileNotFoundException() {
        super("指定されたファイルが見つかりません");
    }

    public FileNotFoundException(Exception e) {
        super("指定されたファイルが見つかりません\n" + e.getLocalizedMessage());
    }
}
