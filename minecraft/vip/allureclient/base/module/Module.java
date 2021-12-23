package vip.allureclient.base.module;

import vip.allureclient.AllureClient;
import vip.allureclient.base.bind.Bindable;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.interfaces.Toggleable;

public class Module implements Toggleable, Bindable {

    private final String moduleName;
    private int moduleKeyBind;
    private final ModuleCategory moduleCategory;
    private boolean isModuleToggled;
    private String moduleSuffix;

    public Module(){
        if(isIdentified()) {
            this.moduleName = getClass().getAnnotation(ModuleData.class).moduleName();
            this.moduleKeyBind = getClass().getAnnotation(ModuleData.class).moduleBind();
            this.moduleCategory = getClass().getAnnotation(ModuleData.class).moduleCategory();
        }
        else {
            this.moduleName = "Unidentified Module";
            this.moduleKeyBind = 0;
            this.moduleCategory = ModuleCategory.COMBAT;
        }
        AllureClient.getInstance().getBindManager().registerBind(moduleKeyBind, this);
    }

    public String getModuleName(){
        return moduleName;
    }

    public String getModuleDisplayName() {
        return getModuleSuffix() == null ? getModuleName() : getModuleName() + " \2477" + getModuleSuffix();
    }

    public String getModuleSuffix() {
        return moduleSuffix;
    }

    public void setModuleSuffix(String moduleSuffix) {
        this.moduleSuffix = moduleSuffix;
    }

    public int getModuleKeyBind(){
        return moduleKeyBind;
    }

    public void setModuleKeyBind(int moduleKeyBind){
        this.moduleKeyBind = moduleKeyBind;
    }

    public ModuleCategory getModuleCategory() {
        return moduleCategory;
    }

    private boolean isIdentified() {
        return getClass().isAnnotationPresent(ModuleData.class);
    }

    @Override
    public boolean isToggled() {
        return isModuleToggled;
    }

    @Override
    public void setToggled(boolean toggled) {
        isModuleToggled = toggled;
        if (isModuleToggled) {
            onEnable();
            AllureClient.getInstance().getEventManager().subscribe(this);
        }
        else {
            AllureClient.getInstance().getEventManager().unsubscribe(this);
            onDisable();
        }
    }

    @Override
    public void toggle() {
        isModuleToggled = !isModuleToggled;
        if (isModuleToggled) {
            onEnable();
            AllureClient.getInstance().getEventManager().subscribe(this);
        }
        else {
            AllureClient.getInstance().getEventManager().unsubscribe(this);
            onDisable();
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public int getBind() {
        return moduleKeyBind;
    }

    @Override
    public void onPressed() {
        toggle();
    }
}
