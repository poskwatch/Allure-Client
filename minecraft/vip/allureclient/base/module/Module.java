package vip.allureclient.base.module;

import vip.allureclient.AllureClient;

public class Module {

    private final String moduleName;
    private int moduleKeyBind;
    private final ModuleCategory moduleCategory;
    private boolean isModuleToggled;

    public Module(){
        final boolean hasDataAnnotation = getClass().isAnnotationPresent(ModuleData.class);
        this.moduleName = hasDataAnnotation ? getClass().getAnnotation(ModuleData.class).label() : "No Name Found";
        this.moduleKeyBind = hasDataAnnotation ? getClass().getAnnotation(ModuleData.class).keyBind() : 0;
        this.moduleCategory = hasDataAnnotation ? getClass().getAnnotation(ModuleData.class).category() : ModuleCategory.WORLD;
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

    public int getModuleKeyBind(){
        return moduleKeyBind;
    }

    public void setModuleKeyBind(int moduleKeyBind){
        this.moduleKeyBind = moduleKeyBind;
    }

    public ModuleCategory getModuleCategory() {
        return moduleCategory;
    }
}
