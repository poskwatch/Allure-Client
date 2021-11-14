package vip.allureclient.visual.screens.dropdown.component.sub;

import net.minecraft.client.gui.Gui;
import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.property.Property;
import vip.allureclient.base.util.math.MathUtil;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.impl.property.ValueProperty;
import vip.allureclient.visual.screens.dropdown.component.Component;
import vip.allureclient.visual.screens.dropdown.component.sub.impl.BooleanPropertyComponent;
import vip.allureclient.visual.screens.dropdown.component.sub.impl.EnumPropertyComponent;
import vip.allureclient.visual.screens.dropdown.component.sub.impl.ValuePropertyComponent;

import java.util.ArrayList;

public class CheatButtonComponent extends Component {

    private final Module module;
    public int offset;
    public boolean expanded;
    public ArrayList<Component> subComponents = new ArrayList<>();

    private double animationToggleBar;
    private double animationToggleBarTarget;
    private double animationFlowY;
    private double animationFlowYTarget;
    private boolean flowAnimationCompleted;

    public CheatButtonComponent(Module module, int offset){
        this.module = module;
        this.offset = offset;
        int settingOffset = offset + 14;
        for(Property<?> property : AllureClient.getInstance().getPropertyManager().getOptions(module)){
            if(property instanceof BooleanProperty){
                subComponents.add(new BooleanPropertyComponent((BooleanProperty) property, this, settingOffset));
                settingOffset += 14;
            }
            if(property instanceof ValueProperty){
                subComponents.add(new ValuePropertyComponent((ValueProperty<?>) property, this, settingOffset));
                settingOffset += 14;
            }
            if(property instanceof EnumProperty){
                subComponents.add(new EnumPropertyComponent((EnumProperty<?>) property, this, settingOffset));
                settingOffset += 14;
            }
        }
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

        Gui.drawHorizontalGradient(getParentFrame().getX(), (float) y, Math.round(animationToggleBar), getParentFrame().frameHeight,
                0xffd742f5, 0xff42cef5);

        AllureClient.getInstance().getFontManager().smallFontRenderer.drawStringWithShadow(module.getModuleName(), getParentFrame().getX() + 4, y + 4, -1);
        final String ARROW_UP = "F";
        final String ARROW_DOWN = "G";
        AllureClient.getInstance().getFontManager().iconFontRenderer.drawString(expanded ? ARROW_DOWN : ARROW_UP, getParentFrame().getX() + getParentFrame().frameWidth - 10, y + 5, -1);
        animationToggleBar = MathUtil.animateDoubleValue(animationToggleBarTarget, animationToggleBar, 0.03);
        animationFlowY = MathUtil.animateDoubleValue(animationFlowYTarget, animationFlowY, 0.03);
        if(module.isModuleToggled()) {
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
            subComponents.forEach(subComponent -> subComponent.onDrawScreen(mouseX, mouseY));
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(isHoveringButton(mouseX, mouseY)){
            if(mouseButton == 0) {
                module.setModuleToggled(!module.isModuleToggled());
                if (module.isModuleToggled()) {
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
            subComponents.forEach(subComponent -> subComponent.onMouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        if(expanded)
            subComponents.forEach(subComponent -> subComponent.onMouseReleased(mouseX, mouseY, mouseButton));
    }

    @Override
    public void onKeyTyped(int typedKey) {
        if(expanded)
            subComponents.forEach(subComponent -> subComponent.onKeyTyped(typedKey));
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
        if(expanded){
            return (subComponents.size() + 1) * 14;
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
            comp.setOffset(subY);
            subY += 14;
        }
    }
}
