package vip.allureclient.impl.event.player;

import net.minecraft.entity.EntityLivingBase;
import vip.allureclient.base.event.Event;

public class PlayerHurtSoundEvent implements Event {

    private final EntityLivingBase entityLivingBase;

    public PlayerHurtSoundEvent(EntityLivingBase entityLivingBase) {
        this.entityLivingBase = entityLivingBase;
    }

    public EntityLivingBase getEntityLivingBase() {
        return entityLivingBase;
    }
}
