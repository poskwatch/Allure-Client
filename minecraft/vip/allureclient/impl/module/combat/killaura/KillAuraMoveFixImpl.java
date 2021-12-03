package vip.allureclient.impl.module.combat.killaura;

import vip.allureclient.impl.event.player.PlayerMoveEvent;

public class KillAuraMoveFixImpl {

    public static void watchdogMoveFix(PlayerMoveEvent playerMoveEvent) {
        if (playerMoveEvent.isMoving())
            playerMoveEvent.setSpeed(0.1f);
    }

}
