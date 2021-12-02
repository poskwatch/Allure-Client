package vip.allureclient.visual.notification;

import java.awt.*;

public enum NotificationType {
    SUCCESS(Color.GREEN.darker()),
    INFO(Color.WHITE.darker()),
    WARNING(Color.YELLOW.darker()),
    ERROR(Color.RED.darker());

    public final Color notificationColor;

    NotificationType(Color notificationColor) {
        this.notificationColor = notificationColor;
    }

}
