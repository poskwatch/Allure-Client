package vip.allureclient.impl.module.visual;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.visual.*;
import vip.allureclient.impl.event.events.player.PlayerHurtSoundEvent;
import vip.allureclient.impl.event.events.visual.Render2DEvent;
import vip.allureclient.impl.module.combat.KillAura;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;

public class HitMarkers extends Module {

    private double currentAlpha = 255;

    public HitMarkers() {
        super("Hit Markers", ModuleCategory.VISUAL);
        this.setListener(new IEventListener() {
            @EventHandler(events = Render2DEvent.class, priority = Priority.LOW)
            final IEventCallable<Render2DEvent> onRender2D = (event -> {
                currentAlpha = AnimationUtil.linearAnimation(0, currentAlpha, 2);
                for (int i = 0; i < 4; i++) {
                    drawHitMarker(event.getScaledResolution());
                }
            });
            @EventHandler(events = PlayerHurtSoundEvent.class, priority = Priority.LOW)
            final IEventCallable<PlayerHurtSoundEvent> onPlayerHurtSound = (event -> {
                if (KillAura.getInstance().getCurrentTarget() != null && event.getEntityLivingBase().equals(KillAura.getInstance().getCurrentTarget())) {
                    SoundUtil.playSound("hitmarker.wav");
                    currentAlpha = 255;
                }
            });
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

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
