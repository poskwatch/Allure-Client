package vip.allureclient.impl.module.world.hackerdetector.check.impl;

import net.minecraft.entity.player.EntityPlayer;
import vip.allureclient.AllureClient;
import vip.allureclient.impl.module.world.hackerdetector.check.Check;
import vip.allureclient.visual.notification.NotificationType;

import java.util.function.Consumer;

public class FlyCheck implements Check {
    @Override
    public Consumer<EntityPlayer> runPlayerCheck() {
        return entityPlayer -> {
            if (entityPlayer.motionY == 0) {
                AllureClient.getInstance().getNotificationManager().addNotification("Hacker Detected",
                        String.format("%s just used flight", entityPlayer.getName()), 3500, NotificationType.WARNING);
            }
        };
    }
}
