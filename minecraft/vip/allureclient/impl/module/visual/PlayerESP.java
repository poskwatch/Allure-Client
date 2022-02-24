package vip.allureclient.impl.module.visual;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.NetworkUtil;
import vip.allureclient.base.util.math.MathUtil;
import vip.allureclient.base.util.visual.ColorUtil;
import vip.allureclient.base.util.visual.glsl.GLUtil;
import vip.allureclient.impl.event.events.visual.Render2DEvent;
import vip.allureclient.impl.module.combat.AntiBot;
import vip.allureclient.impl.property.ColorProperty;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.impl.property.MultiSelectEnumProperty;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerESP extends Module {

    // TODO: Manage ESP components in sub-classes

    public final ArrayList<Entity> listedEntities = new ArrayList<>();
    private final IntBuffer viewport;
    private final FloatBuffer modelView;
    private final FloatBuffer projection;
    private final FloatBuffer vector;

    private final MultiSelectEnumProperty<ESPComponents> espComponentsProperty = new MultiSelectEnumProperty<>("Components", this, ESPComponents.BOX, ESPComponents.HEALTH);

    private final ColorProperty boxColorProperty = new ColorProperty("Box Color", Color.WHITE, this) {
        @Override
        public boolean isPropertyHidden() {
            return !espComponentsProperty.isSelected(ESPComponents.BOX);
        }
    };

    private final EnumProperty<HealthBarMode> healthBarModeProperty = new EnumProperty<HealthBarMode>("Bar Mode", HealthBarMode.HEALTH_BASED, this) {
        @Override
        public boolean isPropertyHidden() {
            return !espComponentsProperty.isSelected(ESPComponents.HEALTH);
        }
    };
    private final ColorProperty solidHealthBarProperty = new ColorProperty("Health Bar Color", Color.MAGENTA, this) {
        @Override
        public boolean isPropertyHidden() {
            return !espComponentsProperty.isSelected(ESPComponents.HEALTH) || !healthBarModeProperty.getPropertyValue().equals(HealthBarMode.STATIC);
        }
    };
    private final ColorProperty gradientBarStartProperty = new ColorProperty("Bar Gradient Start", Color.GREEN, this) {
        @Override
        public boolean isPropertyHidden() {
            return !espComponentsProperty.isSelected(ESPComponents.HEALTH) || !healthBarModeProperty.getPropertyValue().equals(HealthBarMode.GRADIENT);
        }
    };
    private final ColorProperty gradientBarEndProperty = new ColorProperty("Bar Gradient End", Color.MAGENTA, this) {
        @Override
        public boolean isPropertyHidden() {
            return !espComponentsProperty.isSelected(ESPComponents.HEALTH) || !healthBarModeProperty.getPropertyValue().equals(HealthBarMode.GRADIENT);
        }
    };

    public PlayerESP() {
        super("Player ESP", ModuleCategory.VISUAL);
        viewport = GLAllocation.createDirectIntBuffer(16);
        modelView = GLAllocation.createDirectFloatBuffer(16);
        projection = GLAllocation.createDirectFloatBuffer(16);
        vector = GLAllocation.createDirectFloatBuffer(4);
        this.setListener(new IEventListener() {
            @EventHandler(events = Render2DEvent.class, priority = Priority.VERY_LOW)
            final IEventCallable<Render2DEvent> onRender2D = (event -> {
                updateEntities();
                GL11.glPushMatrix();
                //double scaling = event.getScaledResolution().getScaleFactor() / Math.pow(event.getScaledResolution().getScaleFactor(), 2.0D);
                listedEntities.forEach(entity -> {
                    // different interpolation
                    double x = entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * event.getPartialTicks());
                    double y = entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * event.getPartialTicks());
                    double z = entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * event.getPartialTicks());
                    double width = entity.width / 2;
                    double height = entity.height + (entity.isSneaking() ? -0.3D : 0.2D);
                    AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                    List<Vector3f> vectors = Arrays.asList(new Vector3f((float) aabb.minX, (float) aabb.minY, (float) aabb.minZ),
                            new Vector3f((float) aabb.minX, (float) aabb.maxY, (float) aabb.minZ),
                            new Vector3f((float) aabb.maxX, (float) aabb.minY, (float) aabb.minZ),
                            new Vector3f((float) aabb.maxX, (float) aabb.maxY, (float) aabb.minZ),
                            new Vector3f((float) aabb.minX, (float) aabb.minY, (float) aabb.maxZ),
                            new Vector3f((float) aabb.minX, (float) aabb.maxY, (float) aabb.maxZ),
                            new Vector3f((float) aabb.maxX, (float) aabb.minY, (float) aabb.maxZ),
                            new Vector3f((float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ));
                    mc.entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);

                    Vector4f position = null;

                    for (Vector3f vector : vectors) {
                        vector = project2D(event.getScaledResolution().getScaleFactor(),
                                vector.x - mc.getRenderManager().viewerPosX,
                                vector.y - mc.getRenderManager().viewerPosY,
                                vector.z - mc.getRenderManager().viewerPosZ);
                        if (vector != null && vector.z >= 0.0D && vector.z < 1.0D) {
                            if (position == null) {
                                position = new Vector4f(vector.x, vector.y, vector.z, 0.0F);
                            }
                            position.x = Math.min(vector.x, position.x);
                            position.y = Math.min(vector.y, position.y);
                            position.z = Math.max(vector.x, position.z);
                            position.w = Math.max(vector.y, position.w);
                        }
                    }

                    if (position != null) {
                        mc.entityRenderer.setupOverlayRendering();
                        double posX = position.x;
                        double posY = position.y;
                        double endPosX = position.z;
                        double endPosY = position.w;

                        GLUtil.glColor(boxColorProperty.getPropertyValueRGB());
                        if (espComponentsProperty.isSelected(ESPComponents.BOX)) {
                            final int outlineColor = 0xff000000;
                            final int color = boxColorProperty.getPropertyValueRGB();

                            Gui.drawRect(posX - 0.5D, posY + 0.5D, posX + 0.5D - 0.5D, endPosY - 0.5D, color);
                            Gui.drawRect(posX, endPosY - 0.5D, endPosX, endPosY, color);
                            Gui.drawRect(posX - 0.5D, posY, endPosX, posY + 0.5D, color);
                            Gui.drawRect(endPosX - 0.5D, posY, endPosX, endPosY, color);

                            Gui.drawRect(posX, posY + 0.5D, posX + 0.5D, endPosY - 0.5D, outlineColor);
                            Gui.drawRect(endPosX - 1, posY + 0.5D, endPosX - 0.5D, endPosY - 0.5D, outlineColor);
                            Gui.drawRect(posX, posY + 0.5D, endPosX - 0.5D, posY + 1, outlineColor);
                            Gui.drawRect(posX, endPosY - 1D, endPosX - 0.5D, endPosY - 0.5D, outlineColor);
                            Gui.drawRect(posX - 1, posY - 0.5D, endPosX + 0.5D, posY, outlineColor);
                            Gui.drawRect(posX - 1D, posY - 0.5D, posX - 0.5D, endPosY, outlineColor);
                            Gui.drawRect(endPosX, posY - 0.5D, endPosX + 0.5D, endPosY, outlineColor);
                            Gui.drawRect(posX - 1D, endPosY, endPosX + 0.5D, endPosY + 0.5D, outlineColor);
                        }

                        if (entity instanceof EntityLivingBase) {
                            EntityLivingBase livingEntity = (EntityLivingBase) entity;
                            if (espComponentsProperty.isSelected(ESPComponents.HEALTH)) {
                                int healthBarColor = healthBarModeProperty.getPropertyValue().equals(HealthBarMode.STATIC) ?
                                        solidHealthBarProperty.getPropertyValueRGB() : ColorUtil.getHealthColor(livingEntity, 255).getRGB();

                                Gui.drawRect(posX - 3.5D, posY, posX - 1.5D, endPosY, 0x80000000);

                                if (healthBarModeProperty.getPropertyValue().equals(HealthBarMode.HEALTH_BASED) || healthBarModeProperty.getPropertyValue().equals(HealthBarMode.STATIC)) {
                                    Gui.drawRect(posX - 3D, endPosY + 0.5D - ((endPosY - posY) * (livingEntity.getHealth() / livingEntity.getMaxHealth())), posX - 2D, endPosY - 0.5D, healthBarColor);
                                }
                                else {
                                    GL11.glEnable(GL11.GL_SCISSOR_TEST);
                                    GLUtil.glScissor(posX - 3D, endPosY - ((endPosY - 0.5D) - (posY)) *
                                            livingEntity.getHealth() / livingEntity.getMaxHealth(), 1, (endPosY - 0.5D) - (posY));
                                    Gui.drawGradientRect(posX - 3D, posY + 0.5D, posX - 2D, endPosY - 0.5D,
                                            gradientBarStartProperty.getPropertyValueRGB(), gradientBarEndProperty.getPropertyValueRGB());
                                    GL11.glDisable(GL11.GL_SCISSOR_TEST);
                                }

                                final String status = livingEntity.getHealth() >= 10 ? "\247aHealthy" : "\2474Weak";

                                GLUtil.drawFilledRectangle(endPosX + 3, posY, AllureClient.getInstance().getFontManager().csgoFontRenderer.getStringWidth(status) + 3, 9, 0x90000000);
                                AllureClient.getInstance().getFontManager().csgoFontRenderer.drawStringWithShadow(status, endPosX + 5, posY + 2, -1);

                                GLUtil.glColor(0xFFFFFFFF);
                            }
                            if (espComponentsProperty.isSelected(ESPComponents.ARMOR)) {
                                // TODO: armor bar implementation
                            }
                        }
                    }
                });
                GL11.glPopMatrix();
                GL11.glEnable(GL11.GL_BLEND);
                mc.entityRenderer.setupOverlayRendering();
            });
        });
    }

    private Vector3f project2D(int scaleFactor, double x, double y, double z) {
        GL11.glGetFloat(2982, modelView);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        return GLU.gluProject((float)x, (float)y, (float)z, modelView, projection, viewport, vector) ? new Vector3f((vector.get(0) / (float)scaleFactor), (((float) Display.getHeight() - vector.get(1)) / (float)scaleFactor), vector.get(2)) : null;
    }

    private void updateEntities(){
        listedEntities.clear();
        mc.theWorld.loadedEntityList.forEach(entity -> {
            final boolean antiBot = AntiBot.getInstance().isToggled() &&
                    AntiBot.getInstance().antiBotModeProperty.getPropertyValue().equals(AntiBot.AntiBotMode.WATCHDOG)
                    || AntiBot.getInstance().antiBotModeProperty.getPropertyValue().equals(AntiBot.AntiBotMode.ADVANCED)
                    && entity instanceof EntityPlayer;
            if (entity == mc.thePlayer && mc.gameSettings.thirdPersonView == 0 ||
                    !(entity instanceof EntityPlayer) ||
                    (antiBot && !NetworkUtil.isPlayerPingValid((EntityPlayer) entity)))
                return;
            listedEntities.add(entity);
        });
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private enum HealthBarMode {
        HEALTH_BASED("Health Based"),
        STATIC("Static"),
        GRADIENT("Gradient");

        private final String name;

        HealthBarMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private enum ESPComponents {
        BOX("Box"),
        HEALTH("Health"),
        ARMOR("Armor");

        private final String name;

        ESPComponents(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
