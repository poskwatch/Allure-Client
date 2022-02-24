package vip.allureclient.impl.module.player;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import vip.allureclient.AllureClient;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.Stopwatch;
import vip.allureclient.base.util.player.IRotations;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.base.util.player.PacketUtil;
import vip.allureclient.impl.event.events.network.PacketReceiveEvent;
import vip.allureclient.impl.event.events.network.PacketSendEvent;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;
import vip.allureclient.impl.event.events.world.WorldLoadEvent;
import vip.allureclient.impl.module.combat.KillAura;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Disabler extends Module implements IRotations {

    Queue<C0FPacketConfirmTransaction> confirmTransactionQueue = new ConcurrentLinkedQueue<>();
    Queue<C00PacketKeepAlive> keepAliveQueue = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<ClickJob> clickJobs = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<C03PacketPlayer> blinkPackets = new ConcurrentLinkedQueue<>();
    public static Stopwatch timer = new Stopwatch();
    public Stopwatch clickTimer = new Stopwatch();
    Stopwatch lastRelease = new Stopwatch();

    int lastUid, cancelledPackets;
    public static boolean hasDisabled;
    public boolean isInInv = false, isCraftingItem = false;

    public Disabler() {
        super("Bypass", ModuleCategory.PLAYER);
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class, priority = Priority.VERY_HIGH)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = (event -> {
                if (mc.thePlayer.ticksExisted % 40 == 0) {
                    int rate = (int) ((cancelledPackets / 40f) * 100);
                    cancelledPackets = 0;
                }
                if (!clickJobs.isEmpty() && mc.thePlayer.fallDistance <= 1 && KillAura.getInstance().getCurrentTarget() == null) {
                    if (!MovementUtil.isMoving()) {
                        if (!isInInv) {
                            mc.getNetHandler().getNetworkManager().sendPacket(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                        }
                        while (!clickJobs.isEmpty()) {
                            ClickJob job = clickJobs.poll();
                            mc.playerController.windowClick(job.windowId, job.slotId, job.mouseButtonClicked, job.mode, job.playerIn);
                        }
                        clickTimer.reset();
                    }
                } else {
                    if (isInInv && !(mc.currentScreen instanceof GuiInventory) && clickTimer.hasReached(200)) {
                        mc.getNetHandler().getNetworkManager().sendPacket(new C0DPacketCloseWindow(0));
                    }
                }
                if (MovementUtil.isMoving()) {
                    if (!AllureClient.getInstance().getModuleManager().getModuleOrNull("Scaffold").isToggled()) {
                        event.setYaw(MovementUtil.getMovementDirection(), false);
                    }
                }
                if (hasDisabled) {
                    if (confirmTransactionQueue.isEmpty()) {
                        lastRelease.reset();
                    } else {
                        if (confirmTransactionQueue.size() >= 6) {
                            while (!keepAliveQueue.isEmpty())
                                PacketUtil.sendPacketDirect(keepAliveQueue.poll());
                            while (!confirmTransactionQueue.isEmpty()) {
                                C0FPacketConfirmTransaction poll = confirmTransactionQueue.poll();
                                PacketUtil.sendPacketDirect(poll);
                            }
                        }
                    }
                }
            });
            @EventHandler(events = PacketReceiveEvent.class, priority = Priority.VERY_HIGH)
            final IEventCallable<PacketReceiveEvent> onReceivePacket = (e -> {
                if (e.getPacket() instanceof S08PacketPlayerPosLook) {
                    S08PacketPlayerPosLook packet = ((S08PacketPlayerPosLook) e.getPacket());
                    if (!hasDisabled && mc.thePlayer.ticksExisted > 20) {
                        e.setCancelled(true);
                    }
                }
            });
            @EventHandler(events = PacketSendEvent.class, priority = Priority.VERY_HIGH)
            final IEventCallable<PacketSendEvent> onSendPacket = (e -> {
                if (hasDisabled) {
                    if (e.getPacket() instanceof C03PacketPlayer && !(e.getPacket() instanceof C03PacketPlayer.C04PacketPlayerPosition || e.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook || e.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook)) {
                        cancelledPackets ++;
                        e.setCancelled(true);
                    }
                }
                if (!e.isCancelled() && e.getPacket() instanceof C03PacketPlayer) {
                    if (isInInv) {
                        if (blinkPackets.isEmpty())
                            System.out.println("Start Blink");
                        blinkPackets.offer((C03PacketPlayer) e.getPacket());
                        e.setCancelled(true);
                    } else if (!blinkPackets.isEmpty()) {
                        System.out.println("Stop Blink");
                        while (!blinkPackets.isEmpty()) {
                            PacketUtil.sendPacketDirect(blinkPackets.poll());
                        }
                    }
                }
                if (e.getPacket() instanceof C16PacketClientStatus) {
                    C16PacketClientStatus clientStatus = ((C16PacketClientStatus) e.getPacket());
                    if (clientStatus.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                        if (!isInInv) {
                            System.out.println("Inv: Open");
                            isInInv = true;
                        } else {
                            e.setCancelled(true);
                        }
                    }
                }
                if (e.getPacket() instanceof C0DPacketCloseWindow) {
                    C0DPacketCloseWindow closeWindow = ((C0DPacketCloseWindow) e.getPacket());
                    if (closeWindow.windowId == 0) {
                        if (isInInv) {
                            System.out.println("Inv: Close");
                            isInInv = false;
                        } else {
                            e.setCancelled(true);
                        }
                    }
                }

                // Disabler
                if (e.getPacket() instanceof C0FPacketConfirmTransaction) {
                    processConfirmTransactionPacket(e);
                } else if (e.getPacket() instanceof C00PacketKeepAlive) {
                    processKeepAlivePacket(e);
                } else if (e.getPacket() instanceof C03PacketPlayer) {
                    processPlayerPosLooksPacket(e);
                }
            });
            @EventHandler(events = WorldLoadEvent.class, priority = Priority.VERY_HIGH)
            final IEventCallable<WorldLoadEvent> onWorldLoad = (event -> {
                confirmTransactionQueue.clear();
                keepAliveQueue.clear();
                hasDisabled = false;
                lastUid = cancelledPackets = 0;
                clickJobs.clear();
                isInInv = false;
                isCraftingItem = false;
            });
        });
    }

    @Override
    public float[] getRotations() {
        return new float[]{MovementUtil.getMovementDirection(), mc.thePlayer.rotationPitch};
    }

    @Override
    public void setRotations(UpdatePositionEvent event, float[] rotations, boolean visualize) {
        event.setYaw(rotations[0], visualize);
        event.setPitch(rotations[1], visualize);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private boolean isMoving() {
        return !timer.hasReached(100);
    }

    public void processConfirmTransactionPacket(PacketSendEvent e) {
        C0FPacketConfirmTransaction packet = ((C0FPacketConfirmTransaction) e.getPacket());
        int windowId = packet.getWindowId();
        int uid = packet.getUid();
        if (windowId != 0 || uid >= 0) {
            System.out.println("Inventory synced.");
        } else {
            if (uid == --lastUid) {
                if (!hasDisabled) {
                    hasDisabled = true;
                }
                confirmTransactionQueue.offer(packet);
                e.setCancelled(true);
            }
            lastUid = uid;
        }
    }

    public void processKeepAlivePacket(PacketSendEvent e) {
        C00PacketKeepAlive packet = ((C00PacketKeepAlive) e.getPacket());
        if (hasDisabled) {
            keepAliveQueue.offer(packet);
            e.setCancelled(true);
        }
    }

    public void processPlayerPosLooksPacket(PacketSendEvent e) {
        if (!hasDisabled) {
            e.setCancelled(true);
        }
    }

    private void delayClick(int windowId, int slotId, int mouseButtonClicked, int mode, EntityPlayer playerIn) {
        if (windowId != 0) {
            mc.playerController.windowClick(windowId, slotId, mouseButtonClicked, mode, playerIn);
        } else {
            ClickJob job = new ClickJob(windowId, slotId, mouseButtonClicked, mode, playerIn);
            for (ClickJob clickJob : clickJobs) {
                if (clickJob.equals(job)) {
                    return;
                }
            }
            clickJobs.offer(job);
        }
    }

    static class ClickJob {
        public final int windowId, slotId, mouseButtonClicked, mode;
        public final EntityPlayer playerIn;
        public ClickJob(int windowId, int slotId, int mouseButtonClicked, int mode, EntityPlayer playerIn) {
            this.windowId = windowId;
            this.slotId = slotId;
            this.mouseButtonClicked = mouseButtonClicked;
            this.mode = mode;
            this.playerIn = playerIn;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClickJob)) return false;
            ClickJob clickJob = (ClickJob) o;
            return windowId == clickJob.windowId && slotId == clickJob.slotId && mouseButtonClicked == clickJob.mouseButtonClicked && mode == clickJob.mode && playerIn.equals(clickJob.playerIn);
        }
    }
}
