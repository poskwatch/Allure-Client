package vip.allureclient.base.util.client;

public class HardwareUtil {

    public static String getOS(){
        final String unformattedOS = System.getProperty("os-name");
        if(unformattedOS.contains("win")){
            return "Windows";
        }
        else if(unformattedOS.contains("mac")){
            return "Mac";
        }
        else if(unformattedOS.contains("unix")){
            return "Linux";
        }
        else {
            return "N/A";
        }
    }

}
