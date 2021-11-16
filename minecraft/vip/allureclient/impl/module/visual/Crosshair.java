package vip.allureclient.impl.module.visual;

import net.minecraft.client.gui.Gui;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.impl.event.visual.Render2DEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.ValueProperty;

@ModuleData(moduleName = "Crosshair", keyBind = 0, category = ModuleCategory.VISUAL)
public class Crosshair extends Module {

    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;

    private final ValueProperty<Double> gapProperty = new ValueProperty<>("Gap", 3.0D, 0.0D, 20.0D, this);

    private final ValueProperty<Double> widthProperty = new ValueProperty<>("Width", 4.7D, 0.0D, 20.0D, this);

    private final ValueProperty<Double> heightProperty = new ValueProperty<>("Height", 0.7D, 0.0D, 20.0D, this);

    private final BooleanProperty tShapeProperty = new BooleanProperty("T Shape", true, this);

    private final BooleanProperty centerDotProperty = new BooleanProperty("Center Dot", true, this);

    private final BooleanProperty dynamicProperty = new BooleanProperty("Dynamic", true, this);

    public Crosshair() {
        this.onRender2DEvent = (render2DEvent -> {
            final double centerX = render2DEvent.getScaledResolution().getScaledWidth() / 2.0D;
            final double centerY = render2DEvent.getScaledResolution().getScaledHeight() / 2.0D;

            double gap = gapProperty.getPropertyValue();
            final double width = widthProperty.getPropertyValue();
            final double height = heightProperty.getPropertyValue();

            final int crosshairColor = ClientColor.getInstance().getColorRGB();

            if (dynamicProperty.getPropertyValue()) {
                if (Wrapper.getPlayer().isSneaking())
                    gap *= 0.5D;
                if(MovementUtil.isMoving())
                    gap *= 2.0D;
            }

            if (centerDotProperty.getPropertyValue()) {
                Gui.drawRect(centerX - 1D, centerY - 1D, centerX + 1D, centerY + 1D, 0xff000000);
                Gui.drawRect(centerX - 0.5D, centerY - 0.5D, centerX + 0.5D, centerY + 0.5D, crosshairColor);
            }

            Gui.drawRect(centerX - gap - width - 0.5D, centerY - (height/2) - 0.5D, centerX - gap + 0.5D, centerY + (height/2) + 0.5D, 0xff000000);
            Gui.drawRect(centerX - gap - width, centerY - (height/2), centerX - gap, centerY + (height/2), crosshairColor);

            Gui.drawRect(centerX + gap - 0.5D, centerY - (height/2) - 0.5D, centerX + gap + width + 0.5D, centerY + (height/2) + 0.5D, 0xff000000);
            Gui.drawRect(centerX + gap, centerY - (height/2), centerX + gap + width, centerY + (height/2), crosshairColor);

            Gui.drawRect(centerX - (height / 2.0D) - 0.5D, centerY + gap - 0.5D, centerX + (height / 2.0D) + 0.5D, centerY + gap + width + 0.5D, 0xff000000);
            Gui.drawRect(centerX - (height / 2.0D), centerY + gap, centerX + (height / 2.0D), centerY + gap + width, crosshairColor);

            if (!tShapeProperty.getPropertyValue()) {
                Gui.drawRect(centerX - (height / 2.0D) - 0.5D, centerY - gap - width - 0.5D, centerX + (height / 2.0D) + 0.5D, centerY - gap + 0.5D, 0xff000000);
                Gui.drawRect(centerX - (height / 2.0D), centerY - gap - width, centerX + (height / 2.0D), centerY - gap, crosshairColor);
            }
        });
    }

}
