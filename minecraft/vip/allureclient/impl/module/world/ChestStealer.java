package vip.allureclient.impl.module.world;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.ModuleCategory;
import vip.allureclient.base.module.ModuleData;
import vip.allureclient.base.util.client.TimerUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.ValueProperty;

@ModuleData(moduleName = "ChestStealer", keyBind = 0, category = ModuleCategory.WORLD)
public class ChestStealer extends Module {

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    private final BooleanProperty autoCloseProperty = new BooleanProperty("Auto Close", true, this);

    private final BooleanProperty titleCheckProperty = new BooleanProperty("No Menu Steal", true, this);

    private final ValueProperty<Long> delayMSProperty = new ValueProperty<>("Delay (MS)", 300L, 0L, 1000L, this);

    private final TimerUtil delayMSTimer = new TimerUtil();

    public ChestStealer() {
        onUpdatePositionEvent = (updatePositionEvent -> {
            if(Wrapper.getPlayer().openContainer != null && Wrapper.getPlayer().openContainer instanceof ContainerChest) {
                ContainerChest chest = (ContainerChest) Wrapper.getPlayer().openContainer;
                for(int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
                    if(chest.getLowerChestInventory().getStackInSlot(i) != null && delayMSTimer.hasReached(delayMSProperty.getPropertyValue())) {
                        if(titleCheckProperty.getPropertyValue() && !isChest())
                            return;
                        Wrapper.getMinecraft().playerController.windowClick(chest.windowId, i, 0, 1, Wrapper.getPlayer());
                        delayMSTimer.reset();
                    }
                }
                if (isChestEmpty(chest) && autoCloseProperty.getPropertyValue()) {
                    Wrapper.getPlayer().closeScreen();
                }
            }
        });
    }

    private boolean isChest() {
        if(Minecraft.getMinecraft().currentScreen instanceof GuiChest) {
            GuiChest chestGui = (GuiChest) Minecraft.getMinecraft().currentScreen;
            String name = chestGui.lowerChestInventory.getDisplayName().getUnformattedText();
            return name.equals("Chest") || name.equals("Large Chest") || name.equals("") || name.contains("Chest");
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

}
