package net.asaken1021.vmmanager.util;

public class ConnectException extends Exception {
    public ConnectException() {
        super("ハイパーバイザへの接続に失敗しました");
    }

    public ConnectException(Exception e) {
        super("ハイパーバイザへの接続に失敗しました\n" + e.getLocalizedMessage());
    }
}
