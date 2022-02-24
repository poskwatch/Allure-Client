package vip.allureclient.impl.module.visual.hud;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import vip.allureclient.AllureClient;
import vip.allureclient.base.font.MinecraftFontRenderer;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.NetworkUtil;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.base.util.visual.*;
import vip.allureclient.base.util.visual.glsl.GLUtil;
import vip.allureclient.impl.event.events.visual.Render2DEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.ColorProperty;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.impl.property.ValueProperty;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class HUD extends Module {

    private final BooleanProperty vanillaFontProperty = new BooleanProperty("Vanilla Font", false, this);

    private final EnumProperty<ListColorMode> listColorProperty = new EnumProperty<>("List Color", ListColorMode.CLIENT, this);

    private final ColorProperty gradientStartColorProperty = new ColorProperty("Gradient Start", Color.RED, this) {
        @Override
        public boolean isPropertyHidden() {
            return !listColorProperty.getPropertyValue().equals(ListColorMode.GRADIENT);
        }
    };

    private final ColorProperty gradientsEndColorProperty = new ColorProperty("Gradient End", Color.BLUE, this) {
        @Override
        public boolean isPropertyHidden() {
            return !listColorProperty.getPropertyValue().equals(ListColorMode.GRADIENT);
        }
    };

    private final ValueProperty<Double> gradientSpeedProperty = new ValueProperty<Double>("Gradient Seconds", 3D, 1D, 10D, this) {
        @Override
        public boolean isPropertyHidden() {
            return !listColorProperty.getPropertyValue().equals(ListColorMode.GRADIENT);
        }
    };

    private final ValueProperty<Integer> gradientOffsetProperty = new ValueProperty<Integer>("Gradient Offset", 10, 1, 50, this) {
        @Override
        public boolean isPropertyHidden() {
            return !listColorProperty.getPropertyValue().equals(ListColorMode.GRADIENT);
        }
    };

    private final ColorProperty staticColorProperty = new ColorProperty("Color", Color.MAGENTA, this) {
        @Override
        public boolean isPropertyHidden() {
            return !listColorProperty.getPropertyValue().equals(ListColorMode.SOLID);
        }
    };

    private final BooleanProperty blurBackgroundProperty = new BooleanProperty("Blur Background", false, this);

    private final ValueProperty<Integer> backgroundOpacityProperty = new ValueProperty<>("Background Alpha", 127, 0, 255, this);

    private final EnumProperty<ListOutlineMode> listOutlineModeProperty = new EnumProperty<>("List Outline", ListOutlineMode.RIGHT, this);

    private final EnumProperty<WatermarkModes> waterMarkModeProperty = new EnumProperty<>("Watermark", WatermarkModes.NEW, this);

    private final BooleanProperty playerInfoProperty = new BooleanProperty("Player Info", true, this);

    public HUD() {
        super("HUD", ModuleCategory.VISUAL);
        this.setListener(new IEventListener() {
            @EventHandler(events = Render2DEvent.class, priority = Priority.VERY_HIGH)
            final IEventCallable<Render2DEvent> onRender2D = (event -> {

                final int height = event.getScaledResolution().getScaledHeight(), width = event.getScaledResolution().getScaledWidth();

                final MinecraftFontRenderer fontRenderer = AllureClient.getInstance().getFontManager().mediumFontRenderer;

                AllureClient.getInstance().getNotificationManager().render(event.getScaledResolution(), 5);

                String watermark = String.format("\247r%s\247f%s v%s",
                        AllureClient.getInstance().CLIENT_NAME.charAt(0),
                        AllureClient.getInstance().CLIENT_NAME.substring(1), AllureClient.getInstance().getVersion().toString());

                if (waterMarkModeProperty.getPropertyValue().equals(WatermarkModes.NEW) || waterMarkModeProperty.getPropertyValue().equals(WatermarkModes.SOLID)) {
                    drawStringWithShadow(watermark, 6, 6, -1);
                }
                else {
                    final String csgoWatermark = String.format("allureclient.vip \2477|\247f FPS: %s \2477|\247f %s", Minecraft.getDebugFPS(),
                            mc.getCurrentServerData() == null ? "Singleplayer" : mc.getCurrentServerData().serverIP);
                    RenderUtil.drawBorderedRect(5, 5, AllureClient.getInstance().getFontManager().csgoFontRenderer.getStringWidth(csgoWatermark) + 5,
                            13, 0x90000000, 0x20ffffff);
                    GLUtil.glHorizontalGradientQuad(5, 5, (float) (AllureClient.getInstance().getFontManager().csgoFontRenderer.getStringWidth(csgoWatermark) + 5), 1,
                            ColorUtil.interpolateColorsDynamic(7, 1, Color.MAGENTA, Color.BLUE).getRGB(),
                            ColorUtil.interpolateColorsDynamic(7, 1, Color.BLUE, Color.MAGENTA).getRGB());
                    AllureClient.getInstance().getFontManager().csgoFontRenderer.drawStringWithShadow(csgoWatermark, 7, 10, -1);
                }

                if (playerInfoProperty.getPropertyValue()) {
                    fontRenderer.drawStringWithShadow("Ping: \2477" + NetworkUtil.getPing(), 1, height - 29, -1);
                    fontRenderer.drawStringWithShadow("Block/s: \2477" + MovementUtil.getPlayerBPS(), 1, height - 19, -1);
                    fontRenderer.drawStringWithShadow(String.format("%.1f, %.1f, %.1f", mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), 1, height - 9, -1);
                }

                final ArrayList<Module> sortedDisplayModules = new ArrayList<>(AllureClient.getInstance().getModuleManager().getModulesAsArraylist());
                sortedDisplayModules.sort(Comparator.comparingDouble(module -> getStringWidth(((Module) module).getModuleDisplayName())).reversed());

                final AtomicInteger moduleDrawCount = new AtomicInteger();
                sortedDisplayModules.forEach(module -> {

                    double targetPosX = event.getScaledResolution().getScaledWidth() - getStringWidth(module.getModuleDisplayName()) - 3;
                    double targetPosY = 4 + moduleDrawCount.get() * 13;

                    module.getAnimatedCoordinate().animateCoordinates(targetPosX, targetPosY, AnimatedCoordinate.AnimationType.EASE_OUT);

                    double posX = module.getAnimatedCoordinate().getX();
                    double posY = module.getAnimatedCoordinate().getY();

                    if (!module.isVisible()) {
                        module.getAnimatedCoordinate().setX(width);
                        return;
                    }

                    int listColor;
                    switch (listColorProperty.getPropertyValue()) {
                        case CLIENT:
                            listColor = ColorUtil.interpolateColorsDynamic(5, moduleDrawCount.get() * 25, ColorUtil.getClientColors()[0], ColorUtil.getClientColors()[1]).getRGB();
                            break;
                        case GRADIENT:
                            listColor = ColorUtil.interpolateColorsDynamic(gradientSpeedProperty.getPropertyValue(), gradientOffsetProperty.getPropertyValue() * moduleDrawCount.get(),
                                    gradientStartColorProperty.getPropertyValue(), gradientsEndColorProperty.getPropertyValue()).getRGB();
                            break;
                        case RAINBOW:
                            listColor = ColorUtil.getRainbowColor(2F, moduleDrawCount.get() * 48, 0.7f);
                            break;
                        default:
                            listColor = staticColorProperty.getPropertyValueRGB();
                            break;
                    }
                    if (blurBackgroundProperty.getPropertyValue())
                        BlurUtil.blurArea((float) posX - 2, (float) posY - 4,
                                getStringWidth(module.getModuleDisplayName()) + 7, 13);

                    GLUtil.drawFilledRectangle((float) posX - 2, (float) posY - 3,
                            getStringWidth(module.getModuleDisplayName()) + 7, 13, new Color(0, 0, 0, backgroundOpacityProperty.getPropertyValue()).getRGB());

                    drawStringWithShadow(module.getModuleDisplayName(), (float) posX, (float) posY - 0.5F, listColor);

                    switch (listOutlineModeProperty.getPropertyValue()) {
                        case LEFT:
                            GlStateManager.color(1, 1, 1);
                            GLUtil.drawFilledRectangle((float) (posX - 3), (float) (posY - 4), 1, 13, listColor);
                            break;
                        case RIGHT:
                            GlStateManager.color(1, 1, 1);
                            GLUtil.drawFilledRectangle(event.getScaledResolution().getScaledWidth() - 1, (float) (posY - 4), 1, 13, listColor);
                            break;
                        case OUTLINE:

                            break;
                    }
                    moduleDrawCount.getAndIncrement();
                });
            });
        });

        setToggled(true);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void drawStringWithShadow(String text, float x, float y, int color) {
        if (vanillaFontProperty.getPropertyValue())
            mc.fontRendererObj.drawStringWithShadow(text, x, y, color);
        else
            AllureClient.getInstance().getFontManager().mediumFontRenderer.drawStringWithShadow(text, x, y, color);
    }

    private float getStringWidth(String text) {
        if (vanillaFontProperty.getPropertyValue())
            return mc.fontRendererObj.getStringWidth(text) - 2;
        else
            return (float) AllureClient.getInstance().getFontManager().mediumFontRenderer.getStringWidth(text);
    }

    private enum ListColorMode {
        CLIENT("Client"),
        GRADIENT("Gradient"),
        SOLID("Solid"),
        RAINBOW("Rainbow");

        private final String name;

        ListColorMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum ListOutlineMode {
        LEFT("Left"),
        RIGHT("Right"),
        OUTLINE("Outline"),
        NONE("None");

        private final String name;

        ListOutlineMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private enum WatermarkModes {
        SOLID("Solid"),
        NEW("New"),
        CSGO("CSGO");

        private final String name;

        WatermarkModes(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static HUD getInstance() {
        return (HUD) AllureClient.getInstance().getModuleManager().getModuleOrNull("HUD");
    }


}
