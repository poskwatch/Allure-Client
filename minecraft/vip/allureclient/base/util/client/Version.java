package vip.allureclient.base.util.client;

public class Version {

    // Version wrapper class, major minor build
    private final int major, minor;
    private final char[] build;

    public Version(int major, int minor, char[] build) {
        this.major = major;
        this.minor = minor;
        this.build = build;
    }

    @Override
    public String toString() {
        return String.format("%d.%d", major, minor);
    }

    public String getBuild() {
        return new String(build);
    }
}
