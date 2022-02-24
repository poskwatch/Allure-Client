package vip.allureclient.impl.module.combat;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.visual.glsl.GLUtil;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;
import vip.allureclient.impl.event.events.visual.Render2DEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.EnumProperty;

import java.util.ArrayList;
import java.util.Comparator;

public class AimBot extends Module {

    // Property to determine whether to silently aim
    private final BooleanProperty silentAimProperty = new BooleanProperty("Silent Aim", false, this);

    // Property to prioritize which body part to aim at
    private final EnumProperty<BodyPartPriority> bodyPartPriorityProperty = new EnumProperty<>("Prioritize", BodyPartPriority.BODY, this);

    // Current targeted entity
    private Entity currentTarget;

    public AimBot() {
        super("Aim Bot", ModuleCategory.COMBAT);
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = (event -> {
                // Create cache of world entities
                final ArrayList<Entity> loadedWorldEntitiesCache = new ArrayList<>(mc.theWorld.loadedEntityList);
                // Sort cache based on distance to player
                loadedWorldEntitiesCache.sort(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity)));
                // Remove if it's yourself
                loadedWorldEntitiesCache.removeIf(entity -> entity.equals(mc.thePlayer));
                // Create current target, at the first spot in cache
                if (!loadedWorldEntitiesCache.isEmpty()) {
                    currentTarget = loadedWorldEntitiesCache.get(0);
                    // If the target isn't null, aim to it
                    if (currentTarget instanceof EntityPlayer) {
                        final float[] aimDirections = getAimDirections(currentTarget, bodyPartPriorityProperty.getPropertyValue());
                        final float yaw = aimDirections[0];
                        final float pitch = aimDirections[1];
                        // If should silently rotate, modify outgoing direction packets instead of client side
                        if (silentAimProperty.getPropertyValue()) {
                            event.setYaw(yaw, true);
                            event.setPitch(pitch, true);
                        }
                        else {
                            mc.thePlayer.rotationYaw = yaw;
                            mc.thePlayer.rotationPitch = pitch;
                        }
                    }
                }
                else
                    currentTarget = null;
            });
            @EventHandler(events = Render2DEvent.class)
            final IEventCallable<Render2DEvent> onRender2D = (event -> {
                // Render an indicator of target and health
                final ScaledResolution sr = event.getScaledResolution();
                final int width = sr.getScaledWidth();
                final int height = sr.getScaledHeight();
                if (currentTarget != null && currentTarget instanceof EntityPlayer) {
                    // Background rectangle
                    GLUtil.glRoundedFilledQuad(width/2.0F - 65, height/2.0F + 15, 130, 30, 8, 0x90909090);
                    AllureClient.getInstance().getFontManager().csgoFontRenderer.drawCenteredString(
                            String.format("Currently Targeting: %s", currentTarget.getName()),
                            width/2.0F,
                            height/2.0F + 25.0F,
                            0xFFFFFFFF
                    );
                    // If living, render health indicator
                    if (currentTarget instanceof EntityLivingBase) {
                        final EntityLivingBase livingTarget = (EntityLivingBase) currentTarget;
                        AllureClient.getInstance().getFontManager().csgoFontRenderer.drawCenteredString(
                                String.format("Health: %.1f", livingTarget.getHealth()),
                                width/2.0F,
                                height/2.0F + 35.0F,
                                0xFFFFFFFF
                        );
                    }
                }
            });
        });
    }

    // Method for getting aim calculation
    // Returns float array storing { yaw, pitch }
    private float[] getAimDirections(Entity targetEntity, BodyPartPriority priority) {
        final float[] directions = new float[2];

        // First, calculate distance to target entity
        final double xDistance = targetEntity.posX - mc.thePlayer.posX,
                yDistance = targetEntity.posY - mc.thePlayer.posY,
                zDistance = targetEntity.posZ - mc.thePlayer.posZ;

        // Create square root of X and Z distance

        final double sqrtDistance = Math.sqrt(xDistance * xDistance + zDistance * zDistance);

        // Yaw calculation: atan2(x, y) * 180 / PI

        final float yaw = (float) (Math.atan2(zDistance, xDistance) * 180 / Math.PI) - 90;

        // Pitch calculation: -(atan2(y, sqrtDistance) * 180 / PI)
        final float pitch = (float) -(Math.atan2(yDistance, sqrtDistance) * 180 / Math.PI);

        // Pitch offset for prioritization (Minecraft doesn't have hit-boxes like CS:GO)
        float pitchOffset = 0.0F;

        // Based on priority, modify pitch offset
        switch (priority) {
            case HEAD:
                pitchOffset = 0.0F;
                break;
            case BODY:
                pitchOffset = 15.0F;
                break;
            case FEET:
                pitchOffset = 35.0F;
                break;
        }

        // Set values within float array

        directions[0] = yaw;
        directions[1] = pitch + pitchOffset;

        return directions;
    }

    // Enum for body part prioritization
    private enum BodyPartPriority {
        BODY("Body"),
        HEAD("Head"),
        FEET("Feet");

        final String name;

        BodyPartPriority(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Override
    public void onEnable() {
        // Set target to null
        this.currentTarget = null;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        // Set target to null
        this.currentTarget = null;
        super.onDisable();
    }
}
