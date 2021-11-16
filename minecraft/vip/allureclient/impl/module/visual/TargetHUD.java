package vip.allureclient.impl.module.visual;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.math.MathUtil;
import vip.allureclient.base.util.visual.ColorUtil;
import vip.allureclient.base.util.visual.RenderUtil;
import vip.allureclient.impl.event.visual.Render2DEvent;
import vip.allureclient.impl.module.combat.KillAura;
import vip.allureclient.impl.property.EnumProperty;

@ModuleData(moduleName = "TargetHUD", keyBind = 0, category = ModuleCategory.VISUAL)
public class TargetHUD extends Module {

    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;

    private final EnumProperty<BarColorModes> barColorModeProperty = new EnumProperty<>("Bar Color", BarColorModes.Health, this);

    public double xLocation = new ScaledResolution(Wrapper.getMinecraft()).getScaledWidth()/2.0D, yLocation = new ScaledResolution(Wrapper.getMinecraft()).getScaledHeight()/2.0D;

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
        RenderUtil.glFilledQuad(5, 7, 125, 50, 0x99000000);
        Gui.drawRectWithWidth(4, 7, 1, 49, 0x99505050);
        Gui.drawRectWithWidth(130, 7, 1, 49, 0x99505050);
        Gui.drawRectWithWidth(4, 6, 127, 1, 0x99505050);
        Gui.drawRectWithWidth(4, 56, 127, 1, 0x99505050);
        final double currentHealth = entity.getHealth();
        final double maxHealth = entity.getMaxHealth();
        final double percent = currentHealth / maxHealth;
        final double barWidth = percent * 125;
        animationValue = MathUtil.animateDoubleValue(barWidth, animationValue, 0.03D);
        if(barColorModeProperty.getPropertyValue().equals(BarColorModes.Health))
            Gui.drawRectWithWidth(5, 7, animationValue, 49, ColorUtil.getHealthColor(entity));
        else
            Gui.drawHorizontalGradient(5, 7, (float) animationValue, 49, 0x6042cef5, 0xffd742f5);
        Wrapper.getMinecraft().fontRendererObj.drawStringWithShadow(entity.getName(), 10, 13, -1);
        final double healthAsPercentage = (MathUtil.roundToPlace(100 * (entity.getHealth() / entity.getMaxHealth()), 2));
        Wrapper.getMinecraft().fontRendererObj.drawStringWithShadow("Health: " + healthAsPercentage + "%", 10, 27, -1);
        Wrapper.getMinecraft().fontRendererObj.drawStringWithShadow("Dist: " + MathUtil.roundToPlace(entity.getDistanceToEntity(Wrapper.getPlayer()), 2), 10, 41, -1);
        GlStateManager.color(1,1, 1);
        GL11.glPopMatrix();
    }

    private enum BarColorModes {
        Health,
        Gradient
    }
}
