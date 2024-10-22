package net.asaken1021.vmmanager.util;

public class DomainStopException extends Exception {
    public DomainStopException() {
        super("仮想マシンの停止に失敗しました");
    }

    public DomainStopException(Exception e) {
        super("仮想マシンの停止に失敗しました\n" + e.getLocalizedMessage());
    }
}
