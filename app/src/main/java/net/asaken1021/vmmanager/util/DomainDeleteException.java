package net.asaken1021.vmmanager.util;

public class DomainDeleteException extends Exception {
    public DomainDeleteException() {
        super("仮想マシンの削除に失敗しました");
    }

    public DomainDeleteException(Exception e) {
        super("仮想マシンの削除に失敗しました\n" + e.getLocalizedMessage());
    }
}
