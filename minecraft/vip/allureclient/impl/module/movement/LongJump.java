package vip.allureclient.impl.module.movement;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.visual.ChatUtil;
import vip.allureclient.impl.event.network.PacketReceiveEvent;
import vip.allureclient.impl.event.network.PacketSendEvent;
import vip.allureclient.impl.event.player.PlayerMoveEvent;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.BooleanProperty;

@ModuleData(moduleName = "Long Jump", moduleBind = 0, moduleCategory = ModuleCategory.MOVEMENT)
public class LongJump extends Module {

    @EventListener
    EventConsumer<PlayerMoveEvent> onPlayerMoveEvent;
    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;
    @EventListener
    EventConsumer<PacketSendEvent> onPacketSendEvent;
    @EventListener
    EventConsumer<PacketReceiveEvent> onPacketReceiveEvent;

    private final BooleanProperty hideJumpsProperty = new BooleanProperty("Hide Jumps", false, this);

    private boolean hasDamaged, bowCharging, bowFinished;
    private int chargingTicks, oldSlot;

    public LongJump() {
        setModuleSuffix("Watchdog");
        onPlayerMoveEvent = (playerMoveEvent -> {
            if (!hasDamaged) {
                playerMoveEvent.setCancelled(true);
                return;
            }
            if (Wrapper.getPlayer().onGround) {
                Wrapper.getPlayer().jump();
                playerMoveEvent.setY(Wrapper.getPlayer().motionY += 0.2121775f);
            }
            if (playerMoveEvent.isMoving()) {
                playerMoveEvent.setSpeed(Wrapper.getPlayer().motionY < 0 ? 0.35 : 0.55);
                if (Wrapper.getPlayer().motionY < 0)
                    Wrapper.getPlayer().motionY += 0.05d;
            }
        });
        onUpdatePositionEvent = (updatePositionEvent -> {
            Wrapper.getPlayer().cameraYaw = 0.1f;
            if (hideJumpsProperty.getPropertyValue()) {
                Wrapper.getPlayer().posY -= Wrapper.getPlayer().posY - Wrapper.getPlayer().lastTickPosY;
                Wrapper.getPlayer().lastTickPosY -= Wrapper.getPlayer().posY - Wrapper.getPlayer().lastTickPosY;
            }
            if (updatePositionEvent.isPre() && !bowFinished) {
                if (!bowCharging) {
                    if (getBowSlot() != -1) {
                        Wrapper.sendPacketDirect(new C09PacketHeldItemChange(getBowSlot()));
                        Wrapper.sendPacketDirect(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, null, 0.0F, 0.0F, 0.0F));
                        bowCharging = true;
                        chargingTicks = 0;
                    } else {
                        ChatUtil.sendMessageToPlayer("You must have a bow in your hot-bar.");
                        setToggled(false);
                    }
                } else {
                    chargingTicks++;
                    switch (chargingTicks) {
                        case 2:
                        case 3:
                            updatePositionEvent.setPitch(-90);
                            break;
                        case 4:
                            Wrapper.sendPacketDirect(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            bowCharging = false;
                            bowFinished = true;
                            break;
                    }
                }
            }
        });
        onPacketSendEvent = (packetSendEvent -> {
            if (hasDamaged && packetSendEvent.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition) {
                //packetSendEvent.setCancelled(true);
            }
        });
        onPacketReceiveEvent = (packetReceiveEvent -> {
           if (packetReceiveEvent.getPacket() instanceof S12PacketEntityVelocity) {
               if (((S12PacketEntityVelocity) packetReceiveEvent.getPacket()).getEntityID() == Wrapper.getPlayer().getEntityId()) {
                   hasDamaged = true;
               }
           }
        });
    }

    @Override
    public void onEnable() {
        hasDamaged = false;
        bowCharging = false;
        bowFinished = false;
        chargingTicks = 0;
        oldSlot = Wrapper.getPlayer().inventory.currentItem;
    }

    @Override
    public void onDisable() {
        Wrapper.sendPacketDirect(new C09PacketHeldItemChange(oldSlot));
    }

    private int getBowSlot() {
        for (int i = 36; i < 45; i++) {
            if (Wrapper.getPlayer().inventoryContainer.getSlot(i).getHasStack()) {
                Item localItem = Wrapper.getPlayer().inventoryContainer.getSlot(i).getStack().getItem();
                if (((localItem instanceof ItemBow))) {
                    return i - 36;
                }
            }
        }
        return -1;
    }
}
