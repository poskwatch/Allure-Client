package vip.allureclient.impl.module.visual;

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
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.NetworkUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.ColorUtil;
import vip.allureclient.impl.event.visual.Render2DEvent;
import vip.allureclient.impl.module.combat.AntiBot;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.ColorProperty;
import vip.allureclient.impl.property.EnumProperty;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ModuleData(moduleName = "Player ESP", moduleBind = 0, moduleCategory = ModuleCategory.VISUAL)
public class PlayerESP extends Module {

    public final ArrayList<Entity> listedEntities = new ArrayList<>();
    private final IntBuffer viewport;
    private final FloatBuffer modelView;
    private final FloatBuffer projection;
    private final FloatBuffer vector;

    private final BooleanProperty boxProperty = new BooleanProperty("Box", true, this);
    private final ColorProperty boxColorProperty = new ColorProperty("Box Color", Color.WHITE, this) {
        @Override
        public boolean isPropertyHidden() {
            return !boxProperty.getPropertyValue();
        }
    };

    private final BooleanProperty healthBarProperty = new BooleanProperty("Health Bar", true, this);
    private final EnumProperty<HealthBarMode> healthBarModeProperty = new EnumProperty<HealthBarMode>("Bar Mode", HealthBarMode.Health, this) {
        @Override
        public boolean isPropertyHidden() {
            return !healthBarProperty.getPropertyValue();
        }
    };
    private final ColorProperty solidHealthBarProperty = new ColorProperty("Health Bar Color", Color.MAGENTA, this) {
        @Override
        public boolean isPropertyHidden() {
            return !healthBarProperty.getPropertyValue() && !healthBarModeProperty.getPropertyValue().equals(HealthBarMode.Solid);
        }
    };

    @EventListener
    EventConsumer<Render2DEvent> onRender2D;

    public PlayerESP() {
        viewport = GLAllocation.createDirectIntBuffer(16);
        modelView = GLAllocation.createDirectFloatBuffer(16);
        projection = GLAllocation.createDirectFloatBuffer(16);
        vector = GLAllocation.createDirectFloatBuffer(4);
        onRender2D = (event -> {
            updateEntities();
            GL11.glPushMatrix();
            //double scaling = event.getScaledResolution().getScaleFactor() / Math.pow(event.getScaledResolution().getScaleFactor(), 2.0D);
            listedEntities.forEach(entity -> {
                double x = interpolateScale(entity.posX, entity.lastTickPosX, event.getPartialTicks());
                double y = interpolateScale(entity.posY, entity.lastTickPosY, event.getPartialTicks());
                double z = interpolateScale(entity.posZ, entity.lastTickPosZ, event.getPartialTicks());
                double width = entity.width / 1.5D;
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
                Wrapper.getMinecraft().entityRenderer.setupCameraTransform(event.getPartialTicks(), 0);

                Vector4f position = null;

                for (Vector3f vector : vectors) {
                    vector = project2D(event.getScaledResolution().getScaleFactor(),
                            vector.x - Wrapper.getMinecraft().getRenderManager().viewerPosX,
                            vector.y - Wrapper.getMinecraft().getRenderManager().viewerPosY,
                            vector.z - Wrapper.getMinecraft().getRenderManager().viewerPosZ);
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

                if(position != null){
                    Wrapper.getMinecraft().entityRenderer.setupOverlayRendering();
                    double posX = position.x;
                    double posY = position.y;
                    double endPosX = position.z;
                    double endPosY = position.w;

                    if (boxProperty.getPropertyValue()) {
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
                        if (healthBarProperty.getPropertyValue()) {
                            int healthBarColor = healthBarModeProperty.getPropertyValue().equals(HealthBarMode.Solid) ?
                                    solidHealthBarProperty.getPropertyValueRGB() : ColorUtil.getHealthColor(livingEntity, 255).getRGB();

                            Gui.drawRect(posX - 3.5D, posY, posX - 1.5D, endPosY, 0x80000000);

                            if (healthBarModeProperty.getPropertyValue().equals(HealthBarMode.Health) || healthBarModeProperty.getPropertyValue().equals(HealthBarMode.Solid)) {
                                Gui.drawRect(posX - 3D, endPosY + 0.5D - ((endPosY - posY) * (livingEntity.getHealth() / livingEntity.getMaxHealth())), posX - 2D, endPosY - 0.5D, healthBarColor);
                            }
                        }
                    }
                }
            });
            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            Wrapper.getMinecraft().entityRenderer.setupOverlayRendering();
        });
    }

    private Vector3f project2D(int scaleFactor, double x, double y, double z) {
        GL11.glGetFloat(2982, modelView);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        return GLU.gluProject((float)x, (float)y, (float)z, modelView, projection, viewport, vector) ? new Vector3f((vector.get(0) / (float)scaleFactor), (((float) Display.getHeight() - vector.get(1)) / (float)scaleFactor), vector.get(2)) : null;
    }

    private double interpolateScale(double current, double old, double scale) {
        return old + (current - old) * scale;
    }

    private void updateEntities(){
        listedEntities.clear();
        Wrapper.getWorld().loadedEntityList.forEach(entity -> {
            final boolean antiBot = AntiBot.getInstance().isModuleToggled() &&
                    AntiBot.getInstance().antiBotModeProperty.getPropertyValue().equals(AntiBot.AntiBotMode.Watchdog)
                    || AntiBot.getInstance().antiBotModeProperty.getPropertyValue().equals(AntiBot.AntiBotMode.Advanced)
                    && entity instanceof EntityPlayer;
            if(entity == Wrapper.getPlayer() && Wrapper.getMinecraft().gameSettings.thirdPersonView == 0 || !(entity instanceof EntityPlayer) || (antiBot && !NetworkUtil.isPlayerPingNull((EntityPlayer) entity))){
                return;
            }
            listedEntities.add(entity);
        });
    }

    private enum HealthBarMode {
        Health,
        Solid,
        Gradient
    }
}
