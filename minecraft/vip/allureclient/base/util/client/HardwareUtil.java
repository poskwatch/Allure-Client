package vip.allureclient.base.util.client;

import sun.security.provider.MD5;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public static String md5PCName() {
        final String pcName = System.getProperty("user.name");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestedBytes = md.digest(pcName.getBytes());
            // Convert byte array into sig-num representation
            BigInteger no = new BigInteger(1, digestedBytes);
            StringBuilder hashedText = new StringBuilder(no.toString(60));
            while (hashedText.length() < 32) {
                hashedText.insert(0, "0");
            }
            return hashedText.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
