package vip.allureclient.impl.module.visual;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.AnimationUtil;
import vip.allureclient.base.util.visual.ColorUtil;
import vip.allureclient.base.util.visual.GLUtil;
import vip.allureclient.impl.event.visual.Render2DEvent;
import vip.allureclient.impl.module.combat.KillAura;
import vip.allureclient.impl.property.BooleanProperty;

import static org.lwjgl.opengl.GL11.*;

@ModuleData(moduleName = "Target HUD", moduleBind = 0, moduleCategory = ModuleCategory.VISUAL)
public class TargetHUD extends Module {

    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;

    public double xLocation = new ScaledResolution(Wrapper.getMinecraft()).getScaledWidth()/2.0D, yLocation = new ScaledResolution(Wrapper.getMinecraft()).getScaledHeight()/2.0D;

    private final BooleanProperty animateBarProperty = new BooleanProperty("Animate bar", true, this);

    public TargetHUD() {
        this.onRender2DEvent = (render2DEvent -> {
            if (KillAura.getInstance().getCurrentTarget() != null) {
                drawTargetHUD((EntityLivingBase) KillAura.getInstance().getCurrentTarget());
            }
            if (Wrapper.getMinecraft().currentScreen instanceof GuiChat) {
                drawTargetHUD(Wrapper.getPlayer());
            }
        });
    }

    private double animationValue;

    public void drawTargetHUD(EntityLivingBase entity) {
        GL11.glPushMatrix();
        GL11.glTranslated(xLocation, yLocation, 0);
        GLUtil.glOutlinedFilledQuad(5, 7, 127, 32, 0x90000000, 0xff101010);
        if (entity instanceof EntityPlayer) {
            AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer) entity;
            Wrapper.getMinecraft().getTextureManager().bindTexture(abstractClientPlayer.getLocationSkin());
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
            Wrapper.getMinecraft().fontRendererObj.drawStringWithShadow("?", 18F, 19.5F, -1);
        }
        Wrapper.getMinecraftFontRenderer().drawStringWithShadow(entity.getName(), 40, 12, -1);
        final double currentHealth = entity.getHealth();
        final double maxHealth = entity.getMaxHealth();
        final double percent = Math.min((currentHealth / maxHealth), 1);
        animationValue = AnimationUtil.linearAnimation(87 * percent, animationValue, 0.7);
        Gui.drawRectWithWidth(39, 24, 89, 12, 0x70101010);
        Gui.drawRectWithWidth(40, 25, 87, 10, ColorUtil.getHealthColor(entity, 127).darker().getRGB());
        Gui.drawRectWithWidth(40, 25, animateBarProperty.getPropertyValue() ? animationValue : percent * 87, 10, ColorUtil.getHealthColor(entity, 255).getRGB());
        String percentage = String.format("%.1f%%", 100 * (currentHealth/maxHealth));
        double percentageX = (38 + (animateBarProperty.getPropertyValue() ? animationValue : percent * 87)) - Wrapper.getMinecraft().fontRendererObj.getStringWidth(percentage);
        Wrapper.getMinecraftFontRenderer().drawStringWithShadow(percentage, Math.round(Math.max(percentageX, 43.0)), 26.5F, -1);
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
        return (TargetHUD) AllureClient.getInstance().getModuleManager().getModuleByClass.apply(TargetHUD.class);
    }
}
