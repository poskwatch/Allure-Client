package vip.allureclient.impl.module.world;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.Stopwatch;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.ValueProperty;

public class ChestStealer extends Module {

    private final BooleanProperty autoCloseProperty = new BooleanProperty("Auto Close", true, this);

    private final BooleanProperty titleCheckProperty = new BooleanProperty("No Menu Steal", true, this);

    private final ValueProperty<Long> delayMSProperty = new ValueProperty<>("Delay (MS)", 300L, 0L, 1000L, this);

    private final Stopwatch delayMSTimer = new Stopwatch();

    public ChestStealer() {
        super("Chest Stealer", ModuleCategory.WORLD);
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = (event -> {
                if (mc.thePlayer.openContainer != null && mc.thePlayer.openContainer instanceof ContainerChest) {
                    ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;
                    for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
                        if (chest.getLowerChestInventory().getStackInSlot(i) != null && delayMSTimer.hasReached(delayMSProperty.getPropertyValue())) {
                            if(titleCheckProperty.getPropertyValue() && !isChest())
                                return;
                            mc.playerController.windowClick(chest.windowId, i, 0, 1, mc.thePlayer);
                            delayMSTimer.reset();
                        }
                    }
                    if (isChestEmpty(chest) && autoCloseProperty.getPropertyValue())
                        mc.thePlayer.closeScreen();
                }
            });
        });
    }

    private boolean isChest() {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiChest) {
            GuiChest chestGui = (GuiChest) Minecraft.getMinecraft().currentScreen;
            String name = chestGui.lowerChestInventory.getDisplayName().getUnformattedText();
            return name.equals("Large Chest") || name.contains("Chest") || name.equalsIgnoreCase("low");
        }
        return false;
    }

    private boolean isChestEmpty(ContainerChest c) {
        for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); i++) {
            ItemStack slot = c.getLowerChestInventory().getStackInSlot(i);
            if (slot != null)
                return false;
        }
        return true;
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
