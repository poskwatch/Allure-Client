package vip.allureclient.visual.notification;

import java.awt.*;

public enum NotificationType {

    SUCCESS(0xff02a312),
    WARNING(0xffa39802),
    ERROR(0xffa30202),
    INFO(0xffc4c4c4);

    private final int color;

    NotificationType(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
