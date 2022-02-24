package vip.allureclient.impl.module.visual;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.visual.AnimationUtil;
import vip.allureclient.base.util.visual.ColorUtil;
import vip.allureclient.base.util.visual.glsl.GLUtil;
import vip.allureclient.impl.event.events.visual.Render2DEvent;
import vip.allureclient.impl.module.combat.KillAura;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.ValueProperty;

import javax.swing.*;

import static org.lwjgl.opengl.GL11.*;

public class TargetHUD extends Module {

    public final ScaledResolution scaledResolution = new ScaledResolution(mc);

    // X Position property
    public final ValueProperty<Double> xCoordinateProperty = new ValueProperty<>("X Position",
            scaledResolution.getScaledWidth_double(), 0.0D, 1080.0D, this);

    // Y Position property
    public final ValueProperty<Double> yCoordinateProperty = new ValueProperty<>("X Position",
            scaledResolution.getScaledHeight_double(), 0.0D, 1080.0D, this);

    private final BooleanProperty animateBarProperty = new BooleanProperty("Animate bar", true, this);

    public TargetHUD() {
        super("Target HUD", ModuleCategory.VISUAL);
        this.setListener(new IEventListener() {
            @EventHandler(events = Render2DEvent.class, priority = Priority.VERY_HIGH)
            final IEventCallable<Render2DEvent> onRender2D = (event -> {
                if (KillAura.getInstance().getCurrentTarget() != null) {
                    drawTargetHUD((EntityLivingBase) KillAura.getInstance().getCurrentTarget());
                }
                else if (mc.currentScreen instanceof GuiChat) {
                    drawTargetHUD(mc.thePlayer);
                }
            });
        });
    }

    private double animationValue;

    public void drawTargetHUD(EntityLivingBase entity) {
        GL11.glPushMatrix();
        GL11.glTranslated(xCoordinateProperty.getPropertyValue(), yCoordinateProperty.getPropertyValue(), 0);
        GLUtil.glOutlinedFilledQuad(5, 7, 127, 32, 0x90000000, 0xff101010);
        if (entity instanceof EntityPlayer) {
            AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer) entity;
            mc.getTextureManager().bindTexture(abstractClientPlayer.getLocationSkin());
            GlStateManager.color(1, 1, 1);
            glBegin(GL_QUADS);
            glTexCoord2f(1.0F/8, 1.0F/8);
            glVertex2i(5, 7);
            glTexCoord2f(1.0F/8, 1.0F/8 * 2);
            glVertex2i(5, 39);
            glTexCoord2f(1.0F/8 * 2, 1.0F/8 * 2);
            glVertex2i(5 + 32, 39);
            glTexCoord2f(1.0F/8 * 2, 1.0F/8);
            glVertex2i(5 + 32, 7);
            glEnd();
        }
        else {
            Gui.drawRectWithWidth(5, 7, 32, 32, 0xff000000);
            mc.fontRendererObj.drawStringWithShadow("?", 18F, 19.5F, -1);
        }
        mc.fontRendererObj.drawStringWithShadow(entity.getName(), 40, 12, -1);
        final double currentHealth = entity.getHealth();
        final double maxHealth = entity.getMaxHealth();
        final double percent = Math.min((currentHealth / maxHealth), 1);
        animationValue = AnimationUtil.linearAnimation(87 * percent, animationValue, 0.7);
        Gui.drawRectWithWidth(39, 24, 89, 12, 0x70101010);
        Gui.drawRectWithWidth(40, 25, 87, 10, ColorUtil.getHealthColor(entity, 127).darker().getRGB());
        Gui.drawRectWithWidth(40, 25, animateBarProperty.getPropertyValue() ? animationValue : percent * 87, 10, ColorUtil.getHealthColor(entity, 255).getRGB());
        String percentage = String.format("%.1f%%", 100 * (currentHealth/maxHealth));
        double percentageX = (38 + (animateBarProperty.getPropertyValue() ? animationValue : percent * 87)) - mc.fontRendererObj.getStringWidth(percentage);
        mc.fontRendererObj.drawStringWithShadow(percentage, Math.round(Math.max(percentageX, 43.0)), 26.5F, -1);
        GlStateManager.color(1,1, 1);
        GL11.glPopMatrix();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static TargetHUD getInstance() {
        return (TargetHUD) AllureClient.getInstance().getModuleManager().getModuleOrNull("Target HUD");
    }
}
