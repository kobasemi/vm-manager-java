package net.asaken1021.vmmanager.util;

public class DomainCreateException extends Exception {
    public DomainCreateException() {
        super("仮想マシンの作成に失敗しました");
    }

    public DomainCreateException(Exception e) {
        super("仮想マシンの作成に失敗しました\n" + e.getLocalizedMessage());
    }
}
