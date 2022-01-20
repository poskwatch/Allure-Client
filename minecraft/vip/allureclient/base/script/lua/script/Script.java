package vip.allureclient.base.script.lua.script;

import vip.allureclient.AllureClient;

public class Script {

    private final String scriptContents;

    public Script(String scriptContents) {
        this.scriptContents = scriptContents;
    }

    public void loadScript() {
        AllureClient.getInstance().getScriptManager().runScript(scriptContents);
    }

}
