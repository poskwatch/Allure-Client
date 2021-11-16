package vip.allureclient.impl.module.combat;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;
import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.TimerUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.impl.property.ValueProperty;

import java.util.ArrayList;
import java.util.Comparator;

@ModuleData(moduleName = "KillAura", keyBind = Keyboard.KEY_Y, category = ModuleCategory.COMBAT)
public class KillAura extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final TimerUtil apsTimerUtil = new TimerUtil();

    private final ValueProperty<Integer> averageAPSProperty = new ValueProperty<>("Average APS", 10, 0, 13, this);

    private final ValueProperty<Double> attackRangeProperty = new ValueProperty<>("Attack Range", 4.1D, 0.0D, 6.0D, this);

    private final BooleanProperty autoBlockProperty = new BooleanProperty("Auto Block", true, this);

    private final BooleanProperty attackPlayersProperty = new BooleanProperty("Players", true, this),
                                  attackMobsProperty = new BooleanProperty("Monsters", false, this),
                                  attackAnimalsProperty = new BooleanProperty("Animals", false, this),
                                  attackInvisiblesProperty = new BooleanProperty("Invisibles", false, this);

    private final EnumProperty<targetingModes> targetingModeProperty = new EnumProperty<>("Target by", targetingModes.Distance, this);

    private Entity currentTarget;
    private boolean isBlocking;

    public KillAura(){
        this.onModuleEnabled = () -> {
            currentTarget = null;
            apsTimerUtil.reset();
        };

        this.onModuleDisabled = () -> {

        };

        this.onUpdatePositionEvent = (updatePositionEvent -> {
            setModuleSuffix(targetingModeProperty.getEnumValueAsString());
            ArrayList<Entity> targetEntities = new ArrayList<>(Wrapper.getWorld().loadedEntityList);
            targetEntities.removeIf(entity -> !isEntityValid(entity));
            switch (targetingModeProperty.getPropertyValue()) {
                case Distance:
                    targetEntities.sort(Comparator.comparingDouble(entity -> Wrapper.getPlayer().getDistanceToEntity(entity)));
                    break;
                case Health:
                    targetEntities.sort(Comparator.comparingDouble(entity -> ((EntityLivingBase) (entity)).getHealth()));
                    break;
            }
            if (updatePositionEvent.isPre()) {
                if (!targetEntities.isEmpty()) {
                    currentTarget = targetEntities.get(0);
                    if (currentTarget != null) {
                        if (isBlocking) {
                            isBlocking = false;
                            if (isHoldingSword()) {
                                Wrapper.sendPacketDirect(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            }
                        }
                        updatePositionEvent.setYaw(getNCPRotations(currentTarget)[0]);
                        updatePositionEvent.setPitch(getNCPRotations(currentTarget)[1]);
                        if (apsTimerUtil.hasReached(1000 / averageAPSProperty.getPropertyValue())) {
                            Wrapper.getPlayer().swingItem();
                            Wrapper.sendPacketDirect(new C02PacketUseEntity(currentTarget, C02PacketUseEntity.Action.ATTACK));
                            apsTimerUtil.reset();
                        }
                    }
                }
                else {
                    currentTarget = null;
                }
            }
            else {
                if(autoBlockProperty.getPropertyValue() && currentTarget != null && isHoldingSword()){
                    Wrapper.getPlayer().setItemInUse(Wrapper.getPlayer().getCurrentEquippedItem(), Wrapper.getPlayer().getCurrentEquippedItem().getMaxItemUseDuration());
                    if (!isBlocking) {
                        Wrapper.sendPacketDirect(new C08PacketPlayerBlockPlacement( new BlockPos(-1, -1, -1), 255, null, 0.0F, 0.0F, 0.0F));
                        isBlocking = true;
                    }
                }
            }
        });
    }

    private boolean isEntityValid(Entity entity) {
        if(!(entity instanceof EntityLivingBase))
            return false;
        if (entity.isDead || Wrapper.getPlayer().getDistanceToEntity(entity) > attackRangeProperty.getPropertyValue() || entity == Wrapper.getPlayer() || !(((EntityLivingBase) (entity)).getHealth() > 0))
            return false;
        if (!attackMobsProperty.getPropertyValue() && entity instanceof EntityMob)
            return false;
        if (!attackPlayersProperty.getPropertyValue() && entity instanceof EntityPlayer)
            return false;
        if (!attackAnimalsProperty.getPropertyValue() && entity instanceof EntityAnimal)
            return false;
        return attackInvisiblesProperty.getPropertyValue() || !entity.isInvisible();
    }

    private static float[] getNCPRotations(Entity entity) {
        float pitch;
        EntityPlayerSP player = Wrapper.getPlayer();
        double xDist = entity.posX - player.posX;
        double zDist = entity.posZ - player.posZ;
        double yDist = entity.posY - player.posY;
        double dist = StrictMath.sqrt(xDist * xDist + zDist * zDist);
        AxisAlignedBB entityBB = entity.getEntityBoundingBox().expand(0.10000000149011612D, 0.10000000149011612D, 0.10000000149011612D);
        double playerEyePos = player.posY + player.getEyeHeight();
        boolean close = (dist < 2.0D && Math.abs(yDist) < 2.0D);
        if (close && playerEyePos > entityBB.minY) {
            pitch = 60.0F;
        } else {
            yDist = (playerEyePos > entityBB.maxY) ? (entityBB.maxY - playerEyePos) : (
                    (playerEyePos < entityBB.minY) ? (entityBB.minY - playerEyePos) :
                            0.0D);
            pitch = (float)-(StrictMath.atan2(yDist, dist) * 57.29577951308232D);
        }

        float yaw = (float)(StrictMath.atan2(zDist, xDist) * 57.29577951308232D) - 90.0F;
        if (close) {
            int inc = (dist < 1.0D) ? 180 : 90;
            yaw = (Math.round(yaw / inc) * inc);
        }
        return new float[] { yaw, pitch };
    }

    private boolean isHoldingSword() {
        if (Wrapper.getPlayer().getCurrentEquippedItem() == null) {
            return false;
        }
        return Wrapper.getPlayer().getCurrentEquippedItem().getItem() instanceof ItemSword;
    }

    public boolean isBlocking() {
        return isBlocking;
    }

    public Entity getCurrentTarget() {
        return currentTarget;
    }

    public static KillAura getInstance() {
        return (KillAura) AllureClient.getInstance().getModuleManager().getModuleByClass.apply(KillAura.class);
    }

    private enum targetingModes {
        Distance,
        Health
    }
}
