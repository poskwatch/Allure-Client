package vip.allureclient.visual.screens.dropdown.component.sub;

import net.minecraft.client.gui.Gui;
import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.property.Property;
import vip.allureclient.base.util.visual.AnimationUtil;
import vip.allureclient.base.util.visual.GLUtil;
import vip.allureclient.impl.property.*;
import vip.allureclient.visual.screens.dropdown.component.Component;
import vip.allureclient.visual.screens.dropdown.component.sub.impl.*;

import java.util.ArrayList;

public class ModuleComponent extends Component {

    private final Module module;
    public int offset;
    public boolean expanded;
    public ArrayList<Component> subComponents = new ArrayList<>();

    private double animationToggleBar;
    private double animationToggleBarTarget;
    private double animationFlowY;
    private double animationFlowYTarget;
    private boolean flowAnimationCompleted;

    public ModuleComponent(Module module, int offset){
        this.module = module;
        this.offset = offset;
        int settingOffset = offset + 14;
        for (Property<?> property : AllureClient.getInstance().getPropertyManager().getPropertiesByModule(module)) {
            if (property instanceof BooleanProperty){
                subComponents.add(new BooleanPropertyComponent((BooleanProperty) property, this, settingOffset));
                settingOffset += 14;
            }
            if (property instanceof ValueProperty) {
                subComponents.add(new ValuePropertyComponent((ValueProperty<?>) property, this, settingOffset));
                settingOffset += 14;
            }
            if (property instanceof EnumProperty) {
                subComponents.add(new EnumPropertyComponent((EnumProperty<?>) property, this, settingOffset));
                settingOffset += 14;
            }
            if (property instanceof ColorProperty) {
                subComponents.add(new ColorPropertyComponent((ColorProperty) property, this, settingOffset));
                settingOffset += 14;
            }
            if (property instanceof MultiSelectEnumProperty) {
                subComponents.add(new MultiSelectEnumPropertyComponent((MultiSelectEnumProperty<?>) property, this, settingOffset));
                settingOffset += 14;
            }
        }
        subComponents.add(new KeybindingComponent(
                module::getBind,
                module::setBind,
                module::unbind, this, settingOffset));
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {
        double y;
        if(flowAnimationCompleted){
            y = getParentFrame().getY() + offset;
        }
        else {
            y = Math.round(animationFlowY);
        }

        Gui.drawRectWithWidth(getParentFrame().getX(), y, getParentFrame().frameWidth, getParentFrame().frameHeight,
                isHoveringButton(mouseX, mouseY) ? 0xff303030 : 0xff202020);

        GLUtil.glHorizontalGradientQuad(getParentFrame().getX(), y, Math.round(animationToggleBar), getParentFrame().frameHeight,
                0xffd742f5, 0xff42cef5);

        AllureClient.getInstance().getFontManager().smallFontRenderer.drawStringWithShadow(module.getModuleName(), getParentFrame().getX() + 4, y + 4, -1);
        final String ARROW_UP = "F";
        final String ARROW_DOWN = "G";
        AllureClient.getInstance().getFontManager().iconFontRenderer.drawString(expanded ? ARROW_DOWN : ARROW_UP, getParentFrame().getX() + getParentFrame().frameWidth - 10, y + 5, -1);
        animationToggleBar = AnimationUtil.easeOutAnimation(animationToggleBarTarget, animationToggleBar, 0.03);
        animationFlowY = AnimationUtil.linearAnimation(animationFlowYTarget, animationFlowY, 2);
        if(module.isToggled()) {
            animationToggleBarTarget = getParentFrame().frameWidth;
        }
        else{
            animationToggleBarTarget = 0;
        }
        if(getParentFrame().isFrameExpanded()){
            animationFlowYTarget = getParentFrame().getY() + offset;
        }
        else{
            animationFlowYTarget = getParentFrame().getY() + 14;
        }
        if(Math.round(animationFlowY) == getParentFrame().getY() + offset){
            flowAnimationCompleted = true;
        }

        if(expanded){
            subComponents.forEach(subComponent -> {
                if (!subComponent.isHidden())
                    subComponent.onDrawScreen(mouseX, mouseY);
            });
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(isHoveringButton(mouseX, mouseY)){
            if(mouseButton == 0) {
                module.toggle();
                if (module.isToggled()) {
                    animationToggleBarTarget = getParentFrame().frameWidth;
                } else {
                    animationToggleBarTarget = 0;
                }
            }
            if(mouseButton == 1){
                expanded = !expanded;
                subComponents.forEach(Component::onAnimationEvent);
            }
        }
        if(expanded)
            subComponents.forEach(subComponent -> {
                if (!subComponent.isHidden())
                    subComponent.onMouseClicked(mouseX, mouseY, mouseButton);
            });
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        if(expanded)
            subComponents.forEach(subComponent -> {
                if (!subComponent.isHidden())
                    subComponent.onMouseReleased(mouseX, mouseY, mouseButton);
            });
    }

    @Override
    public void onKeyTyped(int typedKey) {
        subComponents.forEach(subComponent -> {
            if (!subComponent.isHidden())
                subComponent.onKeyTyped(typedKey);
        });
        super.onKeyTyped(typedKey);
    }

    private boolean isHoveringButton(int mouseX, int mouseY){
        return mouseX >= getParentFrame().getX() && mouseX <= getParentFrame().getX() + getParentFrame().frameWidth
                && mouseY >= getParentFrame().getY() + offset && mouseY <= getParentFrame().getY() + offset + 13;
    }

    @Override
    public void onAnimationEvent() {
        animationFlowY = getParentFrame().getY() + 14;
        if(getParentFrame().isFrameExpanded()){
            animationFlowYTarget = getParentFrame().getY() + offset;
            flowAnimationCompleted = false;
        }
        else{
            animationFlowY = getParentFrame().getY() + 14;
        }
        if(expanded){
            subComponents.forEach(Component::onAnimationEvent);
        }
    }

    @Override
    public double getHeight(){
        ArrayList<Component> visibleComponents = new ArrayList<>(subComponents);
        visibleComponents.removeIf(Component::isHidden);
        if(expanded){
            double height = 14;
            for (Component component : visibleComponents) {
                height += component.getHeight();
            }
            return height;
        }
        else{
            return 14;
        }
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
        int subY = this.offset + 14;
        for(Component comp : subComponents) {
            if (!comp.isHidden()) {
                comp.setOffset(subY);
                subY += comp.getHeight();
            }
        }
    }
}
