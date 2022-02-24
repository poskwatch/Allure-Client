package vip.allureclient.visual.screens.dropdown;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.visual.AnimationUtil;
import vip.allureclient.base.util.visual.BlurUtil;
import vip.allureclient.base.util.visual.ColorUtil;
import vip.allureclient.base.util.visual.glsl.GLUtil;
import vip.allureclient.visual.screens.dropdown.component.Component;
import vip.allureclient.visual.screens.dropdown.component.sub.ModuleCategoryFrame;
import vip.allureclient.visual.screens.dropdown.component.sub.ModuleComponent;

import java.io.IOException;
import java.util.ArrayList;

public class GuiDropDown extends GuiScreen {

    private static final ArrayList<ModuleCategoryFrame> MODULE_CATEGORY_FRAMES = new ArrayList<>();
    private double nekoAnimation;

    public Runnable onStartTask = () -> {
        for(int i = 0; i < ModuleCategory.values().length; i++){
            MODULE_CATEGORY_FRAMES.add(new ModuleCategoryFrame(ModuleCategory.values()[i], 10 + i * 125, 10));
        }
    };

    @Override
    public void initGui() {
        MODULE_CATEGORY_FRAMES.forEach(moduleCategoryFrame -> {
            for(Component component : moduleCategoryFrame.getChildrenComponents()){
                if(component instanceof ModuleComponent){
                    ((ModuleComponent) component).subComponents.forEach(Component::onAnimationEvent);
                }
            }
        });
        nekoAnimation = height;
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GLUtil.glVerticalGradientQuad(0, 0, width, height, ColorUtil.changeAlpha(ColorUtil.getClientColors()[0], 30).getRGB(), ColorUtil.getClientColors()[1].getRGB());
        Gui.drawRectWithWidth(0, 0, width, height, 0x10000000);
        BlurUtil.blurArea(0, 0, width, height);

        MODULE_CATEGORY_FRAMES.forEach(moduleCategoryFrame -> {
            moduleCategoryFrame.onDrawScreen(mouseX, mouseY);
            moduleCategoryFrame.updateComponents();
        });
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        MODULE_CATEGORY_FRAMES.forEach(moduleCategoryFrame -> moduleCategoryFrame.onMouseClicked(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        MODULE_CATEGORY_FRAMES.forEach(moduleCategoryFrame -> moduleCategoryFrame.onMouseReleased(mouseX, mouseY, state));
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_RSHIFT){
            mc.thePlayer.closeScreen();
        }
        MODULE_CATEGORY_FRAMES.forEach(moduleCategoryFrame -> moduleCategoryFrame.onKeyTyped(keyCode));
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        MODULE_CATEGORY_FRAMES.forEach(ModuleCategoryFrame::onGuiClosed);
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
