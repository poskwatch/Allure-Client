package vip.allureclient.impl.module.combat.killaura;

import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.impl.event.player.PlayerMoveEvent;

public class KillAuraMoveFixImpl {

    public static void watchdogMoveFix(PlayerMoveEvent playerMoveEvent) {
        if (playerMoveEvent.isMoving()) {
            playerMoveEvent.setSpeed(MovementUtil.getBaseMoveSpeed() * 0.4);
            Wrapper.getMinecraft().timer.timerSpeed = 0.87f;
        }
    }

}
