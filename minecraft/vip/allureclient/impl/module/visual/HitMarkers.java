package vip.allureclient.impl.module.visual;

import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.visual.*;
import vip.allureclient.impl.event.player.PlayerHurtSoundEvent;
import vip.allureclient.impl.event.visual.Render2DEvent;
import vip.allureclient.impl.module.combat.killaura.KillAura;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;

@ModuleData(moduleName = "Hit Markers", moduleBind = 0, moduleCategory = ModuleCategory.VISUAL)
public class HitMarkers extends Module {

    private double currentAlpha = 255;

    @EventListener
    EventConsumer<PlayerHurtSoundEvent> onPlayerHurtSoundEvent;

    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;

    public HitMarkers() {
        this.onPlayerHurtSoundEvent = (event -> {
            if (KillAura.getInstance().getCurrentTarget() != null && event.getEntityLivingBase().equals(KillAura.getInstance().getCurrentTarget())) {
                SoundUtil.playSound("hitmarker.wav");
                currentAlpha = 255;
            }
        });
        this.onRender2DEvent = (event -> {
            currentAlpha = AnimationUtil.linearAnimation(0, currentAlpha, 2);
            for (int i = 0; i < 4; i++) {
                drawHitMarker(event.getScaledResolution());
            }
        });
    }

    private void drawHitMarker(ScaledResolution scaledResolution) {
        glDisable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(2.0F);
        glColor4f(1f, 0.3f, 0.3f, (float) currentAlpha / 255.0F);
        glBegin(GL11.GL_LINES);
        glVertex2d(scaledResolution.getScaledWidth()/2.0 - 7, scaledResolution.getScaledHeight()/2.0 - 7);
        glVertex2d(scaledResolution.getScaledWidth()/2.0 - 4, scaledResolution.getScaledHeight()/2.0 - 4);

        glVertex2d(scaledResolution.getScaledWidth()/2.0 + 7, scaledResolution.getScaledHeight()/2.0 + 7);
        glVertex2d(scaledResolution.getScaledWidth()/2.0 + 4, scaledResolution.getScaledHeight()/2.0 + 4);

        glVertex2d(scaledResolution.getScaledWidth()/2.0 - 7, scaledResolution.getScaledHeight()/2.0 + 7);
        glVertex2d(scaledResolution.getScaledWidth()/2.0 - 4, scaledResolution.getScaledHeight()/2.0 + 4);

        glVertex2d(scaledResolution.getScaledWidth()/2.0 + 7, scaledResolution.getScaledHeight()/2.0 - 7);
        glVertex2d(scaledResolution.getScaledWidth()/2.0 + 4, scaledResolution.getScaledHeight()/2.0 - 4);
        glEnd();
        glEnable(GL_TEXTURE_2D);
    }
}
