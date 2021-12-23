package vip.allureclient.base.module.enums;

public enum ModuleCategory {
    COMBAT("Combat", "A"),
    PLAYER("Player", "B"),
    MOVEMENT("Movement", "C"),
    VISUAL("Visual", "D"),
    WORLD("World", "E");

    public final String categoryName;
    public final String iconCode;

    ModuleCategory(String categoryName, String iconCode){
        this.categoryName = categoryName;
        this.iconCode = iconCode;
    }
}
