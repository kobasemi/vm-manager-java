package net.asaken1021.vmmanager.util;

public class DomainLookupException extends Exception {
    public DomainLookupException() {
        super("指定された仮想マシンが見つかりませんでした");
    }

    public DomainLookupException(Exception e) {
        super("指定された仮想マシンが見つかりませんでした\n" + e.getLocalizedMessage());
    }
}
