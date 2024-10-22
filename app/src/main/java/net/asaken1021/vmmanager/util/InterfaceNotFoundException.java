package net.asaken1021.vmmanager.util;

public class InterfaceNotFoundException extends Exception {
    public InterfaceNotFoundException() {
        super("指定されたインターフェイスがホストにありません");
    }

    public InterfaceNotFoundException(Exception e) {
        super("指定されたインターフェイスがホストにありません\n" + e.getLocalizedMessage());
    }
}
