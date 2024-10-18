package net.asaken1021.vmmanager.util;

import org.libvirt.jna.Libvirt.VirErrorCallback;
import org.libvirt.jna.virError;

import com.sun.jna.Pointer;

public class ErrorCallback implements VirErrorCallback {
    @Override
    public void errorCallback(Pointer userData, virError error) {
        // libvirtから直接出力されるエラーを抑制
    }
}
