package vip.allureclient.impl.event.events.player;

import io.github.poskwatch.eventbus.api.interfaces.IEvent;
import net.minecraft.entity.EntityLivingBase;

public class PlayerHurtSoundEvent implements IEvent {

    private final EntityLivingBase entityLivingBase;

    public PlayerHurtSoundEvent(EntityLivingBase entityLivingBase) {
        this.entityLivingBase = entityLivingBase;
    }

    public EntityLivingBase getEntityLivingBase() {
        return entityLivingBase;
    }
}
