package vip.allureclient.base.util.client;

public class TimerUtil {

    public long lastMS = System.currentTimeMillis();

    public void reset() {
        lastMS = System.currentTimeMillis();
    }

    public boolean hasReached(long time)
    {
        return System.currentTimeMillis() - lastMS > time;
    }

}
