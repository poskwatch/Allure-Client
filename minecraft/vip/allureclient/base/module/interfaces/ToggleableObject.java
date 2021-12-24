package vip.allureclient.base.module.interfaces;

public interface ToggleableObject {
    boolean isToggled();
    void setToggled(boolean toggled);
    void toggle();
    void onEnable();
    void onDisable();
}
