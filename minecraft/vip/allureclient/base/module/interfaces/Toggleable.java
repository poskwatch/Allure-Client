package vip.allureclient.base.module.interfaces;

public interface Toggleable {
    boolean isToggled();
    void setToggled(boolean toggled);
    void toggle();
    void onEnable();
    void onDisable();
}
