package vip.allureclient.impl.module.movement;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.base.util.player.PacketUtil;
import vip.allureclient.base.util.visual.ChatUtil;
import vip.allureclient.impl.event.events.network.PacketReceiveEvent;
import vip.allureclient.impl.event.events.player.PlayerMoveEvent;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.visual.notification.NotificationType;

public class LongJump extends Module {

    private final BooleanProperty hideJumpsProperty = new BooleanProperty("Hide Jumps", false, this);

    private boolean hasDamaged, bowCharging, bowFinished;
    private int chargingTicks, oldSlot, groundTicks;

    public LongJump() {
        super("Long Jump", ModuleCategory.MOVEMENT);
        setModuleSuffix("Watchdog");
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = (event -> {
                if (hideJumpsProperty.getPropertyValue()) {
                    mc.thePlayer.posY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
                    mc.thePlayer.lastTickPosY -= mc.thePlayer.posY - mc.thePlayer.lastTickPosY;
                }
                if (event.isPre() && !bowFinished) {
                    if (!bowCharging) {
                        if (getBowSlot() != -1) {
                            PacketUtil.sendPacketDirect(new C09PacketHeldItemChange(getBowSlot()));
                            PacketUtil.sendPacketDirect(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, null, 0.0F, 0.0F, 0.0F));
                            bowCharging = true;
                            chargingTicks = 0;
                        } else {
                            AllureClient.getInstance().getNotificationManager().addNotification("Long Jump",
                                    "You must have a bow in your hotbar for Long Jump", 2000, NotificationType.ERROR);
                            setToggled(false);
                        }
                    } else {
                        chargingTicks++;
                        switch (chargingTicks) {
                            case 2:
                            case 3:
                                event.setPitch(-90, false);
                                break;
                            case 4:
                                PacketUtil.sendPacketDirect(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                bowCharging = false;
                                bowFinished = true;
                                break;
                        }
                    }
                }
                if (mc.thePlayer.onGround && hasDamaged && bowFinished && ++groundTicks > 1) {
                    AllureClient.getInstance().getNotificationManager().addNotification("Long Jump",
                            "Long Jump was auto disabled to prevent flags/errors", 1000, NotificationType.WARNING);
                    setToggled(false);
                }
            });
            @EventHandler(events = PlayerMoveEvent.class)
            final IEventCallable<PlayerMoveEvent> onPlayerMove = (event -> {
                if (!hasDamaged) {
                    event.setCancelled(true);
                    return;
                }
                if (mc.thePlayer.onGround) {
                    event.setY(0.745);
                    mc.thePlayer.motionY = 0.745;
                    event.setSpeed(MovementUtil.getBaseMoveSpeed() * 1.6);
                }
                if (event.isMoving()) {
                    event.setSpeed(mc.thePlayer.motionY < 0 ? 0.35 : 0.57);
                    if (event.getY() < 0)
                        event.setY(mc.thePlayer.motionY += 0.01);
                }
            });
            @EventHandler(events = PacketReceiveEvent.class)
            final IEventCallable<PacketReceiveEvent> onReceivePacket = (event -> {
                final Packet<?> packet = event.getPacket();
                if (packet instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) packet;
                    if (s12.getEntityID() == mc.thePlayer.getEntityId()) {
                        hasDamaged = true;
                    }
                }
            });
        });
    }

    @Override
    public void onEnable() {
        hasDamaged = false;
        bowCharging = false;
        bowFinished = false;
        chargingTicks = 0;
        groundTicks = 0;
        oldSlot = mc.thePlayer.inventory.currentItem;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        // Send change item packet to original slot number
        PacketUtil.sendPacketDirect(new C09PacketHeldItemChange(oldSlot));
        mc.thePlayer.inventory.currentItem = oldSlot;
        super.onDisable();
    }

    // Method called to get slot of bow in hotbar
    private int getBowSlot() {
        for (int i = 36; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                Item localItem = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
                if (((localItem instanceof ItemBow))) {
                    return i - 36;
                }
            }
        }
        return -1;
    }

    private enum MovementMode {
        BOOST("Boost"),
        GLIDE("Glide");

        final String name;

        MovementMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
