package vip.allureclient.visual.notification;


import java.util.concurrent.LinkedBlockingQueue;

public class NotificationManager {

    private static final LinkedBlockingQueue<Notification> pendingNotifications = new LinkedBlockingQueue<>();

    private static Notification currentNotification = null;

    public void createNotification(String title, String body, long time, NotificationType type){
        pendingNotifications.add(new Notification(title, body, time, type));
    }

    private void update() {
        if (currentNotification != null && !currentNotification.isShown()) {
            currentNotification = null;
        }
        if (currentNotification == null && !pendingNotifications.isEmpty()) {
            currentNotification = pendingNotifications.poll();
            currentNotification.show();
        }
    }

    public void render() {
        update();
        if (currentNotification != null)
            currentNotification.render();
    }

}
