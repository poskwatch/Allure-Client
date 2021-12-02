package vip.allureclient.impl.module.player;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import org.lwjgl.input.Keyboard;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.network.PacketSendEvent;
import vip.allureclient.impl.event.player.PlayerMoveEvent;

@ModuleData(moduleName = "Gui Move", moduleBind = 0, moduleCategory = ModuleCategory.PLAYER)
public class GuiMove extends Module {

    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;

    @EventListener
    EventConsumer<PlayerMoveEvent> onPlayerMoveEvent;

    public GuiMove () {
        onPacketSendEvent = (packetSendEvent -> {
           if (packetSendEvent.getPacket() instanceof C0DPacketCloseWindow || packetSendEvent.getPacket() instanceof C16PacketClientStatus) {
               packetSendEvent.setCancelled(true);
           }
        });
        onPlayerMoveEvent = (playerMoveEvent -> {
            if (Wrapper.getMinecraft().currentScreen instanceof GuiInventory || Wrapper.getMinecraft().currentScreen instanceof GuiContainer) {
                Wrapper.getMinecraft().gameSettings.keyBindForward.setPressed(Keyboard.isKeyDown(Wrapper.getMinecraft().gameSettings.keyBindForward.getKeyCode()));
                Wrapper.getMinecraft().gameSettings.keyBindBack.setPressed(Keyboard.isKeyDown(Wrapper.getMinecraft().gameSettings.keyBindBack.getKeyCode()));
                Wrapper.getMinecraft().gameSettings.keyBindLeft.setPressed(Keyboard.isKeyDown(Wrapper.getMinecraft().gameSettings.keyBindLeft.getKeyCode()));
                Wrapper.getMinecraft().gameSettings.keyBindRight.setPressed(Keyboard.isKeyDown(Wrapper.getMinecraft().gameSettings.keyBindRight.getKeyCode()));
            }
        });
    }
}
