package vip.allureclient.impl.module.combat;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.NetworkUtil;
import vip.allureclient.base.util.client.Stopwatch;
import vip.allureclient.base.util.player.IRotations;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.base.util.player.PacketUtil;
import vip.allureclient.base.util.visual.glsl.GLUtil;
import vip.allureclient.impl.event.events.player.PlayerMoveEvent;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;
import vip.allureclient.impl.event.events.visual.Render3DEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.impl.property.MultiSelectEnumProperty;
import vip.allureclient.impl.property.ValueProperty;

import java.util.ArrayList;
import java.util.Comparator;

public class KillAura extends Module implements IRotations {

    private final Stopwatch apsStopwatch = new Stopwatch();

    private final ValueProperty<Integer> averageAPSProperty = new ValueProperty<>("Average APS", 13, 1, 20, this);

    private final ValueProperty<Double> attackRangeProperty = new ValueProperty<>("Attack Range", 4.1D, 0.0D, 6.0D, this);

    private final BooleanProperty autoBlockProperty = new BooleanProperty("Auto Block", true, this);

    private final MultiSelectEnumProperty<Targets> targetsProperty = new MultiSelectEnumProperty<>("Targets", this, Targets.PLAYERS);

    private final EnumProperty<TargetingMode> targetingModeProperty = new EnumProperty<>("Target by", TargetingMode.DISTANCE, this);

    private final EnumProperty<AttackingMode> attackingModeProperty = new EnumProperty<>("Attacking Mode", AttackingMode.ALWAYS, this);

    private final BooleanProperty raytraceProperty = new BooleanProperty("Ray trace", true, this);

    private final BooleanProperty followTargetProperty = new BooleanProperty("Follow Target", false, this);

    private final BooleanProperty renderRangeProperty = new BooleanProperty("Render Range", true, this);

    private Entity currentTarget;
    private boolean isBlocking;

    public KillAura(){
        super("Kill Aura", Keyboard.KEY_Y, ModuleCategory.COMBAT);
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class, priority = Priority.HIGH)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = (event -> {
                // Create cache for world's loaded entities
                ArrayList<Entity> loadedEntityCache = new ArrayList<>(mc.theWorld.loadedEntityList);
                // Remove if deemed invalid by method
                loadedEntityCache.removeIf(entity -> !isEntityValid(entity));

                // Sort the cache by (Distance/Health/Hurt-Time) based on respective settings
                switch (targetingModeProperty.getPropertyValue()) {
                    case DISTANCE:
                        loadedEntityCache.sort(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity)));
                        break;
                    case HEALTH:
                        loadedEntityCache.sort(Comparator.comparingDouble(entity -> ((EntityLivingBase) (entity)).getHealth()));
                        break;
                    case HURT_TIME:
                        loadedEntityCache.sort(Comparator.comparingInt(entity -> ((EntityLivingBase) (entity)).hurtTime));
                        break;
                }

                if (event.isPre()) {
                    if (!loadedEntityCache.isEmpty()) {
                        // Set current target to the first entity in cache
                        currentTarget = loadedEntityCache.get(0);
                        if (currentTarget != null) {
                            // Hypixel doesn't need actual rotations for hitting
                            if (!NetworkUtil.isOnHypixel())
                                setRotations(event, getRotations(), true);
                            // Visual for Hypixel playing
                            else {
                                mc.thePlayer.rotationYawHead = getRotations()[0];
                                mc.thePlayer.renderYawOffset = getRotations()[0];
                                mc.thePlayer.rotationPitchHead = getRotations()[1];
                            }
                            if (mc.objectMouseOver.entityHit != null) {
                                if (raytraceProperty.getPropertyValue() && !mc.objectMouseOver.entityHit.equals(currentTarget))
                                    return;
                            }
                            // Attack if APS ticks have reached necessary point...
                            if (apsStopwatch.hasReached(1000 / averageAPSProperty.getPropertyValue())) {
                                // Unless it isn't the third tick (Tick attack mode)
                                if (attackingModeProperty.getPropertyValue().equals(AttackingMode.TICK) && mc.thePlayer.ticksExisted % 3 != 0)
                                    return;
                                mc.thePlayer.swingItem();
                                PacketUtil.sendPacketDirect(new C02PacketUseEntity(currentTarget, C02PacketUseEntity.Action.ATTACK));
                                apsStopwatch.reset();
                            }
                        }
                    }
                    else {
                        currentTarget = null;
                    }
                }
                else {
                    // Send C08 to tell the server the player is blocking
                    if (autoBlockProperty.getPropertyValue() && currentTarget != null && isHoldingSword()){
                        PacketUtil.sendPacketDirect(new C08PacketPlayerBlockPlacement(new BlockPos(-Double.MIN_VALUE, -Double.MIN_VALUE, -Double.MIN_VALUE),
                                255, mc.thePlayer.getHeldItem(), 0.0f, 0.0f, 0.0f));
                        isBlocking = true;
                    }
                    else
                        isBlocking = false;
                }
            });
            @EventHandler(events = PlayerMoveEvent.class, priority = Priority.VERY_HIGH)
            final IEventCallable<PlayerMoveEvent> onPlayerMove = event -> {
                if (followTargetProperty.getPropertyValue() && currentTarget != null) {
                    final double[] calculations = {
                            (-Math.sin(Math.toRadians(-getCurrentTarget().rotationYaw))) * MovementUtil.getBaseMoveSpeed(),
                            (Math.cos(Math.toRadians(-getCurrentTarget().rotationYaw))) * MovementUtil.getBaseMoveSpeed()
                    };
                    event.setX(calculations[0]);
                    event.setZ(calculations[1]);
                }
            };
            @EventHandler(events = Render3DEvent.class, priority = Priority.HIGH)
            final IEventCallable<Render3DEvent> onRender3D = (event -> {
                // Render range using a 3D ellipse
                if (renderRangeProperty.getPropertyValue())
                    GLUtil.gl3DEllipse(mc.thePlayer, event.getPartialTicks(), 100, attackRangeProperty.getPropertyValue().floatValue(), 0x30FFFFFF);
            });
        });
    }

    @Override
    public void onEnable() {
        currentTarget = null;
        apsStopwatch.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        currentTarget = null;
        isBlocking = false;
        super.onDisable();
    }

    private boolean isEntityValid(Entity entity) {
        if(!(entity instanceof EntityLivingBase))
            return false;
        if (entity.isDead ||
                mc.thePlayer.getDistanceToEntity(entity) > attackRangeProperty.getPropertyValue() ||
                entity == mc.thePlayer || !(((EntityLivingBase) (entity)).getHealth() > 0))
            return false;
        boolean mobs = targetsProperty.isSelected(Targets.MONSTERS);
        if (!mobs && entity instanceof EntityMob)
            return false;
        boolean players = targetsProperty.isSelected(Targets.PLAYERS);
        if (!players && entity instanceof EntityPlayer)
            return false;
        boolean animals = targetsProperty.isSelected(Targets.ANIMALS);
        if (!animals && entity instanceof EntityAnimal)
            return false;
        if (entity instanceof EntityPlayer) {
            boolean antiBot = AntiBot.getInstance().isToggled()
                    && (AntiBot.getInstance().antiBotModeProperty.getPropertyValue().equals(AntiBot.AntiBotMode.WATCHDOG)
                    || AntiBot.getInstance().antiBotModeProperty.getPropertyValue().equals(AntiBot.AntiBotMode.WATCHDOG));
            if (antiBot && !NetworkUtil.isPlayerPingValid((EntityPlayer) entity))
                return false;
        }
        return targetsProperty.isSelected(Targets.INVISIBLES) || !entity.isInvisible();
    }

    private boolean isHoldingSword() {
        if (mc.thePlayer.getCurrentEquippedItem() == null)
            return false;
        return mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }

    public boolean isBlocking() {
        return isBlocking;
    }

    public Entity getCurrentTarget() {
        return currentTarget;
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
        DISTANCE("Distance"),
        HEALTH("Health"),
        HURT_TIME("Hurt Time");

        private final String name;

        TargetingMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private enum AttackingMode {
        ALWAYS("Always"),
        TICK("Tick");

        private final String name;

        AttackingMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private enum Targets {
        PLAYERS("Players"),
        MONSTERS("Monsters"),
        ANIMALS("Animals"),
        INVISIBLES("Invisibles");

        private final String name;

        Targets(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static KillAura getInstance() {
        return (KillAura) AllureClient.getInstance().getModuleManager().getModuleOrNull("Kill Aura");
    }
}
