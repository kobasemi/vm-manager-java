package net.asaken1021.vmmanager.util.vm.video;

import net.asaken1021.vmmanager.util.TypeNotFoundException;

public enum VideoType {
    VIDEO_VIRTIO("virtio"),
    VIDEO_VGA("vga");

    private String text;

    private VideoType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static VideoType getTypeByString(String type) throws TypeNotFoundException {
        if (type.equals(VideoType.VIDEO_VIRTIO.getText())) {
            return VideoType.VIDEO_VIRTIO;
        } else if (type.equals(VideoType.VIDEO_VGA.getText())) {
            return VideoType.VIDEO_VGA;
        }

        throw new TypeNotFoundException();
    }
}
