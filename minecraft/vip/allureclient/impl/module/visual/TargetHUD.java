package vip.allureclient.impl.module.visual;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.math.MathUtil;
import vip.allureclient.base.util.visual.RenderUtil;
import vip.allureclient.impl.event.visual.Render2DEvent;
import vip.allureclient.impl.module.combat.KillAura;
import vip.allureclient.impl.property.BooleanProperty;

@ModuleData(moduleName = "TargetHUD", keyBind = 0, category = ModuleCategory.VISUAL)
public class TargetHUD extends Module {

    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;

    private final BooleanProperty animateBarProperty = new BooleanProperty("Animate Bar", false, this);

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
        RenderUtil.glFilledQuad(5, 7, 125, 45, 0xff202020);
        Gui.drawRectWithWidth(4, 7, 1, 45, 0xff101010);
        Gui.drawRectWithWidth(130, 7, 1, 45, 0xff101010);
        Gui.drawRectWithWidth(4, 6, 127, 1, 0xff101010);
        Gui.drawRectWithWidth(4, 51, 127, 1, 0xff101010);
        final double currentHealth = entity.getHealth();
        final double maxHealth = entity.getMaxHealth();
        final double percent = Math.min((currentHealth / maxHealth), 1);
        double barWidth = percent * 113;
        animationValue = MathUtil.animateDoubleValue(barWidth, animationValue, 0.009D);
        if (animateBarProperty.getPropertyValue())
            barWidth = animationValue;
        Gui.drawRectWithWidth(10, 28, 115, 15, 0xff101010);
        Gui.drawRectWithWidth(11, 29, barWidth, 13, ClientColor.getInstance().getColor().darker().darker().getRGB());
        Gui.drawRectWithWidth(11, 29, percent * 113, 13, ClientColor.getInstance().getColor().darker().getRGB());
        Wrapper.getMinecraft().fontRendererObj.drawCenteredStringWithShadow(entity.getName(), 67.5F, 13.5F, -1);
        final double healthAsPercentage = Math.min(MathUtil.roundToPlace(100 * (entity.getHealth() / entity.getMaxHealth()), 2), 100.0D);
        Wrapper.getMinecraft().fontRendererObj.drawCenteredStringWithShadow(healthAsPercentage + "%", 67.5F, 32, -1);
        GlStateManager.color(1,1, 1);
        GL11.glPopMatrix();
    }

    public static TargetHUD getInstance() {
        return (TargetHUD) AllureClient.getInstance().getModuleManager().getModuleByClass.apply(TargetHUD.class);
    }
}
