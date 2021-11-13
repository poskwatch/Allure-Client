package vip.allureclient.visual.screens.dropdown;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.util.math.MathUtil;
import vip.allureclient.visual.screens.dropdown.component.CategoryFrame;
import vip.allureclient.visual.screens.dropdown.component.Component;
import vip.allureclient.visual.screens.dropdown.component.sub.CheatButtonComponent;

import java.io.IOException;
import java.util.ArrayList;

public class GuiDropDown extends GuiScreen {

    private static final ArrayList<CategoryFrame> categoryFrames = new ArrayList<>();
    private double nekoAnimation;

    public static void setup(){
        for(int i = 0; i < ModuleCategory.values().length; i++){
            categoryFrames.add(new CategoryFrame(ModuleCategory.values()[i], 10 + i * 125, 10));
        }
    }

    @Override
    public void initGui() {
        categoryFrames.forEach(categoryFrame -> {
            for(Component component : categoryFrame.getChildrenComponents()){
                if(component instanceof CheatButtonComponent){
                    ((CheatButtonComponent) component).subComponents.forEach(Component::onAnimationEvent);
                }
            }
        });
        nekoAnimation = height;
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        //Allure.getInstance().getModuleManager().getModule(ClickGUI.class).setToggled(false);
        super.onGuiClosed();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawGradientRect(0, 0, width, height, 0x6042cef5, 0xffd742f5);
        Gui.drawRectWithWidth(0, 0, width, height, 0x70000000);
        GL11.glPushMatrix();
        mc.getTextureManager().bindTexture(new ResourceLocation("Allure/Images/anime.png"));
        GL11.glColor4f(1, 1, 1, 1);
        nekoAnimation = MathUtil.animateDoubleValue(height - 330, nekoAnimation, 0.05);
        Gui.drawModalRectWithCustomSizedTexture(width - 175, Math.round((float) nekoAnimation), 0, 0, 175, 330, 175, 330);
        GL11.glPopMatrix();
        categoryFrames.forEach(categoryFrame -> {
            categoryFrame.onDrawScreen(mouseX, mouseY);
            categoryFrame.updateComponents();
        });
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        categoryFrames.forEach(categoryFrame -> categoryFrame.onMouseClicked(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        categoryFrames.forEach(categoryFrame -> categoryFrame.onMouseReleased(mouseX, mouseY, state));
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_RSHIFT){
            mc.thePlayer.closeScreen();
        }
        categoryFrames.forEach(categoryFrame -> categoryFrame.onKeyTyped(keyCode));
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
