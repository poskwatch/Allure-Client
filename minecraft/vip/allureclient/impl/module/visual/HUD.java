package vip.allureclient.impl.module.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.font.MinecraftFontRenderer;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.NetworkUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.base.util.visual.*;
import vip.allureclient.impl.event.visual.Render2DEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.ColorProperty;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.impl.property.ValueProperty;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@ModuleData(moduleName = "HUD", moduleBind = 0, moduleCategory = ModuleCategory.VISUAL)
public class HUD extends Module {

    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;

    private final BooleanProperty vanillaFontProperty = new BooleanProperty("Vanilla Font", false, this);

    public final EnumProperty<ListPositionModes> listPositionProperty = new EnumProperty<>("List Position", ListPositionModes.TOP, this);

    private final EnumProperty<ListColorModes> listColorProperty = new EnumProperty<>("List Color", ListColorModes.Client, this);

    private final ColorProperty gradientStartColorProperty = new ColorProperty("Gradient Start", Color.RED, this) {
        @Override
        public boolean isPropertyHidden() {
            return !listColorProperty.getPropertyValue().equals(ListColorModes.Gradient);
        }
    };

    private final ColorProperty gradientsEndColorProperty = new ColorProperty("Gradient End", Color.BLUE, this) {
        @Override
        public boolean isPropertyHidden() {
            return !listColorProperty.getPropertyValue().equals(ListColorModes.Gradient);
        }
    };

    private final ValueProperty<Double> gradientSpeedProperty = new ValueProperty<Double>("Gradient Seconds", 3D, 1D, 10D, this) {
        @Override
        public boolean isPropertyHidden() {
            return !listColorProperty.getPropertyValue().equals(ListColorModes.Gradient);
        }
    };

    private final ValueProperty<Integer> gradientOffsetProperty = new ValueProperty<Integer>("Gradient Offset", 10, 1, 50, this) {
        @Override
        public boolean isPropertyHidden() {
            return !listColorProperty.getPropertyValue().equals(ListColorModes.Gradient);
        }
    };

    private final ColorProperty staticColorProperty = new ColorProperty("Color", Color.MAGENTA, this) {
        @Override
        public boolean isPropertyHidden() {
            return !listColorProperty.getPropertyValue().equals(ListColorModes.Solid);
        }
    };

    private final BooleanProperty blurBackgroundProperty = new BooleanProperty("Blur Background", false, this);

    private final ValueProperty<Integer> backgroundOpacityProperty = new ValueProperty<>("Background Alpha", 127, 0, 255, this);

    private final EnumProperty<ListOutlineModes> listOutlineModeProperty = new EnumProperty<>("List Outline", ListOutlineModes.Right, this);

    private final EnumProperty<WatermarkModes> waterMarkModeProperty = new EnumProperty<>("Watermark", WatermarkModes.New, this);

    private final BooleanProperty playerInfoProperty = new BooleanProperty("Player Info", true, this);

    int notificationYOffset = 5;

    public HUD() {
        this.onRender2DEvent = (render2DEvent -> {

            final int height = render2DEvent.getScaledResolution().getScaledHeight(), width = render2DEvent.getScaledResolution().getScaledWidth();

            final MinecraftFontRenderer fontRenderer = AllureClient.getInstance().getFontManager().mediumFontRenderer;

            AllureClient.getInstance().getNotificationManager().render(render2DEvent.getScaledResolution(), notificationYOffset);

            String watermark = String.format("%s v%s", AllureClient.getInstance().CUSTOM_CLIENT_NAME, AllureClient.getInstance().CLIENT_VERSION);
            if (waterMarkModeProperty.getPropertyValue().equals(WatermarkModes.New))
                watermark = String.format("\247r%s\247f%s v%s", AllureClient.getInstance().CUSTOM_CLIENT_NAME.charAt(0), AllureClient.getInstance().CUSTOM_CLIENT_NAME.substring(1), AllureClient.getInstance().CLIENT_VERSION);

            if (waterMarkModeProperty.getPropertyValue().equals(WatermarkModes.New) || waterMarkModeProperty.getPropertyValue().equals(WatermarkModes.Solid)) {
                drawStringWithShadow(watermark, 6, 6, -1);
            }
            else {
                final String csgoWatermark = String.format("allureclient.vip \2477|\247f FPS: %s \2477|\247f %s", Minecraft.getDebugFPS(),
                        Wrapper.getMinecraft().getCurrentServerData() == null ? "Singleplayer" : Wrapper.getMinecraft().getCurrentServerData().serverIP);
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
                fontRenderer.drawStringWithShadow(String.format("%.1f, %.1f, %.1f", Wrapper.getPlayer().posX, Wrapper.getPlayer().posY, Wrapper.getPlayer().posZ), 1, height - 9, -1);
            }

            final ArrayList<Module> sortedDisplayModules = new ArrayList<>(AllureClient.getInstance().getModuleManager().getSortedDisplayModules(
                    (vanillaFontProperty.getPropertyValue()), true));
            final AtomicInteger moduleDrawCount = new AtomicInteger();
            sortedDisplayModules.forEach(module -> {

                double targetPosX = render2DEvent.getScaledResolution().getScaledWidth() - getStringWidth(module.getModuleDisplayName()) - 3;
                double targetPosY = 3 + moduleDrawCount.get() * 13;

                if (listPositionProperty.getPropertyValue().equals(ListPositionModes.BOTTOM)) {
                    targetPosY = height - 9 - moduleDrawCount.get() * 13;
                    notificationYOffset = 13 + moduleDrawCount.get() * 13;
                }
                else
                    notificationYOffset = 5;

                module.getAnimatedCoordinate().animateCoordinates(targetPosX, targetPosY, AnimatedCoordinate.AnimationType.EASE_OUT);

                double posX = module.getAnimatedCoordinate().getX();
                double posY = module.getAnimatedCoordinate().getY();

                if (!module.isVisible()) {
                    module.getAnimatedCoordinate().setX(width);
                    return;
                }

                int listColor;
                switch (listColorProperty.getPropertyValue()) {
                    case Client:
                        listColor = ColorUtil.interpolateColorsDynamic(5, moduleDrawCount.get() * 25, ColorUtil.getClientColors()[0], ColorUtil.getClientColors()[1]).getRGB();
                        break;
                    case Gradient:
                        listColor = ColorUtil.interpolateColorsDynamic(gradientSpeedProperty.getPropertyValue(), gradientOffsetProperty.getPropertyValue() * moduleDrawCount.get(),
                                gradientStartColorProperty.getPropertyValue(), gradientsEndColorProperty.getPropertyValue()).getRGB();
                        break;
                    case Rainbow:
                        listColor = ColorUtil.getRainbowColor(2F, moduleDrawCount.get() * 48, 0.7f);
                        break;
                    default:
                        listColor = staticColorProperty.getPropertyValueRGB();
                        break;
                }
                if (blurBackgroundProperty.getPropertyValue())
                    BlurUtil.blurArea((float) posX - 2, (float) posY - 4,
                        getStringWidth(module.getModuleDisplayName()) + 7, 13);

                GLUtil.glFilledQuad((float) posX - 2, (float) posY - 4,
                        getStringWidth(module.getModuleDisplayName()) + 7, 13, new Color(0, 0, 0, backgroundOpacityProperty.getPropertyValue()).getRGB());

                drawStringWithShadow(module.getModuleDisplayName(), (float) posX, (float) posY, listColor);

                switch (listOutlineModeProperty.getPropertyValue()) {
                    case Left:
                        GlStateManager.color(1, 1, 1);
                        GLUtil.glFilledQuad((float) (posX - 3), (float) (posY - 4), 1, 13, listColor);
                        break;
                    case Right:
                        GlStateManager.color(1, 1, 1);
                        GLUtil.glFilledQuad(render2DEvent.getScaledResolution().getScaledWidth() - 1, (float) (posY - 4), 1, 13, listColor);
                        break;
                    case Outline:
                        ArrayList<Module> outlineModuleCache = new ArrayList<>(sortedDisplayModules);
                        outlineModuleCache.removeIf(module1 -> !module1.isVisible());
                        GlStateManager.color(1, 1, 1);
                        int toggledIndex = outlineModuleCache.indexOf(module);
                        double offsetWidth = getStringWidth(module.getModuleDisplayName()) + 5;
                        if (toggledIndex != outlineModuleCache.size() - 1)
                            offsetWidth -= getStringWidth(outlineModuleCache.get(toggledIndex + 1).getModuleDisplayName()) + 5;
                        GlStateManager.color(1, 1, 1, 1);
                        GLUtil.glFilledQuad(posX - 2, posY + 1 + fontRenderer.getHeight(), offsetWidth, 1, listColor);
                        GLUtil.glFilledQuad((int) posX - 2, posY - 4, 1, 14, listColor);
                        break;
                }
                moduleDrawCount.getAndIncrement();
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
            Wrapper.getMinecraftFontRenderer().drawStringWithShadow(text, x, y, color);
        else
            AllureClient.getInstance().getFontManager().mediumFontRenderer.drawStringWithShadow(text, x, y, color);
    }

    private float getStringWidth(String text) {
        if (vanillaFontProperty.getPropertyValue())
            return Wrapper.getMinecraftFontRenderer().getStringWidth(text) - 2;
        else
            return (float) AllureClient.getInstance().getFontManager().mediumFontRenderer.getStringWidth(text);
    }

    private enum ListColorModes {
        Client,
        Gradient,
        Solid,
        Rainbow
    }

    private enum ListOutlineModes {
        Left,
        Right,
        Outline,
        None
    }

    public enum ListPositionModes {
        TOP("Top"),
        BOTTOM("Bottom");

        final String name;
        ListPositionModes(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private enum WatermarkModes {
        Solid,
        New,
        CSGO
    }

    public static HUD getInstance() {
        return (HUD) AllureClient.getInstance().getModuleManager().getModuleByClass.apply(HUD.class);
    }
}
