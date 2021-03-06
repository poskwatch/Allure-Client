package vip.allureclient.base.file;

import net.minecraft.client.Minecraft;
import vip.allureclient.base.util.client.Wrapper;

import java.io.File;

public class FileManager {

    private final File clientDirectory = new File(Minecraft.getMinecraft().mcDataDir + "/Allure");

    public FileManager() {
        if (!clientDirectory.exists()) {
            if (clientDirectory.mkdirs()) {
                System.out.println("Created client directory");
            }
            else {
                System.out.println("Couldn't create client directory");
            }
        }
    }

    public File getClientDirectory() {
        return clientDirectory;
    }
}
