package vip.allureclient.visual.screens.dropdown.component;

import net.minecraft.client.gui.Gui;
import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.util.math.MathUtil;
import vip.allureclient.visual.screens.dropdown.component.sub.CheatButtonComponent;

import java.util.ArrayList;

public class ModuleCategoryFrame {

    private final ModuleCategory category;
    private boolean frameExpanded = true, isDraggingFrame;
    private final ArrayList<Component> childrenComponents = new ArrayList<>();
    private int x, y, distX, distY;
    public int frameWidth = 115, frameHeight = 14;

    private double outlineAnimation = 14;
    private boolean completedOutlineAnimation;

    public ModuleCategoryFrame(ModuleCategory category, int x, int y){
        this.category = category;
        this.x = x;
        this.y = y;

        int buttonOffset = 14;
        for(Module module : AllureClient.getInstance().getModuleManager().getModulesByCategory.apply(category)){
            addChildComponent(new CheatButtonComponent(module, buttonOffset));
            buttonOffset += 14;
        }
    }

    public void onDrawScreen(int mouseX, int mouseY) {
        Gui.drawRectWithWidth(x - 1, y, frameWidth + 2, frameHeight, 0xff101010);

        AllureClient.getInstance().getFontManager().smallFontRenderer.drawStringWithShadow(category.categoryName, x + 17, y + 4, -1);
        AllureClient.getInstance().getFontManager().iconFontRenderer.drawString(category.iconCode, x + 4, y + 5, -1);

        outlineAnimation = MathUtil.animateDoubleValue(getFrameHeight() + 1, outlineAnimation, 0.03);
        if(frameExpanded)
            Gui.drawRectWithWidth(x - 1, y + 14, frameWidth + 2, completedOutlineAnimation ? getFrameHeight() + 1 : outlineAnimation, 0xff101010);
        if(isDraggingFrame){
            x = mouseX - distX;
            y = mouseY - distY;
        }
        if(frameExpanded){
            childrenComponents.forEach(childComponent -> childComponent.onDrawScreen(mouseX, mouseY));
        }
        if(outlineAnimation == getFrameHeight() + 1){
            completedOutlineAnimation = true;
        }
        if(Math.round(outlineAnimation) == getFrameHeight() + 1){
            completedOutlineAnimation = true;
        }
    }

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(isHoveringFrame(mouseX, mouseY)){
            if(mouseButton == 0) {
                distX = mouseX - x;
                distY = mouseY - y;
                isDraggingFrame = true;
            }
            if(mouseButton == 1){
                frameExpanded = !frameExpanded;
                if(frameExpanded){
                    outlineAnimation = 14;
                    completedOutlineAnimation = false;
                }
                for (Component childrenComponent : childrenComponents) {
                    if(childrenComponent instanceof CheatButtonComponent){
                        ((CheatButtonComponent) childrenComponent).expanded = false;
                    }
                }
                childrenComponents.forEach(Component::onAnimationEvent);
            }
        }
        if(frameExpanded){
            childrenComponents.forEach(childComponent -> childComponent.onMouseClicked(mouseX, mouseY, mouseButton));

        }
    }

    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {

        isDraggingFrame = false;

        if(frameExpanded){
            childrenComponents.forEach(childComponent -> childComponent.onMouseReleased(mouseX, mouseY, mouseButton));
        }
    }

    public void onKeyTyped(int typedKey) {
        if(frameExpanded){
            childrenComponents.forEach(childComponent -> childComponent.onKeyTyped(typedKey));
        }
    }

    public void onGuiClosed() {
        childrenComponents.forEach(Component::onGuiClosed);
    }

    public ArrayList<Component> getChildrenComponents() {
        return childrenComponents;
    }

    public void addChildComponent(Component component){
        component.setParentFrame(this);
        this.childrenComponents.add(component);
    }

    public void updateComponents(){
        int off = this.frameHeight;
        for(Component comp : childrenComponents) {
            comp.setOffset(off);
            off += comp.getHeight();
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    private boolean isHoveringFrame(int mouseX, int mouseY){
        return mouseX >= x && mouseX <= x + frameWidth && mouseY >= y && mouseY <= y + frameHeight;
    }

    private int getFrameHeight(){
        int initialHeight = childrenComponents.size() * frameHeight;
        for(Component component : childrenComponents){
            if(component instanceof CheatButtonComponent){
                if(((CheatButtonComponent) component).expanded){
                    ArrayList<Component> visibleCheatSubs = new ArrayList<>(((CheatButtonComponent) component).subComponents);
                    visibleCheatSubs.removeIf(Component::isHidden);
                    for (Component sub : visibleCheatSubs)
                        initialHeight += sub.getHeight();
                }
            }
        }
        return initialHeight;
    }

    public boolean isFrameExpanded(){
        return frameExpanded;
    }
}
