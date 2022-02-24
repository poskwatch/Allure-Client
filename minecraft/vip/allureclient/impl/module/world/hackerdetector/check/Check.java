package vip.allureclient.impl.module.world.hackerdetector.check;

import net.minecraft.entity.player.EntityPlayer;

import java.util.function.Consumer;

public interface Check {

    Consumer<EntityPlayer> runPlayerCheck();

}
