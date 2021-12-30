package vip.allureclient.visual.notification;

import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;

public class NotificationManager {

    private final ArrayList<Notification> queuedNotifications;

    public NotificationManager() {
        this.queuedNotifications = new ArrayList<>();
    }

    public void render(ScaledResolution scaledResolution, double yOffset) {
        Notification remove = null;
        for (int i = 0; i < queuedNotifications.size(); i++) {
            Notification current = queuedNotifications.get(i);
            if (current.completed)
                remove = current;
            current.render(scaledResolution, yOffset, i);
        }
        if (remove != null)
            queuedNotifications.remove(remove);
    }

    public void addNotification(String title, String body, long duration, NotificationType notificationType) {
        this.queuedNotifications.add(new Notification(title, body, duration, notificationType));
    }
}
