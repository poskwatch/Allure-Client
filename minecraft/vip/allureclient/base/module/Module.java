package vip.allureclient.base.module;

import vip.allureclient.AllureClient;

public class Module {

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
    }

    public Runnable onModuleEnabled = () -> {};

    public Runnable onModuleDisabled = () -> {};

    public void toggleModule(){
        isModuleToggled = !isModuleToggled;
        if (isModuleToggled) {
            onModuleEnabled.run();
            AllureClient.getInstance().getEventManager().subscribe(this);
        }
        else {
            AllureClient.getInstance().getEventManager().unsubscribe(this);
            onModuleDisabled.run();
        }
    }

    public void setModuleToggled(boolean moduleToggled){
        this.isModuleToggled = moduleToggled;
        if (isModuleToggled) {
            onModuleEnabled.run();
            AllureClient.getInstance().getEventManager().subscribe(this);
        }
        else {
            AllureClient.getInstance().getEventManager().unsubscribe(this);
            onModuleDisabled.run();
        }
    }

    public boolean isModuleToggled() {
        return isModuleToggled;
    }

    public void onKeyPressed(int key){
        if(key == moduleKeyBind){
            toggleModule();
        }
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
}
