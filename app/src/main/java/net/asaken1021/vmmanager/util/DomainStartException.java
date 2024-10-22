package net.asaken1021.vmmanager.util;

public class DomainStartException extends Exception {
    public DomainStartException() {
        super("仮想マシンの起動に失敗しました");
    }

    public DomainStartException(Exception e) {
        super("仮想マシンの起動に失敗しました\n" + e.getLocalizedMessage());
    }
}
