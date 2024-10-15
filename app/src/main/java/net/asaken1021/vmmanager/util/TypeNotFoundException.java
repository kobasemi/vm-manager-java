package net.asaken1021.vmmanager.util;

public class TypeNotFoundException extends Exception {
    public TypeNotFoundException() {
        super("指定された文字列に対応する型がありません");
    }
}
