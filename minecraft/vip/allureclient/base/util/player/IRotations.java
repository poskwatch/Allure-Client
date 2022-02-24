package vip.allureclient.base.util.player;

import vip.allureclient.impl.event.events.player.UpdatePositionEvent;

public interface IRotations {

    float[] getRotations();

    void setRotations(UpdatePositionEvent event, float[] rotations, boolean visualize);

}
