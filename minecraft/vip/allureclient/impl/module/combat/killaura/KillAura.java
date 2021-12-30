package vip.allureclient.impl.module.combat.killaura;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;
import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.NetworkUtil;
import vip.allureclient.base.util.client.TimerUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.GLUtil;
import vip.allureclient.impl.event.network.PacketSendEvent;
import vip.allureclient.impl.event.player.PlayerMoveEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.event.visual.Render3DEvent;
import vip.allureclient.impl.module.combat.AntiBot;
import vip.allureclient.impl.module.visual.TargetHUD;
import vip.allureclient.base.util.player.IRotations;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.impl.property.MultiSelectEnumProperty;
import vip.allureclient.impl.property.ValueProperty;

import java.util.ArrayList;
import java.util.Comparator;

@ModuleData(moduleName = "Kill Aura", moduleBind = Keyboard.KEY_Y, moduleCategory = ModuleCategory.COMBAT)
public class KillAura extends Module implements IRotations {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;

    @EventListener
    EventConsumer<PlayerMoveEvent> onPlayerMoveEvent;

    @EventListener
    EventConsumer<Render3DEvent> onRender3DEvent;

    private final TimerUtil apsTimerUtil = new TimerUtil();

    private final ValueProperty<Integer> averageAPSProperty = new ValueProperty<>("Average APS", 13, 1, 20, this);

    private final ValueProperty<Double> attackRangeProperty = new ValueProperty<>("Attack Range", 4.1D, 0.0D, 6.0D, this);

    private final BooleanProperty autoBlockProperty = new BooleanProperty("Auto Block", true, this);

    private final MultiSelectEnumProperty<Targets> targetsProperty = new MultiSelectEnumProperty<>("Targets", this, Targets.Players);

    private final EnumProperty<TargetingMode> targetingModeProperty = new EnumProperty<>("Target by", TargetingMode.Distance, this);

    private final BooleanProperty movementFixProperty = new BooleanProperty("Move Fix", false, this);

    private final EnumProperty<MovementFixMode> movementFixModeProperty = new EnumProperty<MovementFixMode>("Mode", MovementFixMode.Watchdog, this) {
        @Override
        public boolean isPropertyHidden() {
            return !movementFixProperty.getPropertyValue();
        }
    };

    private final BooleanProperty renderRangeProperty = new BooleanProperty("Render Range", true, this);

    private Entity currentTarget;
    private boolean isBlocking;

    public KillAura(){
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
                        TargetHUD.getInstance().scaleAnimationTarget = 1.0;
                        setRotations(updatePositionEvent, getRotations(), true);
                        if (apsTimerUtil.hasReached(1000 / averageAPSProperty.getPropertyValue())) {
                            Wrapper.getPlayer().swingItem();
                            Wrapper.sendPacketDirect(new C02PacketUseEntity(currentTarget, C02PacketUseEntity.Action.ATTACK));
                            for (int i = 0; i < 10; i++) {
                                World targetWorld = currentTarget.getEntityWorld();
                                double x, y, z;
                                x = currentTarget.posX;
                                y = currentTarget.posY;
                                z = currentTarget.posZ;
                                targetWorld.spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                                        x + ThreadLocalRandom.current().nextDouble(-0.5, 0.5),
                                        y + ThreadLocalRandom.current().nextDouble(-1, 1),
                                        z + ThreadLocalRandom.current().nextDouble(-0.5, 0.5), 23, 23, 23, 152);
                            }
                            apsTimerUtil.reset();
                        }
                    }
                }
                else {
                    currentTarget = null;
                    TargetHUD.getInstance().scaleAnimationTarget = 0;
                }
            }
            else {
                if (autoBlockProperty.getPropertyValue() && currentTarget != null && isHoldingSword()){
                    Wrapper.sendPacketDirect(new C08PacketPlayerBlockPlacement(new BlockPos(-Double.MIN_VALUE, -Double.MIN_VALUE, -Double.MIN_VALUE),
                            255, Wrapper.getPlayer().getCurrentEquippedItem(), 0.0f, 0.0f, 0.0f));
                    isBlocking = true;
                }
                else
                    isBlocking = false;
            }
        });

        this.onPacketSendEvent = (packetSendEvent -> {

        });

        this.onPlayerMoveEvent = (playerMoveEvent -> {
           if (movementFixProperty.getPropertyValue() && movementFixModeProperty.getPropertyValue().equals(MovementFixMode.Watchdog))
               KillAuraMoveFixImpl.watchdogMoveFix(playerMoveEvent);
           if (!playerMoveEvent.isMoving()) {
               Wrapper.getMinecraft().timer.timerSpeed = 1;
           }
        });

        this.onRender3DEvent = (render3DEvent -> {
            if (renderRangeProperty.getPropertyValue())
                GLUtil.gl3DEllipse(Wrapper.getPlayer(), render3DEvent.getPartialTicks(), 100, attackRangeProperty.getPropertyValue().floatValue(), -1);
        });
    }

    @Override
    public void onEnable() {
        currentTarget = null;
        apsTimerUtil.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (isBlocking) {
            isBlocking = false;
            Wrapper.sendPacketDirect(new C08PacketPlayerBlockPlacement( new BlockPos(-1, -1, -1), 255, null, 0.0F, 0.0F, 0.0F));
        }
        currentTarget = null;
        apsTimerUtil.reset();
        Wrapper.getMinecraft().timer.timerSpeed = 1;
        super.onDisable();
    }

    private boolean isEntityValid(Entity entity) {
        if(!(entity instanceof EntityLivingBase))
            return false;
        if (entity.isDead || Wrapper.getPlayer().getDistanceToEntity(entity) > attackRangeProperty.getPropertyValue() || entity == Wrapper.getPlayer() || !(((EntityLivingBase) (entity)).getHealth() > 0))
            return false;
        boolean mobs = targetsProperty.isSelected(Targets.Monsters);
        if (!mobs && entity instanceof EntityMob)
            return false;
        boolean players = targetsProperty.isSelected(Targets.Players);
        if (!players && entity instanceof EntityPlayer)
            return false;
        boolean animals = targetsProperty.isSelected(Targets.Animals);
        if (!animals && entity instanceof EntityAnimal)
            return false;
        if (entity instanceof EntityPlayer) {
            boolean antiBot = AntiBot.getInstance().isToggled()
                    && (AntiBot.getInstance().antiBotModeProperty.getPropertyValue().equals(AntiBot.AntiBotMode.Watchdog)
                    || AntiBot.getInstance().antiBotModeProperty.getPropertyValue().equals(AntiBot.AntiBotMode.Watchdog));
            if (antiBot && !NetworkUtil.isPlayerPingNull((EntityPlayer) entity))
                return false;
        }
        return targetsProperty.isSelected(Targets.Invisibles) || !entity.isInvisible();
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

    private float[] getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 0.5D;
        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
        return new float[]{yaw, pitch};
    }

    @Override
    public float[] getRotations() {
        double x = getCurrentTarget().posX;
        double z = getCurrentTarget().posZ;
        double y = getCurrentTarget().posY + (getCurrentTarget().getEyeHeight() / 3.1F);
        return getRotationFromPosition(x, z, y);
    }

    @Override
    public void setRotations(UpdatePositionEvent event, float[] rotations, boolean visualize) {
        event.setYaw(getRotations()[0], visualize);
        event.setPitch(getRotations()[1], visualize);
    }

    private enum TargetingMode {
        Distance,
        Health
    }

    private enum MovementFixMode {
        Watchdog
    }

    private enum Targets {
        Players,
        Monsters,
        Animals,
        Invisibles
    }
}
