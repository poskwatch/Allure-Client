package vip.allureclient.base.util.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import vip.allureclient.base.util.client.Wrapper;

public class PacketUtil {

    public static void processClientS08(S08PacketPlayerPosLook s08) {
        Wrapper.sendPacketDirect(new C03PacketPlayer.C06PacketPlayerPosLook(s08.getX(), s08.getY(), s08.getZ(), s08.getYaw(), s08.getPitch(), true));
    }

}
