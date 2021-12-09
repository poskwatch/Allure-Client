package vip.allureclient.visual.screens.dropdown.component;

import vip.allureclient.AllureClient;
import vip.allureclient.base.font.MinecraftFontRenderer;

public class Component {

    private ModuleCategoryFrame parentFrame;

    public final MinecraftFontRenderer fontRenderer = AllureClient.getInstance().getFontManager().smallFontRenderer;

    public final MinecraftFontRenderer getFontRenderer() {
        return AllureClient.getInstance().getFontManager().smallFontRenderer;
    }

    public ModuleCategoryFrame getParentFrame() {
        return parentFrame;
    }

    public void setParentFrame(ModuleCategoryFrame frame){
        this.parentFrame = frame;
    }

    public void onDrawScreen(int mouseX, int mouseY) {

    }

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {

    }

    public void onKeyTyped(int typedKey) {

    }

    public void onGuiClosed() {

    }

    public double getHeight(){
        return 14;
    }

    public void onAnimationEvent(){

    }

    public boolean isHidden() {
        return false;
    }

    public void setOffset(int offset){

    }

}
